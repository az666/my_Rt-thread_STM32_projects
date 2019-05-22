#include "CRC.h"
//CRC16校验算法 适用于字符串处理
unsigned short crc16(unsigned char *ptr,unsigned char len)
{
	unsigned long wcrc=0XFFFF;//预置16位crc寄存器，初值全部为1
	int i=0,j=0;//定义计数
	for(i=0;i<len;i++)//循环计算每个数据
  {
		wcrc^=*ptr++;//将八位数据与crc寄存器亦或.指针地址增加，指向下个数据
		for(j=0;j<8;j++)//循环计算数据的
		{
			if(wcrc&0X0001)//判断右移出的是不是1，如果是1则与多项式进行异或。
			{
				wcrc=wcrc>>1^0XA001;//先将数据右移一位再与上面的多项式进行异或
			}
			else//如果不是1，则直接移出
			{
				wcrc>>=1;//直接移出
			}
		}
	}
	return (wcrc);//默认就是 高位在前  低位在后   
}
//CRC16校验算法 适用于16进制处理
unsigned short CRC_16_HEX(unsigned char *Buf, unsigned char CRC_CNT)
{
    unsigned long CRC_Temp;
    unsigned char i,j;
    CRC_Temp = 0xffff;
    for (i=0;i<CRC_CNT; i++){
        //printf("%x@",Buf[i]);      
        CRC_Temp ^= Buf[i];        
        for (j=0;j<8;j++) {
            if (CRC_Temp & 0x01)
                CRC_Temp = (CRC_Temp >>1 ) ^ 0xa001;
            else
                CRC_Temp = CRC_Temp >> 1;
        }
    }
		
    return(CRC_Temp>>8|CRC_Temp<<8);  //
}
//国标CRC校验方式代码
unsigned int CRC16_Checkout ( unsigned char *puchMsg, unsigned int usDataLen ) 
	{ 
	unsigned int i,j,crc_reg,check; 
  crc_reg = 0xFFFF; 
	for(i=0;i<usDataLen;i++)  
	{ 
		crc_reg = (crc_reg>>8) ^ puchMsg[i];   
		for(j=0;j<8;j++) 
		{ 
			check = crc_reg & 0x0001;  
			crc_reg >>= 1;       
			if(check==0x0001)  
      {    
	    crc_reg ^= 0xA001;  
	     } 
	}
	} 
	return crc_reg; 
}


