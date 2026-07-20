#include "stm32f10x.h"                  // Device header
#include "OLED.h"
#include "MyRTC.h"                                                                                                                                                                                                     
#include "Key.h"
#include "LED.h"
#include "SetTime.h"
#include "MPU6050.h"
#include "Delay.h"
#include <math.h>
#include "dino.h"
#include "AD.h"
#include "Store.h"
#include "Infrared.h"

#include "FreeRTOS.h"
#include "task.h"
#include "queue.h"

//蓝牙
#include "Serial.h"
#include <stdio.h>
#include <string.h>
#include "LED.h"

//温湿度
#include "dht11.h"
#include "PWM.h"

//DMA
#include "AD.h"

//W25Q64
#include "W25Q64.h"

#include "feng.h"

#include "esp8266.h"

//存放按键值
 uint8_t KeyNum;
 
 uint8_t IRNum;
 
void Weather_Parse(void);
char climate[10];

void Peripheral_Init(void)
{
	PWM_Init();
	Store_Init();	
	MyRTC_Init();
	Key_Init();
	LED_Init();
	
	AD_Init();
	
	W25Q64_Init();
}

void Weather_Parse(void)
{
	switch (code)
	{
		case 0:strcpy (climate,"晴");break;
		case 1:strcpy (climate,"晴");break;
		case 2:strcpy (climate,"晴");break;
		case 3:strcpy (climate,"晴");break;
		case 4:strcpy (climate,"多云");break;
		case 5:strcpy (climate,"多云");break;
		case 6:strcpy (climate,"多云");break;
		case 7:strcpy (climate,"多云");break;
		case 8:strcpy (climate,"多云");break;
		case 9:strcpy (climate,"阴");break;
		case 10:strcpy (climate,"阵雨");break;
		case 11:strcpy (climate,"雷阵雨");break;
		case 12:strcpy (climate,"雷阵雨");break;
		case 13:strcpy (climate,"小雨");break;
		case 14:strcpy (climate,"中雨");break;
		case 15:strcpy (climate,"大雨");break;
		case 16:strcpy (climate,"暴雨");break;
		case 17:strcpy (climate,"大暴雨");break;
		case 18:strcpy (climate,"大暴雨");break;
		case 19:strcpy (climate,"冻雨");break;
		case 20:strcpy (climate,"雨夹雪");break;
		case 21:strcpy (climate,"阵雪");break;
		case 22:strcpy (climate,"小雪");break;
		case 23:strcpy (climate,"中雪");break;
		case 24:strcpy (climate,"大雪");break;
		case 25:strcpy (climate,"暴雪");break;
		case 30:strcpy (climate,"雾");break;
		case 31:strcpy (climate,"霾");break;
		case 32:strcpy (climate,"风");break;
		case 33:strcpy (climate,"大风");break;
		case 37:strcpy (climate,"冷");break;
		case 38:strcpy (climate,"热");break;
	}
}

//首页UI

void Show_First_UI(void)
{
	//左上角日期
//	MyRTC_ReadTime();
//	OLED_Printf(0,0,OLED_6X8,"%d-%d-%d",MyRTC_Time[0],MyRTC_Time[1],MyRTC_Time[2]);
//	
//	//中间时间

//	OLED_Printf(16,16,OLED_12X24,"%02d:%02d:%02d",MyRTC_Time[3],MyRTC_Time[4],MyRTC_Time[5]);
	
	//底下菜单和设置修改时间
	
	memset (climate,0,sizeof (climate));
	Weather_Parse();
	OLED_Printf(114,48,OLED_6X8,":");
	OLED_ShowString(79,48,"天气",OLED_8X16);
	OLED_ShowString(116,48,climate,OLED_8X16);
	OLED_ShowChar(102,0,':',OLED_8X16);
	OLED_ShowString(78,2,"星期",OLED_8X16);
	
	OLED_Printf (0,0,OLED_6X8,"%d-%d-%d",MyRTC_Time[0],MyRTC_Time[1],MyRTC_Time[2]);
	OLED_Printf (16,16,OLED_12X24,"%02d:%02d:%02d",MyRTC_Time [3],MyRTC_Time [4],MyRTC_Time [5]);
	if (MyRTC_Time [6]==0)
	{
		OLED_ShowNum(110,0,7,1,OLED_8X16);
	}
	else
	{
		OLED_ShowNum(110,0,MyRTC_Time [6],1,OLED_8X16);
	}		
	
	OLED_ShowString(0,48,"菜单",OLED_8X16);
	OLED_ShowString(38,48,"历史",OLED_8X16);
	//OLED_ShowString(96,48,"设置",OLED_8X16);
}



//平移动画


