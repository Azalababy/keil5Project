#include "stm32f10x.h"                  // Device header


void PWM_Init(void)
{
	//TIM2是APB1的外设
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM2,ENABLE);
	
	//使用重映射 将PA0重映射到PA15(如果想让PA15，PB3，PB4这三个引脚当作GPIO使用，就加上第一第三句)
	//打开AFIO时钟
//	RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO,ENABLE);
//	//重映射定时器或其他外设的服用引脚
//	GPIO_PinRemapConfig(GPIO_PartialRemap1_TIM2,ENABLE);
//	//解除JTAG复用
//	GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable,ENABLE);
	
	//GPIO初始化
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA,ENABLE);
	GPIO_InitTypeDef GPIO_InitStructure;
	GPIO_InitStructure.GPIO_Mode=GPIO_Mode_AF_PP;
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_0;
	//GPIO_InitStructure.GPIO_Pin=GPIO_Pin_15;
	GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
	GPIO_Init(GPIOA,&GPIO_InitStructure);
	
	
	//配置 PA4 (AIN1) 为推挽输出，初始低电平 -- 电机方向控制
	GPIO_InitStructure.GPIO_Mode=GPIO_Mode_Out_PP;
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_4;
	GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
	GPIO_Init(GPIOA,&GPIO_InitStructure);
	GPIO_ResetBits(GPIOA,GPIO_Pin_4);  //AIN1=0

	//配置 PA5 (AIN2) 为推挽输出，初始低电平 -- 电机方向控制
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_5;
	GPIO_Init(GPIOA,&GPIO_InitStructure);
	GPIO_ResetBits(GPIOA,GPIO_Pin_5);  //AIN2=0
	
	
	
	//选择内部时钟
	TIM_InternalClockConfig(TIM2);
	//初始化时基单元
	TIM_TimeBaseInitTypeDef TIM_InitStructure;
	TIM_InitStructure.TIM_ClockDivision=TIM_CKD_DIV1;
	//向上计数
	TIM_InitStructure.TIM_CounterMode=TIM_CounterMode_Up;
	TIM_InitStructure.TIM_Period=100-1;  //ARR
	TIM_InitStructure.TIM_Prescaler=720-1; //PSC
	TIM_InitStructure.TIM_RepetitionCounter=0;
	TIM_TimeBaseInit(TIM2,&TIM_InitStructure);
	
	//初始化输出比较单元
	TIM_OCInitTypeDef TIM_OCInitStructure;
	//结构体赋初始值
	TIM_OCStructInit(&TIM_OCInitStructure);
	//设置输出比较模式为TIM_OCMode_PWM1
	TIM_OCInitStructure.TIM_OCMode=TIM_OCMode_PWM1;
	//极性选择为高极性（REF有效时输出高电平）
	TIM_OCInitStructure.TIM_OCPolarity=TIM_OCNPolarity_High;
	//输出使能
	TIM_OCInitStructure.TIM_OutputState=TIM_OutputState_Enable;
	//设置CCR的值
	TIM_OCInitStructure.TIM_Pulse=0; //CRR
	TIM_OC1Init(TIM2,&TIM_OCInitStructure);
	
	
	
	//启动定时器
	TIM_Cmd(TIM2,ENABLE);
	
	
	
}

void PWM_SetCompare1(uint16_t Compare)
{
	//设置定时器 TIM2 的通道 1 的比较寄存器（CCR）的值
	TIM_SetCompare1(TIM2,Compare);
}



