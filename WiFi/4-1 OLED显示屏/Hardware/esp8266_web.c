#include "stm32f10x.h"
#include "Serial.h"
#include "Delay.h"
#include "OLED.h"
#include "led.h"
#include <string.h>
#include <stdio.h>

extern char USART2_RX_BUF[1024];
extern uint16_t USART2_RX_LEN;

static uint8_t SendATWait(char *cmd, char *expect, uint32_t timeout_ms)
{
    USART2_RX_LEN = 0;
    memset(USART2_RX_BUF, 0, sizeof(USART2_RX_BUF));
    Serial_SendString(2, cmd);
    Serial_SendString(2, "\r\n");
    uint32_t start = GetTick();
    while (GetTick() - start < timeout_ms) {
        if (strstr(USART2_RX_BUF, expect) != NULL) {
            return 1;
        }
        Delay_ms(10);
    }
    return 0;
}

uint8_t ESP8266_ConnectWiFi(char *ssid, char *pwd)
{
    // Exit transparent mode
    Serial_SendString(2, "+++");
    Delay_ms(500);

    // Reset
    SendATWait("AT+RST", "OK", 2000);
    Delay_ms(1500);

    // Test AT
    if (!SendATWait("AT", "OK", 1000)) {
        Serial_SendString(1, "AT test failed\r\n");
        return 0;
    }

    // Set STA mode
    if (!SendATWait("AT+CWMODE=1", "OK", 1000)) {
        Serial_SendString(1, "Set mode failed\r\n");
        return 0;
    }

    // Connect AP
    char cmd[100];
    sprintf(cmd, "AT+CWJAP=\"%s\",\"%s\"", ssid, pwd);
    USART2_RX_LEN = 0;
    memset(USART2_RX_BUF, 0, sizeof(USART2_RX_BUF));
    Serial_SendString(2, cmd);
    Serial_SendString(2, "\r\n");
    Serial_Printf(1, "Connecting WiFi: %s\r\n", cmd);

    uint8_t conn_flag = 0, ip_flag = 0;
    uint32_t start_time = GetTick();
    while (GetTick() - start_time < 15000) {
        if (strstr(USART2_RX_BUF, "WIFI CONNECTED") != NULL) conn_flag = 1;
        if (strstr(USART2_RX_BUF, "WIFI GOT IP") != NULL) ip_flag = 1;
        if (conn_flag && ip_flag) {
            Serial_SendString(1, "WiFi connected\r\n");
            return 1;
        }
        Delay_ms(10);
    }
    Serial_SendString(1, "WiFi timeout\r\n");
    return 0;
}

uint8_t ESP8266_GetIP(char *ip)
{
    USART2_RX_LEN = 0;
    memset(USART2_RX_BUF, 0, sizeof(USART2_RX_BUF));
    Serial_SendString(2, "AT+CIFSR\r\n");
    Delay_ms(800);
    char *p = strstr(USART2_RX_BUF, "STAIP,\"");
    if (p) {
        p += 7;
        int i = 0;
        while (*p && *p != '\"' && i < 15) ip[i++] = *p++;
        ip[i] = '\0';
        return 1;
    }
    return 0;
}

uint8_t ESP8266_StartServer(uint16_t port)
{
    SendATWait("AT+CIPSERVER=0", "OK", 1000);
    Delay_ms(200);

    if (!SendATWait("AT+CIPMUX=1", "OK", 1000)) {
        Serial_SendString(1, "Set multi-connection failed\r\n");
        return 0;
    }

    char cmd[20];
    sprintf(cmd, "AT+CIPSERVER=1,%d", port);
    if (!SendATWait(cmd, "OK", 2000)) {
        Serial_SendString(1, "Start server failed\r\n");
        return 0;
    }

    USART2_RX_LEN = 0;
    memset(USART2_RX_BUF, 0, sizeof(USART2_RX_BUF));
    Serial_SendString(2, "AT+CIPSERVER?\r\n");
    Delay_ms(500);
    if (strstr(USART2_RX_BUF, "+CIPSERVER:1,80") != NULL) {
        Serial_SendString(1, "Server verified\r\n");
        return 1;
    } else {
        Serial_Printf(1, "Server verify failed: %s\r\n", USART2_RX_BUF);
        return 0;
    }
}