uint8_t First_flag=1;
int First_Show(void)
{
	
	
//		KeyNum=Key_GetNum();
//		IRNum=GetIRKey();
		
		
		if(Key_Num==1 || ir_KeyNum==1)
		{
			First_flag--;
			if(First_flag<=0)
			{
				First_flag=3;
			}
			
		}
		else if(Key_Num==2 || ir_KeyNum==2)
		{
			First_flag++;
			if(First_flag>=4)
			{
				First_flag=1;
			}
		}
		else if(Key_Num==3 || ir_KeyNum==3)
		{
			//进入
			OLED_Clear();
			OLED_Update();
			Key_Num=0;
			ir_KeyNum=0;
			return First_flag;
		}
		
		Key_Num=0;
		ir_KeyNum=0;
		
		switch(First_flag)
		{
			case 1: 
				Show_First_UI();
				OLED_ReverseArea(0,48,32,16);
				OLED_Update();
				break;
			case 2: 
				Show_First_UI();
				OLED_ReverseArea(38,48,32,16);
				OLED_Update();
				break;	
			case 3: 
				Show_First_UI();
				OLED_ReverseArea(80,48,48,16);
				OLED_Update();
				break;	
		}
		
		vTaskDelay(pdMS_TO_TICKS(20)); 
		
		
	
	
}



void Menu_Page1(void)
{
	OLED_Clear();
    OLED_ShowImage(0,0,16,16,Return);
    OLED_ShowString(0,24,"蓝牙接收",OLED_8X16);
    OLED_ShowString(0,48,"温湿度",OLED_8X16);
}

void Menu_Page2(void)
{
	OLED_Clear();
    OLED_ShowString(0,0,"DMA",OLED_8X16);
    OLED_ShowString(0,24,"W25",OLED_8X16);
    OLED_ShowString(0,48,"世界",OLED_8X16);
}

uint8_t Menu_flag=1;
int Menu(void)
{
	while(1)
	{
//		KeyNum=Key_GetNum();
//		IRNum=GetIRKey();
		
		uint8_t Menu_Temp_flag=0;
		if(Key_Num==1 || ir_KeyNum==1)
			{
				Menu_flag--;
				if(Menu_flag<=0)
				{
					Menu_flag=6;
				}
				
			}
			else if(Key_Num==2 || ir_KeyNum==2)
			{
				Menu_flag++;
				if(Menu_flag>=7)
				{
					Menu_flag=1;
				}
			}
			else if(Key_Num==3 || ir_KeyNum==3)
			{
				//进入
				Key_Num=0;
				ir_KeyNum=0;
				OLED_Clear();
				OLED_Update();
				
				Menu_Temp_flag=Menu_flag; 
				
			}
			
			if(Menu_Temp_flag==1){return 0;}
			else if(Menu_Temp_flag==2){SettingPage();}
			else if(Menu_Temp_flag==3){SettingDht();}
			else if(Menu_Temp_flag==4){SettingDMA();}
			else if(Menu_Temp_flag==5){SettingW25Q64();}
			else if(Menu_Temp_flag==6){}

			Key_Num=0;
			ir_KeyNum=0;
			
			switch(Menu_flag)
			{
				case 1:
					
					Menu_Page1();
					OLED_ReverseArea(0,0,16,16);
					OLED_Update();
					break;
				case 2:
					Menu_Page1();
					OLED_ReverseArea(0,24,64,16);
					OLED_Update();
					break;	
				case 3:
					
					Menu_Page1();
					OLED_ReverseArea(0,48,48,16);
					OLED_Update();
					break;	
				case 4:
					
					Menu_Page2();
					OLED_ReverseArea(0,0,24,16);
					OLED_Update();
					break;
				case 5:
					
					Menu_Page2();
					OLED_ReverseArea(0,24,24,16);
					OLED_Update();
					break;
				case 6:
					
					Menu_Page2();
					OLED_ReverseArea(0,48,32,16);
					OLED_Update();
					break;
			}
			
		vTaskDelay(pdMS_TO_TICKS(20)); 
	}
		
}


//-----------------------------蓝牙模块

