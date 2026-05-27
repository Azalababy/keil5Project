#include "stm32f10x.h"                  // Device header
#include "Delay.h"

void Feng_Init(void)
{
	
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
	GPIO_InitTypeDef GPIO_InitStructure;
	//推挽输出
	GPIO_InitStructure.GPIO_Mode=GPIO_Mode_Out_PP;
	//配置某个 GPIO 端口（比如 GPIOA、GPIOB）的第 0 号引脚（Pin0）
	GPIO_InitStructure.GPIO_Pin=GPIO_Pin_11;
	//50Hz
	GPIO_InitStructure.GPIO_Speed=GPIO_Speed_50MHz;
	GPIO_Init(GPIOB,&GPIO_InitStructure);
	
	GPIO_SetBits(GPIOB,GPIO_Pin_11);
		
		
}

void Feng_ON(void)
{
	
		
		GPIO_ResetBits(GPIOB,GPIO_Pin_11); //0000 0000 0000 0001
		Delay_ms(10);
		GPIO_SetBits(GPIOB,GPIO_Pin_11); //0000 0000 0000 0010
		Delay_ms(10);
		GPIO_ResetBits(GPIOB,GPIO_Pin_11); //0000 0000 0000 0001
		Delay_ms(10);
		GPIO_SetBits(GPIOB,GPIO_Pin_11); //0000 0000 0000 0010
		Delay_ms(70);
		
	
}

void Feng_OFF(void)
{
	GPIO_SetBits(GPIOB,GPIO_Pin_11);
	
}
	






