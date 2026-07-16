#include "stm32f10x.h"

static __IO uint32_t uwTick;

/**
  * @brief  微秒级延时（72MHz 下）
  * @param  xus 延时时长，范围：0~233015
  * @retval 无
  */
void Delay_us(uint32_t xus)
{
    SysTick->LOAD = 72 * xus;
    SysTick->VAL = 0x00;
    SysTick->CTRL = 0x00000005;
    while(!(SysTick->CTRL & 0x00010000));
    SysTick->CTRL = 0x00000004;
}

/**
  * @brief  毫秒级延时
  * @param  xms 延时时长
  * @retval 无
  */
void Delay_ms(uint32_t xms)
{
    while(xms--)
    {
        Delay_us(1000);
    }
}

/**
  * @brief  秒级延时
  * @param  xs 延时时长
  * @retval 无
  */
void Delay_s(uint32_t xs)
{
    while(xs--)
    {
        Delay_ms(1000);
    }
}

/**
  * @brief  初始化SysTick，产生1ms中断
  * @retval 无
  */
void Delay_Init(void)
{
    // 系统时钟为72MHz，SysTick频率 = 72MHz/8 = 9MHz，重装载值 = 9000 得到1ms
    if (SysTick_Config(SystemCoreClock / 1000))
    {
        while(1);  // 初始化失败
    }
}

/**
  * @brief  SysTick中断服务函数（1ms递增）
  * @retval 无
  */
void SysTick_Handler(void)
{
    uwTick++;
}

/**
  * @brief  获取当前系统运行毫秒数
  * @retval 毫秒计数
  */
uint32_t GetTick(void)
{
    return uwTick;
}