int SettingPage(void)
{
	uint8_t temp;
	uint8_t humi;
	
	OLED_Clear();
	OLED_ShowString(0,0,"TxPacket",OLED_8X16);
	OLED_ShowString(0,32,"RxPacket",OLED_8X16);
	
	
	
	
	while(1)
	{		
		
		
		
		if(Key_Num==3 || ir_KeyNum==3)
		{
			
			OLED_Clear();
			OLED_Update();
			LED1_OFF();
			return 0;
		}
		
		Key_Num=0;
		ir_KeyNum=0;
		
		if(Serial_RxFlag==1)
		{
			//OLED是覆盖显示，如果旧数据比新数据长，旧的尾巴会显示出来
			OLED_ShowString(0,48,"                ",OLED_8X16);
			OLED_ShowString(0,48,USART1_RX_BUF,OLED_8X16);
			
			char * substr1=strtok(USART1_RX_BUF,",");
			
			if(strcmp(substr1,"LED")==0)
			{
				char * substr2=strtok(NULL,",");
				if(strcmp(substr2,"ON")==0)
				{
					LED1_ON();
					Serial_SendString(1,"LED_ON_OK\r\n");
					OLED_ShowString(0,16,"                ",OLED_8X16);
					OLED_ShowString(0,16,"LED_ON_OK",OLED_8X16);
				}
				else if(strcmp(substr2,"OFF")==0)
				{
					LED1_OFF();
					Serial_SendString(1,"LED_OFF_OK\r\n");
					OLED_ShowString(0,16,"                ",OLED_8X16);
					OLED_ShowString(0,16,"LED_OFF_OK",OLED_8X16);
				}
				else
				{
					Serial_SendString(1,"ERROR_COMMAND\r\n");
					OLED_ShowString(0,16,"                ",OLED_8X16);
					OLED_ShowString(0,16,"ERROR_COMMAND",OLED_8X16);
				}
			}
			else if(strcmp(substr1,"DHT")==0)//获取温湿度
			{
				char * substr2=strtok(NULL,",");
				if(strcmp(substr2,"ON")==0)
				{
					DHT11_Read_Data(&temp,&humi);
					Serial_SendString(1,"温度：");
					Serial_SendNumber(1,temp,2);
					Serial_SendString(1,"  C");
					Serial_SendString(1,"\r\n");
					Serial_SendString(1,"湿度：");
					Serial_SendNumber(1,humi,2);
					Serial_SendString(1,"  %");
					Serial_SendString(1,"\r\n");

					
					Serial_SendString(1,"DHT_ON_OK\r\n");
					OLED_ShowString(0,16,"                ",OLED_8X16);
					OLED_ShowString(0,16,"DHT_ON_OK",OLED_8X16);
				}
				else
				{
					Serial_SendString(1,"ERROR_COMMAND\r\n");
					OLED_ShowString(0,16,"                ",OLED_8X16);
					OLED_ShowString(0,16,"ERROR_COMMAND",OLED_8X16);
				}
			}
			
			
			Serial_RxFlag=0;
		}
		
		OLED_Update();
		vTaskDelay(pdMS_TO_TICKS(20)); 
		
	}
		
}

//-----------------------------温湿度模块



int SettingDht(void)
{
	uint8_t temp;
	uint8_t humi;
	int i;
	
	OLED_Clear();
	OLED_ShowString(0,0,"温度",OLED_8X16);
	OLED_ShowString(0,16,"湿度",OLED_8X16);
	
	OLED_ShowString(64,0,"C",OLED_8X16);
	OLED_ShowString(64,16,"%",OLED_8X16);
	
	
	while(1)
	{		
		
		if(Key_Num==3 || ir_KeyNum==3)
		{
			
			OLED_Clear();
			OLED_Update();
			return 0;
		}
		
		Key_Num=0;
		ir_KeyNum=0;
		
		
		DHT11_Read_Data(&temp,&humi);
		
		//显示温度数据
		OLED_ShowNum(40,0,temp,2,OLED_8X16);
		//显示湿度数据
		OLED_ShowNum(40,16,humi,2,OLED_8X16);
		
		
		
		OLED_Update();
		
		//温湿度过高时触发警报（呼吸灯）
		
		if(temp>=24 && humi>60)
		{
			for(i=0;i<=100;i++){
				PWM_SetCompare1(i);
				Delay_ms(10);
			}
			for(i=0;i<=100;i++){
				PWM_SetCompare1(100-i);
				Delay_ms(10);
			}
		}
		
	
		
		
		
		vTaskDelay(pdMS_TO_TICKS(20)); 
		
	}
		
}



//-----------------------------DMA模块

typedef struct Feng{
	int A;
	int B;
	int C;
} feng;

int SettingDMA(void)
{
	uint8_t temp;
	uint8_t humi;
	int i;
	
	feng AD;
	
	OLED_Clear();
	OLED_ShowString(0,0,"AD0:",OLED_8X16);
	OLED_ShowString(0,16,"AD1:",OLED_8X16);
	OLED_ShowString(0,32,"AD2:",OLED_8X16);
	
	
	while(1)
	{		
		
		
		if(Key_Num==3 || ir_KeyNum==3)
		{
			Key_Num=0;
			ir_KeyNum=0;
			OLED_Clear();
			OLED_Update();
			Feng_OFF();
			return 0;
		}
		
		//ADC连续扫描+DMA循环转运
		OLED_ShowNum(30,0,AD_Value[0],4,OLED_8X16);
		OLED_ShowNum(30,16,AD_Value[1],4,OLED_8X16);
		OLED_ShowNum(30,32,AD_Value[2],4,OLED_8X16);
		
		AD.A=AD_Value[0];
		AD.B=AD_Value[1];
		AD.C=AD_Value[2];
		
		//蜂鸣器报警
		if((AD.A<2000 && AD.B<2000) || AD.C<2000 )
		{
			Feng_ON();
		
		}
		
		OLED_Update();
		vTaskDelay(pdMS_TO_TICKS(20)); 
	}
		
}

