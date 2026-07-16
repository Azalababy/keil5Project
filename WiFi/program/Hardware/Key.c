#include "stm32f10x.h"                  // Device header
#include "Delay.h"


#include "FreeRTOS.h"
#include "queue.h"
#include "task.h"


#include "OLED.h"

uint8_t Key_Num;

 //定义队列
 QueueHandle_t g_xQueueKey; 


void Key_Init(void)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA,ENABLE);
	
	GPIO_InitTypeDef GPIO_InitStructure;
	
	GPIO_InitStructure.GPIO_Mode=GPIO_Mode_IPU;
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_1 ;
	GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
	
	GPIO_Init(GPIOB,&GPIO_InitStructure);
	
	GPIO_InitStructure.GPIO_Mode=GPIO_Mode_IPU;
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_4 | GPIO_Pin_6;
	GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
	
	GPIO_Init(GPIOA,&GPIO_InitStructure);
	
	//按键队列
	g_xQueueKey= xQueueCreate(10, sizeof(uint8_t));
}

//读取按键值
//uint8_t Key_GetNum(void)
//{
//	uint8_t Key_Num=0;
//	//读取特定端口值GPIO_ReadInputDataBit
//	//低电平点亮，按下按钮时为电平0
//	if(GPIO_ReadInputDataBit(GPIOB,GPIO_Pin_1)==0)
//	{
//		//消除按下抖动
//		Delay_ms(20);
//		//按键一直按下
//		while(GPIO_ReadInputDataBit(GPIOB,GPIO_Pin_1)==0);
//		//消除松手抖动
//		Delay_ms(20);
//		Key_Num=1;
//	}
//	
//	if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_6)==0)
//	{
//		//消除按下抖动
//		Delay_ms(20);
//		//按键一直按下
//		while(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_6)==0);
//		//消除松手抖动
//		Delay_ms(20);
//		Key_Num=2;
//	}
//	if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==0)
//	{
//		//消除按下抖动
//		Delay_ms(20);
//		//按键一直按下
//		while(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==0);
//		//消除松手抖动
//		Delay_ms(20);
//		Key_Num=3;
//	}
//	
//	return Key_Num;
//	
//}

//返回Key_Num
uint8_t Key_GetNum(void)
{
	uint8_t Temp;
	if(Key_Num)
	{
		Temp=Key_Num;
		Key_Num=0;
		
		
		return Temp;
	}
	else
	{
		return 0;
	}
}

int press_time; //按下时间
//检测Key3按下时间
void Key3_Tick(void)
{
	if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==0)
	{
		press_time++;
	}
	if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==1)
	{
		press_time=0;
	}
}

//因为当Key_GetNum读取键值时，按键一直按下会一直在while循环；阻塞了主程序时间；所以要使用中断

uint8_t Key_GetState(void)
{
	if(GPIO_ReadInputDataBit(GPIOB,GPIO_Pin_1)==0)
	{
		return 1;
	}
	
	else if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_6)==0)
	{
		return 2;
	}
	else if((GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==0)&&press_time>1000)
	{
		return 4;
	}
	else if(GPIO_ReadInputDataBit(GPIOA,GPIO_Pin_4)==0)
	{
		return 3;
	}
	else
	{
		return 0;
	}
	
	
}

//每当20ms读取一次按键值:如果上一次引脚是低电平（按了按键返回其中一个：1，2，3），本次引脚是高电平(Key_GetState返回0表示没有按按键)，就可以判断按键被按下了一次
void Key_Tick(void)
{
	static uint8_t Count;
	static uint8_t CurrentState,PreState;
	Count++;
	if(Count==20)
	{
		Count=0;
		PreState=CurrentState;
		CurrentState=Key_GetState();
		if(PreState!=0 && CurrentState==0)
		{
			Key_Num=PreState;
			//Key队列
			xQueueSendFromISR(g_xQueueKey, &Key_Num,NULL);
			
		}
		
	}
}



