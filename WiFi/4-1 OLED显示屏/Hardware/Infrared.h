#ifndef __INFRARED_H
#define __INFRARED_H

#include <stdint.h>

void IR_Init(void);
uint8_t IR_GetData(uint8_t *pDev, uint8_t *pKey);
uint8_t IR_HasData(void);

#define IR_KEY_POWER  0xa2
#define IR_KEY_MENU   0xe2
#define IR_KEY_TEST   0x22
#define IR_KEY_ADD    0x02
#define IR_KEY_RETURN 0xc2
#define IR_KEY_LEFT   0xe0
#define IR_KEY_PLAY   0xa8
#define IR_KEY_RIGHT  0x90
#define IR_KEY_0      0x68
#define IR_KEY_DEC    0x98
#define IR_KEY_C      0xb0
#define IR_KEY_1      0x30
#define IR_KEY_2      0x18
#define IR_KEY_3      0x7a
#define IR_KEY_4      0x10
#define IR_KEY_5      0x38
#define IR_KEY_6      0x5a
#define IR_KEY_7      0x42
#define IR_KEY_8      0x4a
#define IR_KEY_9      0x52
#define IR_KEY_REPEAT 0x00

#endif