//-----------------------------W25Q64模块



int SettingW25Q64(void)
{
	
	uint8_t MID;
	uint16_t DID;
	uint16_t ArrayRead[3];
	int i;

	OLED_Clear();
	
	
	while(1)
	{		
		
		
		if(Key_Num==3 || ir_KeyNum==3)
		{
			Key_Num=0;
			ir_KeyNum=0;
			OLED_Clear();
			OLED_Update();
			return 0;
		}
		
		
		//ADC连续扫描+DMA循环转运
//		OLED_ShowNum(30,0,AD_Value[0],4,OLED_8X16);
//		OLED_ShowNum(30,16,AD_Value[1],4,OLED_8X16);
//		OLED_ShowNum(30,32,AD_Value[2],4,OLED_8X16);
		
	
		
		OLED_ShowString(0,0,"MID:   DID:",OLED_8X16);
		OLED_ShowString(0,16,"W:",OLED_8X16);
		OLED_ShowString(0,32,"R:",OLED_8X16);
		
		W25Q64_ReadID(&MID,&DID);
		OLED_ShowHexNum(32,0,MID,2,OLED_8X16);
		OLED_ShowHexNum(96,0,DID,4,OLED_8X16);
		
		//写之前擦除扇区
		W25Q64_SectorErase(0x000000);
		//写入
		W25Q64_PageProgram(0x000000,AD_Value,3);
		W25Q64_ReadData(0x000000,ArrayRead,3);
		OLED_ShowNum(16,16,AD_Value[0],4,OLED_8X16);
		OLED_ShowNum(52,16,AD_Value[1],4,OLED_8X16);
		OLED_ShowNum(88,16,AD_Value[2],4,OLED_8X16);
		
		OLED_ShowNum(16,32,ArrayRead[0],4,OLED_8X16);
		OLED_ShowNum(52,32,ArrayRead[1],4,OLED_8X16);
		OLED_ShowNum(88,32,ArrayRead[2],4,OLED_8X16);
		
		OLED_Update();
		
		vTaskDelay(pdMS_TO_TICKS(20)); 
	}
		
}


////-----------------------------W25Q64模块



//int SettingW25Q64(void)
//{
//	
//	uint8_t MID;
//	uint16_t DID;
//	uint16_t ArrayRead[3];
//	int i;

//	OLED_Clear();
//	
//	
//	while(1)
//	{		
//		
//		
//		if(Key_Num==3 || ir_KeyNum==3)
//		{
//			Key_Num=0;
//			ir_KeyNum=0;
//			OLED_Clear();
//			OLED_Update();
//			return 0;
//		}
//		
//		
//		//ADC连续扫描+DMA循环转运
////		OLED_ShowNum(30,0,AD_Value[0],4,OLED_8X16);
////		OLED_ShowNum(30,16,AD_Value[1],4,OLED_8X16);
////		OLED_ShowNum(30,32,AD_Value[2],4,OLED_8X16);
//		
//	
//		
//		OLED_ShowString(0,0,"MID:   DID:",OLED_8X16);
//		OLED_ShowString(0,16,"W:",OLED_8X16);
//		OLED_ShowString(0,32,"R:",OLED_8X16);
//		
//		W25Q64_ReadID(&MID,&DID);
//		OLED_ShowHexNum(32,0,MID,2,OLED_8X16);
//		OLED_ShowHexNum(96,0,DID,4,OLED_8X16);
//		
//		//写之前擦除扇区
//		W25Q64_SectorErase(0x000000);
//		//写入
//		W25Q64_PageProgram(0x000000,AD_Value,3);
//		W25Q64_ReadData(0x000000,ArrayRead,3);
//		OLED_ShowNum(16,16,AD_Value[0],4,OLED_8X16);
//		OLED_ShowNum(52,16,AD_Value[1],4,OLED_8X16);
//		OLED_ShowNum(88,16,AD_Value[2],4,OLED_8X16);
//		
//		OLED_ShowNum(16,32,ArrayRead[0],4,OLED_8X16);
//		OLED_ShowNum(52,32,ArrayRead[1],4,OLED_8X16);
//		OLED_ShowNum(88,32,ArrayRead[2],4,OLED_8X16);
//		
//		OLED_Update();
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//		
//}




