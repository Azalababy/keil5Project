#include "stm32f10x.h"                  // Device header

//由于SPI操作速度非常快，所以不用加延时
//从机选择  
void MySPI_W_SS(uint8_t BitValue)
{
	GPIO_WriteBit(GPIOB,GPIO_Pin_12,BitValue);
}


void MySPI_Init(void)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE);
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_SPI2, ENABLE);
	
	GPIO_InitTypeDef GPIO_InitStructure;
	//PA4(SS)还是用软件模拟
 	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12;
 	GPIO_Init(GPIOB, &GPIO_InitStructure);
	
	//复用推挽输出
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13 |GPIO_Pin_15;
 	GPIO_Init(GPIOB, &GPIO_InitStructure);
	
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_14;
 	GPIO_Init(GPIOB, &GPIO_InitStructure);
	
	SPI_InitTypeDef SPI_InitStructure;
	//主机还是从机
    SPI_InitStructure.SPI_Mode=SPI_Mode_Master;	
	//双线全双工
	SPI_InitStructure.SPI_Direction=SPI_Direction_2Lines_FullDuplex;	
	//8位数据帧
	SPI_InitStructure.SPI_DataSize=SPI_DataSize_8b;
	//高位先行
	SPI_InitStructure.SPI_FirstBit=SPI_FirstBit_MSB;	
	//分频  72MHz/128
	SPI_InitStructure.SPI_BaudRatePrescaler=SPI_BaudRatePrescaler_64;
	//时钟极性 （模式0）
	SPI_InitStructure.SPI_CPOL=SPI_CPOL_Low;	
	//第一个边沿移出，第二个边沿移入也叫采样
	SPI_InitStructure.SPI_CPHA=SPI_CPHA_1Edge;
	//软件NSS
	SPI_InitStructure.SPI_NSS=SPI_NSS_Soft;	
	//校验（不用）
	SPI_InitStructure.SPI_CRCPolynomial=7;
	SPI_Init(SPI2,&SPI_InitStructure);
	
	SPI_Cmd(SPI2,ENABLE);
	
	MySPI_W_SS(1);
	
}

//3个基本时序单元
void MySPI_Start(void)
{
	MySPI_W_SS(0);
}

void MySPI_Stop(void)
{
	MySPI_W_SS(1);
}

//法1：交换一个字节
uint8_t MySPI_SwapByte(uint8_t ByteSend)
{
	//p171
	//第一步：等待寄存器为空，不着急写
	//TXE 标志置 1（发送寄存器空）
	while(SPI_I2S_GetFlagStatus(SPI2,SPI_I2S_FLAG_TXE) !=SET);
	
	//写入数据寄存器，自动转入到移位寄存器,生成波形
	SPI_I2S_SendData(SPI2,ByteSend);
	
	//等待RXNE=1 ；表示收到一个字节
	while(SPI_I2S_GetFlagStatus(SPI2,SPI_I2S_FLAG_RXNE) !=SET);
	
	//读取数据
	return SPI_I2S_ReceiveData(SPI2);
	
}

//法2：更应和p154移位模型
//uint8_t MySPI_SwapByte(uint8_t ByteSend)
//{
//	uint8_t i;
//	for(i=0;i<8;i++)
//	{
//	MySPI_W_MOSI(ByteSend & 0x80);
//	//最高位左移出去，最低为自动补0
//	ByteSend<<=1;
//	MySPI_W_SCK(1);
//		//读进来如果是1，进入if放在最低位，如果是零则还是默认值，因为上面已经在最低为自动补0
//	if(MySPI_W_MISO()==1){ByteSend |= 0x01;}
//	MySPI_W_SCK(0);
//	}

//	
//	return ByteSend;
//}


