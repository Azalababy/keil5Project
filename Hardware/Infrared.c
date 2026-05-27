#include "stm32f10x.h"                  // Device header
#include "Delay.h"

#include "LED.h"
#include "Delay.h"
#include "OLED.h"
#include "String.h"

#include "FreeRTOS.h"
#include "task.h"
#include "queue.h"

#define IR_BUF_LEN 16


 uint8_t ir_KeyNum;
 
 //定义队列
 QueueHandle_t g_xQueueIR; 



static uint32_t g_IR_Times[68];
static uint8_t g_IR_Cnt = 0;
static uint32_t g_IR_LastTime = 0;

static uint8_t g_IR_Buf[IR_BUF_LEN];
static uint8_t g_IR_Buf_R = 0;
static uint8_t g_IR_Buf_W = 0;

#define NEXT_POS(x) ((x + 1) % IR_BUF_LEN)

static uint8_t isBufEmpty(void)
{
    return (g_IR_Buf_R == g_IR_Buf_W);
}

static uint8_t isBufFull(void)
{
    return (g_IR_Buf_R == NEXT_POS(g_IR_Buf_W));
}

static void putKey(uint8_t key)
{
    if (!isBufFull())
    {
        g_IR_Buf[g_IR_Buf_W] = key;
        g_IR_Buf_W = NEXT_POS(g_IR_Buf_W);
    }
}

static uint8_t getKey(void)
{
    uint8_t key = 0xFF;
    if (!isBufEmpty())
    {
        key = g_IR_Buf[g_IR_Buf_R];
        g_IR_Buf_R = NEXT_POS(g_IR_Buf_R);
    }
    return key;
}

void IR_Init(void)
{
    RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA | RCC_APB2Periph_AFIO, ENABLE);
    RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM3, ENABLE);
    
    GPIO_InitTypeDef GPIO_InitStructure;
    GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
    GPIO_InitStructure.GPIO_Pin = GPIO_Pin_8;
    GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
    GPIO_Init(GPIOA, &GPIO_InitStructure);
    
    GPIO_EXTILineConfig(GPIO_PortSourceGPIOA, GPIO_PinSource8);
    
    EXTI_InitTypeDef EXTI_InitStructure;
    EXTI_InitStructure.EXTI_Line = EXTI_Line8;
    EXTI_InitStructure.EXTI_LineCmd = ENABLE;
    EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;
    EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Rising_Falling;
    EXTI_Init(&EXTI_InitStructure);
    
    NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
    
    NVIC_InitTypeDef NVIC_InitStructure;
    NVIC_InitStructure.NVIC_IRQChannel = EXTI9_5_IRQn;
    NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
    NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
    NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
    NVIC_Init(&NVIC_InitStructure);
    
    TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
    TIM_TimeBaseStructure.TIM_Period = 0xFFFF;
    TIM_TimeBaseStructure.TIM_Prescaler = 72 - 1;
    TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;
    TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;
	 TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure);
    
    TIM_Cmd(TIM3, ENABLE);
	
	//红外队列
	g_xQueueIR= xQueueCreate(10, sizeof(uint8_t));
}

static uint8_t parseData(void)
{
    uint32_t time;
    uint8_t i, byte = 0, bit = 0;
    uint8_t data[4] = {0};
    
    time = g_IR_Times[1] - g_IR_Times[0];
    if (time < 8000 || time > 10000)
    {
        return 1;
    }
    
    time = g_IR_Times[2] - g_IR_Times[1];
    if (time < 3500 || time > 5500)
    {
        return 1;
    }
    
    for (i = 0; i < 32; i++)
    {
        time = g_IR_Times[3 + i * 2 + 1] - g_IR_Times[3 + i * 2];
        data[byte] <<= 1;
        bit++;
        
        if (time > 1000 && time < 1800)
        {
            data[byte] |= 1;
        }
        else if (time < 400 || time > 700)
        {
            return 1;
        }
        
        if (bit == 8)
        {
            byte++;
            bit = 0;
        }
    }
    
    if ((data[0] != (uint8_t)~data[1]) || (data[2] != (uint8_t)~data[3]))
    {
        return 1;
    }
    
    putKey(data[0]);
    putKey(data[2]);
    
    return 0;
}

