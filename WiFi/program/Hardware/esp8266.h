#ifndef __ESP8266_H__
#define __ESP8266_H__

extern long long int time;
extern uint16_t code;
extern int temperature;

void ESP8266_Init ();
void Get_Time ();
void Get_Weather ();

#endif