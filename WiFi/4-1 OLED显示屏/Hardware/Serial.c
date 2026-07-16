#include "stm32f10x.h"
#include "Serial.h"
#include <stdio.h>
#include <stdarg.h>

char USART1_RX_BUF[1024] = {0};
uint16_t USART1_RX_LEN = 0;
char USART2_RX_BUF[1024] = {0};
uint16_t USART2_RX_LEN = 0;

void Serial_Init(uint8_t USART_Name)
{
    if (USART_Name == 1)
    {
        RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1, ENABLE);
        RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);

        GPIO_InitTypeDef GPIO_InitStructure;
        GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
        GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;
        GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
        GPIO_Init(GPIOA, &GPIO_InitStructure);

        GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
        GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;
        GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
        GPIO_Init(GPIOA, &GPIO_InitStructure);

        USART_InitTypeDef USART_InitStructure;
        USART_InitStructure.USART_BaudRate = 9600;          // 蓝牙波特率
        USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
        USART_InitStructure.USART_Mode = USART_Mode_Tx | USART_Mode_Rx;
        USART_InitStructure.USART_Parity = USART_Parity_No;
        USART_InitStructure.USART_StopBits = USART_StopBits_1;
        USART_InitStructure.USART_WordLength = USART_WordLength_8b;
        USART_Init(USART1, &USART_InitStructure);

        USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);

        NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
        NVIC_InitTypeDef NVIC_InitStructure;
        NVIC_InitStructure.NVIC_IRQChannel = USART1_IRQn;
        NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
        NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
        NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
        NVIC_Init(&NVIC_InitStructure);

        USART_Cmd(USART1, ENABLE);
    }
    else if (USART_Name == 2)
    {
        RCC_APB1PeriphClockCmd(RCC_APB1Periph_USART2, ENABLE);
        RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);

        GPIO_InitTypeDef GPIO_InitStructure;
        GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
        GPIO_InitStructure.GPIO_Pin = GPIO_Pin_2;
        GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
        GPIO_Init(GPIOA, &GPIO_InitStructure);

        GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU;
        GPIO_InitStructure.GPIO_Pin = GPIO_Pin_3;
        GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
        GPIO_Init(GPIOA, &GPIO_InitStructure);

        USART_InitTypeDef USART_InitStructure;
        USART_InitStructure.USART_BaudRate = 115200;        // ESP8266波特率
        USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
        USART_InitStructure.USART_Mode = USART_Mode_Tx | USART_Mode_Rx;
        USART_InitStructure.USART_Parity = USART_Parity_No;
        USART_InitStructure.USART_StopBits = USART_StopBits_1;
        USART_InitStructure.USART_WordLength = USART_WordLength_8b;
        USART_Init(USART2, &USART_InitStructure);

        USART_ITConfig(USART2, USART_IT_RXNE, ENABLE);

        NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
        NVIC_InitTypeDef NVIC_InitStructure;
        NVIC_InitStructure.NVIC_IRQChannel = USART2_IRQn;
        NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
        NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
        NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
        NVIC_Init(&NVIC_InitStructure);

        USART_Cmd(USART2, ENABLE);
    }
}

void Serial_SendByte(uint8_t USART_Name, uint8_t Byte)
{
    if (USART_Name == 1)
    {
        USART_SendData(USART1, Byte);
        while (USART_GetFlagStatus(USART1, USART_FLAG_TXE) == RESET);
    }
    else if (USART_Name == 2)
    {
        USART_SendData(USART2, Byte);
        while (USART_GetFlagStatus(USART2, USART_FLAG_TXE) == RESET);
    }
}

void Serial_SendArray(uint8_t USART_Name, uint8_t *Array, uint16_t Length)
{
    for (uint16_t i = 0; i < Length; i++)
    {
        Serial_SendByte(USART_Name, Array[i]);
    }
}

void Serial_SendString(uint8_t USART_Name, char *String)
{
    for (uint16_t i = 0; String[i] != '\0'; i++)
    {
        Serial_SendByte(USART_Name, String[i]);
    }
}

uint32_t Serial_Pow(uint32_t x, uint32_t y)
{
    uint32_t Result = 1;
    while (y--)
    {
        Result *= x;
    }
    return Result;
}

void Serial_SendNumber(uint8_t USART_Name, uint32_t Number, uint16_t Length)
{
    for (uint16_t i = 0; i < Length; i++)
    {
        Serial_SendByte(USART_Name, Number / Serial_Pow(10, Length - i - 1) % 10 + '0');
    }
}

void Serial_Printf(uint8_t USART_Name, char *format, ...)
{
    char String[100];
    va_list arg;
    va_start(arg, format);
    vsprintf(String, format, arg);
    va_end(arg);
    Serial_SendString(USART_Name, String);
}

// 接收缓冲区及标志（可选，您的中断已实现）
volatile uint8_t Serial_RxFlag = 0;   // 如果需要外部使用，可声明为全局

void USART1_IRQHandler(void)
{
    if (USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)
    {
        uint8_t RxData = USART_ReceiveData(USART1);
        // 此处您原有的处理逻辑（如添加缓冲区）
        // 注意：您原有代码中也有 USART1_RX_BUF 操作，可保留
        // 但需注意避免与您的协议冲突，这里简单保留原样
        USART1_RX_BUF[USART1_RX_LEN++] = RxData;
        if (USART1_RX_LEN >= 1023) USART1_RX_LEN = 0;
        // 如果您需要帧结束标志，请自行添加
        USART_ClearITPendingBit(USART1, USART_IT_RXNE);
    }
}

void USART2_IRQHandler(void)
{
    if (USART_GetITStatus(USART2, USART_IT_RXNE) != RESET)
    {
        uint8_t RxData = USART_ReceiveData(USART2);
        USART2_RX_BUF[USART2_RX_LEN++] = RxData;
        if (USART2_RX_LEN >= 1023) USART2_RX_LEN = 0;
        USART_ClearITPendingBit(USART2, USART_IT_RXNE);
    }
}