///*----------------------------------首页时钟-------------------------------------*/

//uint16_t ADValue;
//float VBAT;
//int Battery_Capacity;

////显示电池图标和电量
//void Show_Battery(void)
//{
//	int sum;
//	for(int i=0;i<3000;i++)
//	{
//		ADValue=AD_GetValue();
//		sum+=ADValue;
//		
//	}
//	ADValue=sum/3000;
//	VBAT=(float)ADValue/4095*3.3;
//	Battery_Capacity=(ADValue-3276)*100/819;
//	if(Battery_Capacity<0)Battery_Capacity=0;
//	
//	//OLED_ShowNum(64,0,ADValue,4,OLED_6X8);
//	//OLED_Printf(64,8,OLED_6X8,"VBAT:%.2f",VBAT);
//	OLED_ShowNum(85,4,Battery_Capacity,3,OLED_6X8);
//	OLED_ShowChar(103,4,'%',OLED_6X8);
//	
//	if(Battery_Capacity==100)OLED_ShowImage(110,0,16,16,Battery);
//	else if(Battery_Capacity>=10&&Battery_Capacity<100)
//	{
//		OLED_ShowImage(110,0,16,16,Battery);
//		OLED_ClearArea((112+Battery_Capacity/10),5,(10-Battery_Capacity/10),6);
//		OLED_ClearArea(85,4,6,8);
//	}
//	
//	else
//	{
//		OLED_ShowImage(110,0,16,16,Battery);
//		OLED_ClearArea(112,5,10,6);
//		OLED_ClearArea(85,4,12,8);
//	}
//};


////显示首页时钟UI
//void Show_Clock_UI(void)
//{
//	
//	Show_Battery();
//	MyRTC_ReadTime();
//	OLED_Printf(0,0,OLED_6X8,"%d-%d-%d",MyRTC_Time[0],MyRTC_Time[1],MyRTC_Time[2]);
//	OLED_Printf(16,16,OLED_12X24,"%02d:%02d:%02d",MyRTC_Time[3],MyRTC_Time[4],MyRTC_Time[5]);
//	OLED_ShowString(0,48,"菜单",OLED_8X16);
//	OLED_ShowString(96,48,"设置",OLED_8X16);
//	
//}

//int clkflag=1;//首页时钟按键标志位，赋初值为1，光标一进来停在第一项

////控制光标在首页时钟移动的函数
//int First_Page_Clock(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();

//		if(KeyNum==1)//上一项
//		{
//			clkflag--;
//			if(clkflag<=0)clkflag=2;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			clkflag++;
//			if(clkflag>=3)clkflag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			return clkflag;
//		}
//		
//		else if(KeyNum==4)
//		{
//			GPIO_ResetBits(GPIOB, GPIO_Pin_13);//长按Key3,KeyNum等于4，拉低CTL引脚（PB13),单片机关机
//			GPIO_SetBits(GPIOB, GPIO_Pin_12);//拉高BAT_ADC_EN引脚（PB12),ADC检测电路断开
//		};
//		switch(clkflag)
//		{
//			case 1:
//				Show_Clock_UI();
//				OLED_ReverseArea(0,48,32,16);
//				OLED_Update();
//				break;
//			
//			case 2:
//				Show_Clock_UI();
//				OLED_ReverseArea(96,48,32,16);
//				OLED_Update();
//				break;
//		}
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------设置界面-------------------------------------*/

////显示设置界面UI
//void Show_SettingPage_UI(void)
//{
//	OLED_ShowImage(0,0,16,16,Return);
//	OLED_ShowString(0,16,"日期时间设置",OLED_8X16);
//}

//int setflag=1;
////控制光标在设置界面移动的函数
//int SettingPage(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		uint8_t setflag_temp=0;
//		if(KeyNum==1)//上一项
//		{
//			setflag--;
//			if(setflag<=0)setflag=2;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			setflag++;
//			if(setflag>=3)setflag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			setflag_temp=setflag;
//		}
//		
//		if(setflag_temp==1){return 0;}
//		else if(setflag_temp==2){SetTime();}//跳转到日期时间设置界面
//		
//		switch(setflag)
//		{
//			case 1:
//				Show_SettingPage_UI();
//				OLED_ReverseArea(0,0,16,16);
//				OLED_Update();
//				break;
//			
//			case 2:
//				Show_SettingPage_UI();
//				OLED_ReverseArea(0,16,96,16);
//				OLED_Update();
//				break;
//		}
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------滑动菜单界面-------------------------------------*/

