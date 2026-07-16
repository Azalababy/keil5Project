#include "stm32f10x.h"
#include "Serial.h"
#include "Delay.h"
#include "OLED.h"
#include "led.h"
#include <stdio.h>
#include <string.h>

extern char USART2_RX_BUF[1024];
extern uint16_t USART2_RX_LEN;

static void ClearRxBuf(void)
{
    USART2_RX_LEN = 0;
    memset(USART2_RX_BUF, 0, 1024);
}

void ESP8266_Init(void)
{
    Serial_Init(1);
    Serial_Init(2);
    Delay_ms(2000);

    ClearRxBuf();
    Serial_SendString(2, "+++");
    Delay_ms(1500);
    ClearRxBuf();

    Serial_SendString(2, "AT\r\n");
    Delay_ms(800);
    if (strstr(USART2_RX_BUF, "OK") == NULL) {
        Serial_SendString(1, "AT fail\r\n");
        return;
    }
    Serial_SendString(1, "AT OK\r\n");
    ClearRxBuf();

    Serial_SendString(2, "AT+CWMODE=1\r\n");
    Delay_ms(800);
    ClearRxBuf();

    Serial_SendString(1, "WiFi...\r\n");
    ClearRxBuf();
    Serial_SendString(2, "AT+CWJAP=\"a\",\"12345678\"\r\n");

    uint8_t ok = 0;
    uint32_t elapsed = 0;
    while (elapsed < 15000) {
        if (strstr(USART2_RX_BUF, "WIFI GOT IP") != NULL) { ok = 1; break; }
        if (strstr(USART2_RX_BUF, "FAIL") != NULL) break;
        Delay_ms(20); elapsed += 20;
    }
    if (!ok) { Serial_SendString(1, "WiFi fail\r\n"); return; }
    Serial_SendString(1, "WiFi OK\r\n");
    ClearRxBuf();
    Delay_ms(1000);

    // Get IP
    {
        char ip[16] = {0};
        for (int r = 0; r < 5; r++) {
            ClearRxBuf();
            Serial_SendString(2, "AT+CIFSR\r\n");
            Delay_ms(1500);
            char *p = strstr(USART2_RX_BUF, "STAIP,\"");
            if (!p) p = strstr(USART2_RX_BUF, "+CIFSR:STAIP,\"");
            if (p) {
                while (*p && *p != '\"') p++;
                if (*p == '\"') p++;
                int i = 0;
                while (*p && *p != '\"' && i < 15) ip[i++] = *p++;
                ip[i] = '\0';
                break;
            }
        }
        OLED_Clear();
        if (ip[0]) {
            OLED_ShowString(1, 1, "IP:");
            OLED_ShowString(2, 1, ip);
            Serial_Printf(1, "IP=%s\r\n", ip);
        } else {
            OLED_ShowString(1, 1, "No IP");
        }
    }
    ClearRxBuf();

    Serial_SendString(2, "AT+CIPMUX=1\r\n");
    Delay_ms(800);
    ClearRxBuf();

    Serial_SendString(2, "AT+CIPSERVER=0\r\n");
    Delay_ms(800);
    ClearRxBuf();

    Serial_SendString(2, "AT+CIPSERVER=1,80\r\n");
    Delay_ms(1500);
    ClearRxBuf();

    Serial_SendString(1, "READY\r\n");
}

void ESP8266_HandleClient(void)
{
    char *ipd = strstr(USART2_RX_BUF, "+IPD");
    if (ipd == NULL) return;

    int link_id = 0, data_len = 0;
    if (sscanf(ipd, "+IPD,%d,%d:", &link_id, &data_len) != 2) {
        ClearRxBuf();
        return;
    }

    char *req = strstr(ipd, ":");
    if (req == NULL) { ClearRxBuf(); return; }
    req++;

    int st;
    if      (strstr(req, "GET /ON "))  { LED_On();  st = 1; }
    else if (strstr(req, "GET /OFF ")) { LED_Off(); st = 0; }
    else { st = (GPIO_ReadOutputDataBit(GPIOA, GPIO_Pin_8) == Bit_SET) ? 1 : 0; }

    // HTTP response, inline CSS with %% for snprintf literal %
    const char *fmt =
        "HTTP/1.1 200 OK\r\n"
        "Content-Type: text/html\r\n"
        "Connection: close\r\n"
        "\r\n"
        "<!doctype html><meta charset=UTF-8>"
        "<meta name=viewport content='width=device-width,initial-scale=1'>"
        "<title>LED</title><style>"
        "*{margin:0;padding:0;box-sizing:border-box}"
        "body{font-family:Arial;background:linear-gradient(135deg,#fdfcfb,#e2d1c3);"
        "display:flex;align-items:center;justify-content:center;height:100vh;margin:0}"
        ".c{background:#fff;border-radius:24px;padding:44px 36px;text-align:center;"
        "box-shadow:0 8px 40px rgba(0,0,0,.08)}"
        "h1{color:#e07b3c;font-size:26px;margin:0 0 4px}"
        ".sub{color:#aaa;font-size:12px;margin-bottom:24px}"
        ".d{width:80px;height:80px;border-radius:50%%;margin:0 auto 20px;transition:.3s}"
        ".on{background:radial-gradient(circle at 35%% 35%%,#ffd166,#f4a261);"
        "box-shadow:0 0 40px rgba(244,162,97,.6),0 0 80px rgba(255,209,102,.3)}"
        ".off{background:radial-gradient(circle at 35%% 35%%,#ddd,#bbb);"
        "box-shadow:0 0 20px rgba(0,0,0,.1)}"
        ".s{color:#555;font-size:16px;font-weight:700;margin-bottom:28px}"
        ".b{display:inline-block;padding:13px 30px;border-radius:30px;"
        "text-decoration:none;font-size:15px;font-weight:700;color:#fff;margin:0 8px;"
        "transition:.2s;box-shadow:0 4px 14px rgba(0,0,0,.15)}"
        ".b:active{transform:scale(.95)}"
        ".n{background:linear-gradient(135deg,#f4a261,#e76f51)}"
        ".f{background:linear-gradient(135deg,#bbb,#999)}"
        "</style><div class=c>"
        "<h1>LED Control</h1>"
        "<div class=sub>STM32 + ESP8266</div>"
        "<div class='d %s'></div>"
        "<div class=s>PA8: %s</div>"
        "<a href=/ON class='b n'>ON</a>"
        "<a href=/OFF class='b f'>OFF</a>"
        "</div>";

    static char html[2000];
    int n = snprintf(html, sizeof(html), fmt, st ? "on" : "off", st ? "ON" : "OFF");

    // CIPSEND + fixed delay
    char cmd[40];
    snprintf(cmd, sizeof(cmd), "AT+CIPSEND=%d,%d\r\n", link_id, n);
    Serial_SendString(2, cmd);
    Delay_ms(400);     // ESP8266 always responds ">" within 200ms, 400 is safe

    // Send HTML
    Serial_SendString(2, html);
    Delay_ms(400);     // Wait for ESP8266 to ACK

    // Close
    snprintf(cmd, sizeof(cmd), "AT+CIPCLOSE=%d\r\n", link_id);
    Serial_SendString(2, cmd);
    Delay_ms(200);

    ClearRxBuf();      // Flush everything for next request
}
