#include "stm32f10x.h"                  // Device header
#include "core_cm3.h"   // 添加这一行


// 手动定义 DWT 寄存器地址（适用于 Cortex-M3）
#define DWT_CYCCNTENA_Msk    (1UL << 0)          // DWT 控制寄存器的使能位
#define DWT_BASE             0xE0001000
#define DWT_CTRL             (*(volatile uint32_t *)(DWT_BASE + 0x000))
#define DWT_CYCCNT           (*(volatile uint32_t *)(DWT_BASE + 0x004))
#define DEMCR                (*(volatile uint32_t *)(0xE000EDFC))
#define TRCENA_Msk           (1UL << 24)         // 调试跟踪使能位



void DWT_Init(void)
{
    DEMCR |= TRCENA_Msk;          // 使能 DWT 跟踪
    DWT_CYCCNT = 0;               // 复位计数器
    DWT_CTRL |= DWT_CYCCNTENA_Msk; // 使能 DWT 周期计数
}

void Delay_us(uint32_t us)
{
    uint32_t tick_start = DWT_CYCCNT;
    uint32_t tick_wait = us * (SystemCoreClock / 1000000);
    while ((DWT_CYCCNT - tick_start) < tick_wait);
}