//uint8_t pre_selection;//上次选择的选项
//uint8_t target_selection;//目标选项
//uint8_t x_pre=48;//上次选项的x坐标
//uint8_t Speed=4;//速度
//uint8_t move_flag;//开始移动的标志位，1表示开始移动，0表示停止移动


////滑动菜单动画函数
//void Menu_Animation(void)
//{
//	OLED_Clear();
//	OLED_ShowImage(42,10,44,44,Frame);
//	
//	if(pre_selection<target_selection)
//	{
//		x_pre-=Speed;
//		if(x_pre==0)
//		{
//			pre_selection++;
//			move_flag=0;
//			x_pre=48;
//		}
//	}
//	
//	if(pre_selection>target_selection)
//	{
//		x_pre+=Speed;
//		if(x_pre==96)
//		{
//			pre_selection--;
//			move_flag=0;
//			x_pre=48;
//		}
//	}
//	
//	if(pre_selection>=1)
//	{
//		OLED_ShowImage(x_pre-48,16,32,32,Menu_Graph[pre_selection-1]);
//	}
//	
//	if(pre_selection>=2)
//	{
//		OLED_ShowImage(x_pre-96,16,32,32,Menu_Graph[pre_selection-2]);
//	}
//	
//	OLED_ShowImage(x_pre,16,32,32,Menu_Graph[pre_selection]);
//	OLED_ShowImage(x_pre+48,16,32,32,Menu_Graph[pre_selection+1]);
//	OLED_ShowImage(x_pre+96,16,32,32,Menu_Graph[pre_selection+2]);
//	
//	OLED_Update();
//}


////选择移动方向的函数
//void Set_Selection(uint8_t move_flag,uint8_t Pre_Selection,uint8_t Target_Selection)
//{
//	if(move_flag==1)
//	{
//		pre_selection=Pre_Selection;
//		target_selection=Target_Selection;
//		
//	}
//	Menu_Animation();
//}

////滑动菜单到具体功能的转场函数（下移转场）
//void MenuToFunction(void)
//{
//	for(uint8_t i=0;i<=6;i++)//每个循环向下移8格，六个循环过后完全移出屏幕
//	{
//		OLED_Clear();
//			if(pre_selection>=1)
//		{
//			OLED_ShowImage(x_pre-48,16+8*i,32,32,Menu_Graph[pre_selection-1]);
//		}
//		
//		
//		OLED_ShowImage(x_pre,16+8*i,32,32,Menu_Graph[pre_selection]);
//		OLED_ShowImage(x_pre+48,16+8*i,32,32,Menu_Graph[pre_selection+1]);
//		
//		OLED_Update();
//	}
//	
//}


//uint8_t menu_flag=1;
////控制图标在滑动菜单界面移动的函数
//int Menu(void)
//{
//	move_flag=1;
//	uint8_t DirectFlag=2;//置1：移动到上一项；置2：移动到下一项
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		uint8_t menu_flag_temp=0;
//		if(KeyNum==1)//上一项
//		{
//			DirectFlag=1;
//			move_flag=1;
//			menu_flag--;
//			if(menu_flag<=0)menu_flag=7;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			DirectFlag=2;
//			move_flag=1;
//			menu_flag++;
//			if(menu_flag>=8)menu_flag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			menu_flag_temp=menu_flag;
//		}
//		
//		if(menu_flag_temp==1){return 0;}
//		else if(menu_flag_temp==2){MenuToFunction();StopWatch();}//跳转到秒表界面
//		else if(menu_flag_temp==3){MenuToFunction();LED();}//跳转到手电筒界面
//		else if(menu_flag_temp==4){MenuToFunction();MPU6050();}//跳转到MPU6050界面
//		else if(menu_flag_temp==5){MenuToFunction();Game();}//跳转到游戏界面
//		else if(menu_flag_temp==6){MenuToFunction();Emoji();}//跳转到动态表情包界面
//		else if(menu_flag_temp==7){MenuToFunction();Gradienter();}//跳转到水平仪界面
//			

//			if(menu_flag==1)
//			{
//				if(DirectFlag==1)Set_Selection(move_flag,1,0);
//				else if(DirectFlag==2)Set_Selection(move_flag,0,0);
//			}
//			
//			else
//			{
//				if(DirectFlag==1)Set_Selection(move_flag,menu_flag,menu_flag-1);
//				else if(DirectFlag==2)Set_Selection(move_flag,menu_flag-2,menu_flag-1);
//			}
//			
//			
//			vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------秒表-------------------------------------*/

//uint8_t hour,min,sec;
////显示秒表界面的函数
//void Show_StopWatch_UI(void)
//{
//	OLED_ShowImage(0,0,16,16,Return);
//	OLED_Printf(32,20,OLED_8X16,"%02d:%02d:%02d",hour,min,sec);
//	OLED_ShowString(8,44,"开始",OLED_8X16);
//	OLED_ShowString(48,44,"停止",OLED_8X16);
//	OLED_ShowString(88,44,"清除",OLED_8X16);
//}

