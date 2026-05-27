#include "stm32f10x.h"                  // Device header
#include "MySPI.h"
#include "W25Q64_Ins.h"

void W25Q64_Init(void)
{
	MySPI_Init();
	
	
}

//第一个指针获取厂商ID,第二个获取寄存器设备ID 用于验证SPI
void W25Q64_ReadID(uint8_t *MID,uint16_t *DID)
{
	MySPI_Start();
	//发送9F就是读ID号的指令，下一次交换数据就可以将ID号返回给主机
	MySPI_SwapByte(W25Q64_JEDEC_ID);
	//目的接收，主机发0xFF实际上没有意义
	//这里调用两个相同的函数，但返回值是不一样的；因为这里是通信，通信的时序不一样，它的意义就不一样
	*MID= MySPI_SwapByte(W25Q64_DUMMY_BYTE);
	//高8位
	*DID= MySPI_SwapByte(W25Q64_DUMMY_BYTE);
	*DID <<=8;
	//低8位
	*DID |= MySPI_SwapByte(W25Q64_DUMMY_BYTE);
	MySPI_Stop();
}


//写使能
void W25Q64_WriteEnable(void)
{
	MySPI_Start();
	MySPI_SwapByte(W25Q64_WRITE_ENABLE);
	MySPI_Stop();
}

//读状态寄存器1：目的是判断芯片是不是busy
void W25Q64_WaitBusy(void)
{
	uint32_t Timeout;
	MySPI_Start();
	MySPI_SwapByte(W25Q64_READ_STATUS_REGISTER_1);
	Timeout=100000;
	//最低位存的是busy,取出来；1是忙，0是不忙
	while((MySPI_SwapByte(W25Q64_DUMMY_BYTE) & 0x01)==0x01)
	{
		Timeout--;
		if(Timeout==0)
		{
			break;
		}
	}
	MySPI_Stop();
}

//页编程
//Address :地址 ；DataArray：输入数据数组 ；Count：一次写多少个
void W25Q64_PageProgram(uint32_t Address,uint16_t *DataArray,uint16_t Count)
{
	uint16_t i;
	W25Q64_WriteEnable();
	MySPI_Start();
	MySPI_SwapByte(W25Q64_PAGE_PROGRAM); //发送指令
	MySPI_SwapByte(Address>>16); //0x123456 右移16位就是发12
	MySPI_SwapByte(Address>>8); //0x123456 右移8位就是发1234,但是MySPI_SwapByte交换字节只能接收8位数据,高位舍弃 ；实际发送0x34
	MySPI_SwapByte(Address); //MySPI_SwapByte交换字节只能接收8位数据,高位舍弃 ；实际发送0x56
	
	// 交错发送：高字节、低字节、高字节、低字节...
    for(i = 0; i < Count; i++) {
        MySPI_SwapByte(DataArray[i] >> 8);   // 高字节
        MySPI_SwapByte(DataArray[i] & 0xFF); // 低字节
    }
	MySPI_Stop();
	
	//事后等待
	W25Q64_WaitBusy();
}

//扇区擦除
void W25Q64_SectorErase(uint32_t Address)
{
	W25Q64_WriteEnable();
	MySPI_Start();
	MySPI_SwapByte(W25Q64_SECTOR_ERASE_4KB);
	MySPI_SwapByte(Address>>16); 
	MySPI_SwapByte(Address>>8); 
	MySPI_SwapByte(Address); 
	MySPI_Stop();
	
	W25Q64_WaitBusy();
}

//读取数据 DataArray输出数据
void W25Q64_ReadData(uint32_t Address,uint16_t *DataArray,uint32_t Count)
{
	uint16_t i;
	MySPI_Start();
	MySPI_SwapByte(W25Q64_READ_DATA); 
	MySPI_SwapByte(Address>>16); 
	MySPI_SwapByte(Address>>8); 
	MySPI_SwapByte(Address); 
	
	//读
	for(i=0;i<Count;i++)
	{
		uint16_t high = MySPI_SwapByte(W25Q64_DUMMY_BYTE);  // 先读高字节
        uint16_t low  = MySPI_SwapByte(W25Q64_DUMMY_BYTE);  // 再读低字节
        DataArray[i] = (high << 8) | low;
	}
	
	
	MySPI_Stop();
}




