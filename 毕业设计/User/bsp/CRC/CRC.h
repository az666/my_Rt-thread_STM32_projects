#ifndef __CRC_H
#define __CRC_H
#include "stdio.h"
#include <rtthread.h>
unsigned short crc16(unsigned char *ptr,unsigned char len);
unsigned short CRC_16_HEX(unsigned char *Buf, unsigned char CRC_CNT);
unsigned int CRC16_Checkout ( unsigned char *puchMsg, unsigned int usDataLen );
#endif