static uint8_t isRepeatCode(void)
{
    uint32_t time;
    
    time = g_IR_Times[1] - g_IR_Times[0];
    if (time < 8000 || time > 10000)
    {
        return 0;
    }
    
    time = g_IR_Times[2] - g_IR_Times[1];
    if (time < 2000 || time > 2500)
    {
        return 0;
    }
    
    return 1;
}

extern uint8_t stop;
extern uint32_t stop_flag;

void EXTI9_5_IRQHandler(void)
{
    if (EXTI_GetITStatus(EXTI_Line8) == SET)
    {
		stop=1;
		stop_flag=0;
		
        uint32_t now = TIM_GetCounter(TIM3);
        uint32_t time;
        
        if (now < (g_IR_LastTime & 0xFFFF))
        {
            g_IR_LastTime += 0x10000;
        }
        g_IR_LastTime = (g_IR_LastTime & 0xFFFF0000) | now;
        time = g_IR_LastTime;
        
        if (g_IR_Cnt > 0)
        {
            uint32_t diff = time - g_IR_Times[g_IR_Cnt - 1];
            if (diff > 100000)
            {
                g_IR_Cnt = 0;
            }
        }
        
        if (g_IR_Cnt < 68)
        {
            g_IR_Times[g_IR_Cnt] = time;
            g_IR_Cnt++;
        }
        
        if (g_IR_Cnt == 4)
        {
            if (isRepeatCode())
            {
                g_IR_Cnt = 0;
            }
        }
        
        if (g_IR_Cnt >= 68)
        {
            parseData();
            g_IR_Cnt = 0;
        }
        
        EXTI_ClearITPendingBit(EXTI_Line8);
    }
}

uint8_t IR_GetData(uint8_t *pDev, uint8_t *pKey)
{
    if (isBufEmpty())
    {
        return 0;
    }
    
    *pDev = getKey();
    *pKey = getKey();
    
    return 1;
}

uint8_t IR_HasData(void)
{
    return !isBufEmpty();
}

void IR_Test(void)
{
	uint8_t dev, key;
    static uint8_t last_key = 0xFF;   // 记录上一次映射后的菜单键值
	
	
	const uint8_t codes[]= {0xa2, 0xe2, 0x22, 0x02, 0xc2, 0xe0, 0xa8, 0x90, \
                            0x68, 0x98, 0xb0, 0x30, 0x18, 0x7a, 0x10, 0x38, \
                            0x5a, 0x42, 0x4a, 0x52, 0x00};
    char *names[]= {"Power", "Menu", "Test", "+", "Return", "Left", "Play", "Right", \
                            "0", "-", "C", "1", "2", "3", "4", "5", \
                            "6", "7", "8", "9", "Repeat"};
	
	int i;
	char *str="";
							
	
		if (IR_HasData())
		{
			// 将红外原始码转换为菜单键值 (1~4)
            uint8_t menu_key = 0;
			
			IR_GetData(&dev, &key);
    
					for (i = 0; i < sizeof(codes)/sizeof(codes[0]); i++)
					{
						if (key == codes[i])
						{
							if (strcmp(names[i], "1") == 0) {menu_key = 1;LED1_ON();}
							else if (strcmp(names[i], "2") == 0) {menu_key = 2;LED1_OFF();}
							else if (strcmp(names[i], "Play") == 0) menu_key = 3;
							// 可继续添加 Return 对应 4 等
							break;	
						}
					}
					
					
					
			 // 仅当键值有效，且当前 ir_KeyNum 已被菜单消费时，才设置新键
            if (menu_key != 0 && ir_KeyNum == 0)
            {
                ir_KeyNum = menu_key;
                last_key = menu_key;
				//将值写到输入设备队列
				xQueueSend(g_xQueueIR, &ir_KeyNum, NULL);
            }
            else if (menu_key == 0)
            {
                last_key = 0xFF;   // 无效键重置
            }
		
		}
}

int GetIRKey(void)
{
	int key = ir_KeyNum;
    ir_KeyNum = 0;  // 清除，避免重复读取
	
    return key;
}