//uint8_t start_timing_flag;//1：开始，0：停止

////显示计时器的函数
//void StopWatch_Tick(void)
//{
//	static uint16_t Count;
//	Count++;
//	if(Count>=1000)
//	{
//		Count=0;
//			if(start_timing_flag==1)
//		{
//			sec++;
//			if(sec>=60)
//			{
//				sec=0;
//				min++;
//				if(min>=60)
//				{
//					min=0;
//					hour++;
//					if(hour>99)hour=0;
//				}
//			}
//		}
//	}
//	
//}


//uint8_t stopwatch_flag=1;
////控制光标在秒表界面移动的函数
//int StopWatch(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		uint8_t stopwatch_flag_temp=0;
//		if(KeyNum==1)//上一项
//		{
//			stopwatch_flag--;
//			if(stopwatch_flag<=0)stopwatch_flag=4;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			stopwatch_flag++;
//			if(stopwatch_flag>=5)stopwatch_flag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			stopwatch_flag_temp=stopwatch_flag;
//		}
//		
//		if(stopwatch_flag_temp==1){return 0;}
//		
//		
//		switch(stopwatch_flag)
//		{
//			case 1:
//				Show_StopWatch_UI();
//				OLED_ReverseArea(0,0,16,16);
//				OLED_Update();
//				break;
//			
//			case 2:
//				Show_StopWatch_UI();
//				start_timing_flag=1;
//				OLED_ReverseArea(8,44,32,16);
//				OLED_Update();
//				break;
//			
//			case 3:
//				Show_StopWatch_UI();
//				start_timing_flag=0;
//				OLED_ReverseArea(48,44,32,16);
//				OLED_Update();
//				break;
//			
//			case 4:
//				Show_StopWatch_UI();
//				start_timing_flag=0;
//				hour=min=sec=0;
//				OLED_ReverseArea(88,44,32,16);
//				OLED_Update();
//				break;
//		}
//		
//		vTaskDelay(pdMS_TO_TICKS(20));  
//	}
//}

///*----------------------------------手电筒-------------------------------------*/

////显示手电筒界面的函数
//void Show_LED_UI(void)
//{
//	OLED_ShowImage(0,0,16,16,Return);
//	OLED_ShowString(20,20,"OFF",OLED_12X24);
//	OLED_ShowString(72,20,"ON",OLED_12X24);
//}

//uint8_t led_flag=1;
////控制光标在手电筒界面移动的函数
//int LED(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		uint8_t led_flag_temp=0;
//		if(KeyNum==1)//上一项
//		{
//			led_flag--;
//			if(led_flag<=0)led_flag=3;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			led_flag++;
//			if(led_flag>=4)led_flag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			led_flag_temp=led_flag;
//		}
//		
//		if(led_flag_temp==1){return 0;}
//		
//		
//		switch(led_flag)
//		{
//			case 1:
//				Show_LED_UI();
//				OLED_ReverseArea(0,0,16,16);
//				OLED_Update();
//				break;
//			
//			case 2:
//				Show_LED_UI();
//				LED_OFF();
//				OLED_ReverseArea(20,20,36,24);
//				OLED_Update();
//				break;
//			
//			case 3:
//				Show_LED_UI();
//				LED_ON();
//				OLED_ReverseArea(72,20,24,24);
//				OLED_Update();
//				break;
//			
//		
//		}
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------MPU6050-------------------------------------*/

//int16_t ax,ay,az,gx,gy,gz;//MPU6050测得的三轴加速度和角速度
//float roll_g,pitch_g,yaw_g;//陀螺仪解算的欧拉角
//float roll_a,pitch_a;//加速度计解算的欧拉角
//float Roll,Pitch,Yaw;//互补滤波后的欧拉角
//float a=0.9;//互补滤波器系数
//float Delta_t=0.005;//采样周期
//double pi=3.1415927;

////通过MPU6050的数据进行姿态解算的函数
//void MPU6050_Calculation(void)
//{
//	
//	MPU6050_GetData(&ax,&ay,&az,&gx,&gy,&gz);
//	
//	//通过陀螺仪解算欧拉角
//	roll_g=Roll+(float)gx*Delta_t;
//	pitch_g=Pitch+(float)gy*Delta_t;
//	yaw_g=Yaw+(float)gz*Delta_t;
//	
//	//通过加速度计解算欧拉角
//	pitch_a=atan2((-1)*ax,az)*180/pi;
//	roll_a=atan2(ay,az)*180/pi;
//	
//	//通过互补滤波器进行数据融合
//	Roll=a*roll_g+(1-a)*roll_a;
//	Pitch=a*pitch_g+(1-a)*pitch_a;
//	Yaw=a*yaw_g;
//	
//	
//}

