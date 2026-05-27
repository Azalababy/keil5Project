#include "stm32f10x.h"                  // Device header

uint16_t AD_Value[3];

void AD_Init(void)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_ADC1,ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA,ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);
	
	RCC_AHBPeriphClockCmd(RCC_AHBPeriph_DMA1,ENABLE);
	
	//最大14Hz,只能6分频或8分频
	RCC_ADCCLKConfig(RCC_PCLK2_Div6); //72/6=12MHz
	
	GPIO_InitTypeDef GPIO_InitStructure;
 	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Pin =GPIO_Pin_2 |GPIO_Pin_7;
 	GPIO_Init(GPIOA, &GPIO_InitStructure);
	
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Pin =GPIO_Pin_0;
 	GPIO_Init(GPIOB, &GPIO_InitStructure);
	
	//ADC_Channel_0：通道 ；1 ：菜单上的位置
	ADC_RegularChannelConfig(ADC1,ADC_Channel_2,1,ADC_SampleTime_55Cycles5);  //2:PA7
	ADC_RegularChannelConfig(ADC1,ADC_Channel_7,2,ADC_SampleTime_55Cycles5);  //7:PA7
	ADC_RegularChannelConfig(ADC1,ADC_Channel_8,3,ADC_SampleTime_55Cycles5);  //8:PB0

	
	
	//结构体初始化ADC
	ADC_InitTypeDef ADC_InitStructure;
	//选择ADC的工作模式（单ADC）
	ADC_InitStructure.ADC_Mode=ADC_Mode_Independent;
	//对齐方式（右对齐）
	ADC_InitStructure.ADC_DataAlign=ADC_DataAlign_Right;
	//外部触发源选择（none,选择内部触发）
	ADC_InitStructure.ADC_ExternalTrigConv=ADC_ExternalTrigConv_None;
	//连续转换模式(单次转换)
//	ADC_InitStructure.ADC_ContinuousConvMode=DISABLE;
//连续模式
ADC_InitStructure.ADC_ContinuousConvMode=ENABLE;
	//扫描转换模式（非扫描）
	ADC_InitStructure.ADC_ScanConvMode=ENABLE;
	//在扫描模式下，总共用几个通道（单次模式下只有1有用）
	ADC_InitStructure.ADC_NbrOfChannel=3;
	ADC_Init(ADC1,&ADC_InitStructure);
	
	//DMA
	DMA_InitTypeDef DMA_InitStructure;
	DMA_InitStructure.DMA_PeripheralBaseAddr=(uint32_t)&ADC1->DR;
	//获取DR低16位数据，半字16位转运模式
	DMA_InitStructure.DMA_PeripheralDataSize=DMA_PeripheralDataSize_HalfWord;
	DMA_InitStructure.DMA_PeripheralInc=DMA_PeripheralInc_Disable;
	
	DMA_InitStructure.DMA_MemoryBaseAddr=(uint32_t)AD_Value;
	DMA_InitStructure.DMA_MemoryDataSize=DMA_MemoryDataSize_HalfWord;
	DMA_InitStructure.DMA_MemoryInc=DMA_MemoryInc_Enable;
	
	//传输方向：外设站点是源
	DMA_InitStructure.DMA_DIR=DMA_DIR_PeripheralSRC;
	//传输计数器
	DMA_InitStructure.DMA_BufferSize=3;
	//是否使用自动重装(否)
	//DMA_InitStructure.DMA_Mode=DMA_Mode_Normal;
	//循环模式
DMA_InitStructure.DMA_Mode=DMA_Mode_Circular;
	//选择硬件触发还是软件触发(硬件触发)
	DMA_InitStructure.DMA_M2M=DMA_M2M_Disable;
	//优先级
	DMA_InitStructure.DMA_Priority=DMA_Priority_Medium;
	DMA_Init(DMA1_Channel1,&DMA_InitStructure);
	//DMA转运的3个条件：1、传输计数器>0 2、触发源有触发信号 3、DMA使能
	DMA_Cmd(DMA1_Channel1,ENABLE);
	
	//开启ADC到DMA的输出
	ADC_DMACmd(ADC1,ENABLE);
	//ADC上电
	ADC_Cmd(ADC1,ENABLE);
	
	//ADC校准（固定4个函数操作）
	//复位校准
	ADC_ResetCalibration(ADC1);
	//等待复位校准完成，如果没校准完成，一直空循环等待
	while(ADC_GetResetCalibrationStatus(ADC1)==SET);
	//开始校准
	ADC_StartCalibration(ADC1);
	//等待校准完成
	while(ADC_GetCalibrationStatus(ADC1)==SET);
	//连续转换
	//ADC_SoftwareStartConvCmd(ADC1,ENABLE);
	//ADC触发
ADC_SoftwareStartConvCmd(ADC1,ENABLE);
}

// void AD_GetValue(void)
//{
//	//因为DMA也是单次模式，所以在触发ADC之前要重新写入一下传输计数器
//	//关闭DMA
//	DMA_Cmd(DMA1_Channel1,DISABLE);
//	//赋值传输计数器
//	DMA_SetCurrDataCounter(DMA1_Channel1,4);
//	//开启DMA
//	DMA_Cmd(DMA1_Channel1,ENABLE);
//	//因为ADC还是单次模式，所以还要软件触发一下
//	//ADC_SoftwareStartConvCmd(ADC1,ENABLE);
//	
//	//等待转运完成
//	while(DMA_GetFlagStatus(DMA1_FLAG_TC1)==RESET);
//	
//	DMA_ClearFlag(DMA1_FLAG_TC1);
//	
//}

