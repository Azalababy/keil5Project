#ifndef DELAY_H
#define DELAY_H

#include "stm32f10x.h"

void Delay_us(uint32_t xus);
void Delay_ms(uint32_t xms);
void Delay_s(uint32_t xs);
void Delay_Init(void);
uint32_t GetTick(void);

#endif
