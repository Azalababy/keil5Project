#ifndef __KEY_H
#define __KEY_H

#include "FreeRTOS.h"
#include "queue.h"   // ∂®“Â QueueHandle_t

void Key_Init(void);
uint8_t Key_GetNum(void);
void Key_Tick(void);
void Key3_Tick(void);

extern  QueueHandle_t g_xQueueKey; 
extern uint8_t Key_Num;
#endif