////显示MPU6050界面的UI
//void Show_MPU6050_UI(void)
//{

//		OLED_ShowImage(0,0,16,16,Return);
//		OLED_Printf(0,16,OLED_8X16,"Roll: %.2f",Roll);
//		OLED_Printf(0,32,OLED_8X16,"Pitch:%.2f",Pitch);
//		OLED_Printf(0,48,OLED_8X16,"Yaw:  %.2f",Yaw);
//		
//}

////控制光标在MPU6050界面移动的函数
//int MPU6050(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		if(KeyNum==3)
//		{
//			OLED_Clear();
//			OLED_Update();
//			return 0;
//		}
//		
//		OLED_Clear();
//		MPU6050_Calculation();
//		Show_MPU6050_UI();
//		OLED_ReverseArea(0,0,16,16);
//		OLED_Update();
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------游戏-------------------------------------*/

//void Show_Game_UI(void)
//{
//	OLED_ShowImage(0,0,16,16,Return);
//	OLED_ShowString(0,16,"谷歌小恐龙",OLED_8X16);
//}

//uint8_t game_flag=1;
////控制光标在游戏选择界面移动的函数
//int Game(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		uint8_t game_flag_temp=0;
//		if(KeyNum==1)//上一项
//		{
//			game_flag--;
//			if(game_flag<=0)game_flag=2;
//		}
//		else if(KeyNum==2)//下一项
//		{
//			game_flag++;
//			if(game_flag>=3)game_flag=1;
//		}
//		else if(KeyNum==3)//确认
//		{
//			OLED_Clear();
//			OLED_Update();
//			game_flag_temp=game_flag;
//		}
//		
//		if(game_flag_temp==1){return 0;}
//		else if(game_flag_temp==2){DinoGame_Pos_Init();DinoGame_Animation();}
//		
//		switch(game_flag)
//		{
//			case 1:
//				Show_Game_UI();
//				OLED_ReverseArea(0,0,16,16);
//				OLED_Update();
//				break;
//			
//			case 2:
//				Show_Game_UI();
//				LED_OFF();
//				OLED_ReverseArea(0,16,80,16);
//				OLED_Update();
//				break;
//			
//			
//		
//		}
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}

///*----------------------------------动态表情包-------------------------------------*/

////显示动态表情包
//void Show_Emoji_UI(void)
//{
//	/*闭眼*/
//	for(uint8_t i=0;i<3;i++)
//	{
//		OLED_Clear();
//		OLED_ShowImage(30,10+i,16,16,Eyebrow[0]);//左眉毛
//		OLED_ShowImage(82,10+i,16,16,Eyebrow[1]);//右眉毛
//		OLED_DrawEllipse(40,32,6,6-i,1);//左眼
//		OLED_DrawEllipse(88,32,6,6-i,1);//右眼
//		OLED_ShowImage(54,40,20,20,Mouth);
//		OLED_Update();
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//	
//	/*睁眼*/
//	for(uint8_t i=0;i<3;i++)
//	{
//		OLED_Clear();
//		OLED_ShowImage(30,12-i,16,16,Eyebrow[0]);//左眉毛
//		OLED_ShowImage(82,12-i,16,16,Eyebrow[1]);//右眉毛
//		OLED_DrawEllipse(40,32,6,4+i,1);//左眼
//		OLED_DrawEllipse(88,32,6,4+i,1);//右眼
//		OLED_ShowImage(54,40,20,20,Mouth);
//		OLED_Update();
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//	
//	vTaskDelay(pdMS_TO_TICKS(20)); 
//	
//}

////用按键控制退出动态表情包界面的函数
//int Emoji(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		if(KeyNum==3)
//		{
//			OLED_Clear();
//			OLED_Update();
//			return 0;
//		}
//		
//		Show_Emoji_UI();
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//		
//	}
//}

///*----------------------------------水平仪-------------------------------------*/
////显示水平仪
//void Show_Gradienter_UI(void)
//{
//	MPU6050_Calculation();
//	OLED_DrawCircle(64,32,30,0);
//	OLED_DrawCircle(64-Roll,32+Pitch,4,1);
//}

////用按键控制退出水平仪界面的函数
//int Gradienter(void)
//{
//	while(1)
//	{
//		KeyNum=GetIRKey();
//		if(KeyNum==3)
//		{
//			OLED_Clear();
//			OLED_Update();
//			return 0;
//		}
//		OLED_Clear();
//		Show_Gradienter_UI();
//		OLED_Update();
//		
//		vTaskDelay(pdMS_TO_TICKS(20)); 
//	}
//}


