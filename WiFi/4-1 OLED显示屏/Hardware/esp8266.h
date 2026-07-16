#ifndef ESP8266_H
#define ESP8266_H

#include "stm32f10x.h"

void ESP8266_Init(void);
int ESP8266_ConnectAP(char *ssid, char *pwd);
int ESP8266_GetIP(char *ip);
int ESP8266_StartServer(uint16_t port);
void ESP8266_HandleClient(void);

#endif
