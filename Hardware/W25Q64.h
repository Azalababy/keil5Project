#ifndef __W25Q64_H
#define __W25Q64_H

void W25Q64_Init(void);
void W25Q64_ReadID(uint8_t *MID,uint16_t *DID);

void W25Q64_PageProgram(uint32_t Address,uint16_t *DataArray,uint16_t Count);

//扇区擦除
void W25Q64_SectorErase(uint32_t Address);

//读取数据 DataArray输出数据
void W25Q64_ReadData(uint32_t Address,uint16_t *DataArray,uint32_t Count);

#endif