void ESP8266_HandleClient(void)
{
    if (strstr(USART2_RX_BUF, "+IPD") != NULL) {
        Serial_SendString(1, "Client request received\r\n");
        int link_id = 0, data_len = 0;
        if (sscanf(USART2_RX_BUF, "+IPD,%d,%d:", &link_id, &data_len) == 2) {
            char *req_start = strstr(USART2_RX_BUF, ":") + 1;
            if (strstr(req_start, "GET /") != NULL) {
                int led_state;
                if (strstr(req_start, "ON") != NULL) {
                    LED_On();
                    led_state = 1;
                    Serial_SendString(1, "LED ON\r\n");
                } else if (strstr(req_start, "OFF") != NULL) {
                    LED_Off();
                    led_state = 0;
                    Serial_SendString(1, "LED OFF\r\n");
                } else {
                    led_state = (GPIO_ReadOutputDataBit(GPIOA, GPIO_Pin_8) == Bit_SET) ? 1 : 0;
                }

                char html[800];
                sprintf(html,
                    "HTTP/1.1 200 OK\r\n"
                    "Content-Type: text/html; charset=utf-8\r\n"
                    "Connection: close\r\n"
                    "\r\n"
                    "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">"
                    "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">"
                    "<title>LED Control</title>"
                    "<style>"
                    "*{margin:0;padding:0;box-sizing:border-box;}"
                    "body{font-family:'Segoe UI',Arial,sans-serif;background:linear-gradient(135deg,#1a1a2e,#16213e);"
                    "min-height:100vh;display:flex;align-items:center;justify-content:center;}"
                    ".card{background:#0f3460;border-radius:20px;padding:40px 30px;text-align:center;"
                    "box-shadow:0 20px 60px rgba(0,0,0,0.5);max-width:360px;width:90%%;}"
                    "h1{color:#e94560;font-size:28px;margin-bottom:8px;}"
                    ".sub{color:#a0a0b0;font-size:13px;margin-bottom:24px;}"
                    ".led-indicator{width:80px;height:80px;border-radius:50%%;margin:0 auto 24px;"
                    "transition:all 0.3s ease;box-shadow:0 0 30px rgba(0,0,0,0.4);}"
                    ".led-on{background:radial-gradient(circle at 35%% 35%%,#ff6b6b,#c0392b);"
                    "box-shadow:0 0 50px #e94560,0 0 100px rgba(233,69,96,0.4);}"
                    ".led-off{background:radial-gradient(circle at 35%% 35%%,#555,#222);"
                    "box-shadow:0 0 20px rgba(0,0,0,0.5);}"
                    ".status{color:#eee;font-size:18px;margin-bottom:24px;font-weight:bold;}"
                    ".btn-row{display:flex;gap:16px;justify-content:center;flex-wrap:wrap;}"
                    ".btn{display:inline-block;padding:14px 32px;border-radius:12px;text-decoration:none;"
                    "font-size:16px;font-weight:600;border:none;cursor:pointer;transition:all 0.2s ease;"
                    "color:#fff;min-width:100px;}"
                    ".btn-on{background:linear-gradient(135deg,#e94560,#c0392b);box-shadow:0 6px 20px rgba(233,69,96,0.4);}"
                    ".btn-on:hover{transform:translateY(-2px);box-shadow:0 10px 28px rgba(233,69,96,0.6);}"
                    ".btn-off{background:linear-gradient(135deg,#555,#333);box-shadow:0 6px 20px rgba(0,0,0,0.4);}"
                    ".btn-off:hover{transform:translateY(-2px);box-shadow:0 10px 28px rgba(0,0,0,0.6);}"
                    ".footer{color:#555;font-size:11px;margin-top:24px;}"
                    "</style></head><body>"
                    "<div class=\"card\">"
                    "<h1>LED Control</h1>"
                    "<div class=\"sub\">STM32 + ESP8266</div>"
                    "<div class=\"led-indicator %s\"></div>"
                    "<div class=\"status\">PA8 LED: <span style=\"color:%s;\">%s</span></div>"
                    "<div class=\"btn-row\">"
                    "<a href=\"/ON\" class=\"btn btn-on\">Turn ON</a>"
                    "<a href=\"/OFF\" class=\"btn btn-off\">Turn OFF</a>"
                    "</div>"
                    "<div class=\"footer\">IP: 10.238.213.134</div>"
                    "</div></body></html>",
                    led_state ? "led-on" : "led-off",
                    led_state ? "#ff6b6b" : "#888",
                    led_state ? "ON" : "OFF"
                );
                char send_cmd[32];
                sprintf(send_cmd, "AT+CIPSEND=%d,%d\r\n", link_id, strlen(html));
                Serial_SendString(2, send_cmd);
                Delay_ms(100);
                Serial_SendString(2, html);
                Delay_ms(100);
                sprintf(send_cmd, "AT+CIPCLOSE=%d\r\n", link_id);
                Serial_SendString(2, send_cmd);
                Delay_ms(100);
                Serial_SendString(1, "Response sent\r\n");
            }
        }
        USART2_RX_LEN = 0;
        memset(USART2_RX_BUF, 0, sizeof(USART2_RX_BUF));
    }
}
