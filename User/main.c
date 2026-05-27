#include "stm32f10x.h"                  // Device header
#include "Delay.h"
#include "OLED.h"
#include "menu.h"
#include "Timer.h"
#include "Key.h"

#include "LED.h"

#include "Dino.h"

#include "FreeRTOS.h"
#include "task.h"
#include "queue.h"

#include "Infrared.h"

#include "DWT.h"

#include "feng.h"
#include "Serial.h"

#include "MyRTC.h"
#include "esp8266.h"
#include <string.h>

/* 自定义*/

/*开始任务优先级*/
#define START_TASK_PRIO		1
/*开始任务堆栈大小*/
#define START_STK_SIZE 		128  
/*开始任务句柄*/
TaskHandle_t StartTask_Handler;
/*开始任务函数*/
void Start_Task(void *pvParameters);

/*OLED任务*/
#define OledShow_TASK_PRIO		5		/*OLED显示任务*/
#define OledShow_STK_SIZE 		128
TaskHandle_t OledShowTask_Handler;
void OledShow_Task(void *pvParameters);

/*IR任务*/
#define IR_TASK_PRIO		4		/*IR显示任务*/
#define IR_STK_SIZE 		128
TaskHandle_t IR_Handler;
void IR_Task(void *pvParameters);

/*Wifi任务*/
#define Wifi_TASK_PRIO		4		/*Wifi任务*/
#define Wifi_STK_SIZE 		128
TaskHandle_t Wifi_Handler;
void Wifi_Task(void *pvParameters);

/*Input任务*/
#define Input_TASK_PRIO		4		/*Input显示任务*/
#define Input_STK_SIZE 		128
TaskHandle_t Input_Handler;
void Input_Task(void *pvParameters);





//存放按键值
uint8_t g_KeyNum;

static int clkflag1;
static int setflag1;



/**
  *       0             X??           127 
  *      .------------------------------->
  *    0 |
  *      |
  *      |
  *      |
  *  Y?? |
  *      |
  *      |
  *      |
  *   63 |
  *      v
  * 
  */
  
//OLED任务 
uint8_t stop=0;
void OledShow_Task(void *pvParameters)
{
	extern int First_Show(void);
	
	while(1)
	{
		//OLED_Clear();
		
		//上电进入睡眠模式
		if(stop==0)
		{
			OLED_Clear();
			OLED_ShowString(0,0,"STOP",OLED_8X16);
			OLED_Update();
			__WFI();
		}
		else if(stop==1)
		{
			OLED_Clear();
			clkflag1=First_Show();
			if(clkflag1==1)
			{
				//进入菜单
				Menu();
			}
			else if(clkflag1==2)
			{
				
			}
			else if(clkflag1==3)
			{
				
			}
			
			OLED_Update();
		}
		
		
		
		
		
		vTaskDelay(pdMS_TO_TICKS(20)); 
	}
		
		
		 

	
}

//红外左右移动任务
void IR_Task(void *pvParameters)
{
	
	while(1)
	{
		
		IR_Test();
		vTaskDelay(pdMS_TO_TICKS(20)); 
	}
		  
}



//wifi获取时间任务
void Wifi_Task(void *pvParameters)
{
		
		while (1)
		{
			MyRTC_ReadTime();
				
			
			
			vTaskDelay(pdMS_TO_TICKS(20)); 
		}
		
		  
}











int main(void)
{
	
	/*外设初始化*/
	IR_Init();
	Peripheral_Init();
	LED_Init();
	
	Timer_Init();
	Key_Init();
	
	/*OLED初始化*/
	OLED_Init();
	
	DWT_Init();
	Feng_Init();
	
	ESP8266_Init ();
	Get_Time ();
	Get_Weather ();
	RTC_SetCounter(time/1000);

	
	
//	int clkflag1;
//	int setflag1;
	
//	extern int press_time;
//	extern uint8_t Key_Num;
	
	/*创建开始任务*/
    pdPASS==xTaskCreate((TaskFunction_t )Start_Task,            
                (const char*    )"Start_Task",          
                (uint16_t       )START_STK_SIZE,        
                (void*          )NULL,                  
                (UBaseType_t    )START_TASK_PRIO,       
                (TaskHandle_t*  )&StartTask_Handler);
	
	
	/*启动任务调度器*/
    vTaskStartScheduler();
	
	
	
//	while (1)
//	{
//		clkflag1=First_Show();
//		if(clkflag1==1)
//		{
//			
//			Menu();
//		}
//		else if(clkflag1==2)
//		{
////			
////			SettingPage();
//			
//		}
//		
//	}


}


void Start_Task(void *pvParameters)
{
	taskENTER_CRITICAL();		/*进入临界区*/
	
	
	/*OLED任务*/
	xTaskCreate((TaskFunction_t )OledShow_Task,     
                (const char*    )"OledShow_Task",   
                (uint16_t       )OledShow_STK_SIZE, 
                (void*          )NULL,
                (UBaseType_t    )OledShow_TASK_PRIO,
                (TaskHandle_t*  )&OledShowTask_Handler);
				
	
//	/*IR任务*/
	xTaskCreate((TaskFunction_t )IR_Task,     
                (const char*    )"IR_Task",   
                (uint16_t       )IR_STK_SIZE, 
                (void*          )NULL,
                (UBaseType_t    )IR_TASK_PRIO,
                (TaskHandle_t*  )&IR_Handler);
				
	//wifi任务
	xTaskCreate((TaskFunction_t )Wifi_Task,     
                (const char*    )"Wifi_Task",   
                (uint16_t       )Wifi_STK_SIZE, 
                (void*          )NULL,
                (UBaseType_t    )Wifi_TASK_PRIO,
                (TaskHandle_t*  )&Wifi_Handler);	
				
	//输入设备任务
//	xTaskCreate((TaskFunction_t )Input_Task,     
//                (const char*    )"Input_Task",   
//                (uint16_t       )Input_STK_SIZE, 
//                (void*          )NULL,
//                (UBaseType_t    )Input_TASK_PRIO,
//                (TaskHandle_t*  )&Input_Handler);

	
	taskEXIT_CRITICAL();		/*退出临界区*/					
	/*删除开始任务*/
	vTaskDelete(StartTask_Handler); 
	

				
}





uint32_t stop_flag=0;
void TIM2_IRQHandler(void)
{
	
	
	
	if (TIM_GetITStatus(TIM2, TIM_IT_Update) == SET)
	{
		//5分钟无操作进入睡眠模式
		stop_flag++;
		if(stop_flag>=300000)
		{
			stop_flag=0;
			
			OLED_Clear();
			OLED_ShowString(0,0,"STOP",OLED_8X16);
			OLED_Update();
			
			__WFI();
		}
		
		Key3_Tick();
		
		Key_Tick();
		IR_Test();
		TIM_ClearITPendingBit(TIM2, TIM_IT_Update);
	}
}









