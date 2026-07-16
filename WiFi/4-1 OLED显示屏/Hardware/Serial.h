#ifndef SERIAL_H
#define SERIAL_H

#include "stm32f10x.h"

void Serial_Init(uint8_t USART_Name);
void Serial_SendByte(uint8_t USART_Name, uint8_t Byte);
void Serial_SendArray(uint8_t USART_Name, uint8_t *Array, uint16_t Length);
void Serial_SendString(uint8_t USART_Name, char *String);
void Serial_SendNumber(uint8_t USART_Name, uint32_t Number, uint16_t Length);
void Serial_Printf(uint8_t USART_Name, char *format, ...);

// 接收缓冲区（供外部使用）
extern char USART1_RX_BUF[1024];
extern uint16_t USART1_RX_LEN;
extern char USART2_RX_BUF[1024];
extern uint16_t USART2_RX_LEN;

#endif
