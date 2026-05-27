#include "stm32f10x.h"                  // Device header
#include "Serial.h"
#include "Delay.h"
#include "OLED.h"
#include <stdio.h>
#include <string.h>

long long int time;
int code;
int temperature;

void ESP8266_Init ()
{
		Serial_Init(1);
		Serial_Init(2);
		Delay_ms (1000);
	
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"+++");
		Serial_SendString(1,"退出透传模式\r\n");
		Delay_ms (1000);
	
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT\r\n");
		Serial_SendString(1,"AT测试\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"OK\r\n") !=NULL )
		{
			Serial_SendString(1,"模块正常\r\n");
		}
		else
		{
			Serial_SendString(1,"模块异常\r\n");
			return;
		}
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+RST\r\n");
		Serial_SendString(1,"模块复位\r\n");
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CWMODE=1\r\n");
		Serial_SendString(1,"开始连接wifi\r\n");
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CWJAP=\"a\",\"12345678\"\r\n");
		Serial_SendString(1,"正在连接wifi...\r\n");
		
		 // 轮询检测WiFi连接状态
    u8 conn_flag = 0;
    u8 ip_flag = 0;
    unsigned int start_time = 0;
    while(start_time < 10000)
    {
			
        if(strstr(USART2_RX_BUF, "WIFI CONNECTED") != NULL) conn_flag = 1;
        if(strstr(USART2_RX_BUF, "WIFI GOT IP") != NULL) ip_flag = 1;
        if(conn_flag && ip_flag) break;  // 两个标识都出现，提前退出轮询
        Delay_ms(10);
        start_time += 10;
    }
    
    if(conn_flag && ip_flag)
    {
        Serial_SendString(1,"WiFi连接成功\r\n");
    }
    else
    {
        Serial_SendString(1,"WiFi连接失败，超时/参数错误\r\n");
    }
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPMUX=0\r\n");
		Serial_SendString(1,"开启单连接模式\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"OK\r\n") !=NULL )
		{
			Serial_SendString(1,"单连接模式正常\r\n");
		}
		else
		{
			Serial_SendString(1,"单连接模式异常\r\n");
			return;
		}
		Delay_ms (1000);
}
void Get_Time ()
{
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPSTART=\"TCP\",\"api.pinduoduo.com\",80\r\n");
		Serial_SendString(1,"开启连接时间服务器\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"CONNECT\r\n") !=NULL )
		{
			Serial_SendString(1,"服务器连接正常\r\n");
		}
		else
		{
			Serial_SendString(1,"服务器连接失败\r\n");
			return;
		}
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPMODE=1\r\n");
		Serial_SendString(1,"开启透传模式\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"OK\r\n") !=NULL )
		{
			Serial_SendString(1,"透传模式正常\r\n");
		}
		else
		{
			Serial_SendString(1,"透传模式异常\r\n");
			return;
		}
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPSEND\r\n");
		Serial_SendString(1,"开始透传数据\r\n");
		Delay_ms (800);
		Serial_SendString(2,"GET http://api.pinduoduo.com/api/server/_stm\r\n");
		Delay_ms (1000);
		Serial_SendString(1,USART2_RX_BUF);
		
		char *p=NULL;
		p=strstr (USART2_RX_BUF,"server_time");
		if (p!=NULL)
		{
			sscanf (p+13,"%lld",&time);
			Serial_Printf(1,"\r\n提取到的时间:%lld\r\n",time);
		}
		else
		{
			Serial_SendString(1,"时间提取失败");
		}
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"+++");
		Serial_SendString(1,"退出透传模式\r\n");
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPCLOSE\r\n");
		Serial_SendString(1,"关闭连接\r\n");
		Delay_ms (1000);
}

void Get_Weather ()
{
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPSTART=\"TCP\",\"api.seniverse.com\",80\r\n");
		Serial_SendString(1,"开启连接天气服务器\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"CONNECT\r\n") !=NULL )
		{
			Serial_SendString(1,"服务器连接正常\r\n");
		}
		else
		{
			Serial_SendString(1,"服务器连接失败\r\n");
			return;
		}
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPMODE=1\r\n");
		Serial_SendString(1,"开启透传模式\r\n");
		Delay_ms (800);
		if (strstr (USART2_RX_BUF,"OK\r\n") !=NULL )
		{
			Serial_SendString(1,"透传模式正常\r\n");
		}
		else
		{
			Serial_SendString(1,"透传模式异常\r\n");
			return;
		}
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPSEND\r\n");
		Serial_SendString(1,"开始透传数据\r\n");
		Delay_ms (800);
		Serial_SendString(2,"GET https://api.seniverse.com/v3/weather/now.json?key=SAFfMtpIxdHLWdQ2Q&location=haerbin&language=en&unit=c\r\n");
		Delay_ms (1000);
		Serial_SendString(1,USART2_RX_BUF);
		Delay_ms (800);
		
		char *p=NULL;
		p=strstr (USART2_RX_BUF,"code");
		if (p!=NULL)
		{
			sscanf (p+7,"%d",&code);
			Serial_Printf(1,"\r\n提取到的天气代码:%d\r\n",code);
		}
		else
		{
			Serial_SendString(1,"天气代码提取失败");
		}
		p=strstr (USART2_RX_BUF,"temperature");
		if (p!=NULL)
		{
			sscanf (p+14,"%d",&temperature);
			Serial_Printf(1,"\r\n提取到的温度:%d\r\n",temperature);
		}
		else
		{
			Serial_SendString(1,"温度提取失败");
		}
		
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"+++");
		Serial_SendString(1,"退出透传模式\r\n");
		Delay_ms (1000);
		
		USART2_RX_LEN=0;
		memset (USART2_RX_BUF,0,sizeof (USART2_RX_BUF));
		Serial_SendString(2,"AT+CIPCLOSE\r\n");
		Serial_SendString(1,"关闭连接\r\n");
		Delay_ms (1000);
	
}