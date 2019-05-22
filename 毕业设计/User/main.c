
/*
*************************************************************************
*                             包含的头文件
*************************************************************************
*/ 
#include "board.h"
#include "stdio.h"
#include "rtthread.h"
#include <string.h>
#include <ctype.h>


/*
*************************************************************************
*                               变量
*************************************************************************
*/
/* 定义线程控制块 */
static rt_thread_t led1_thread = RT_NULL; //led线程
static rt_thread_t usart2_thread = RT_NULL;//串口2线程
static rt_thread_t usart3_thread = RT_NULL;//串口3线程

/* 定义信号量控制块 */
rt_sem_t test_sem2 = RT_NULL;
rt_sem_t test_sem3 = RT_NULL;
rt_sem_t CSQ_sem = RT_NULL;
/************************* 全局变量声明 ****************************/
/*
 * 当我们在写应用程序的时候，可能需要用到一些全局变量。
 connected_IP(IP,Port,ID,User,Pwd);
 FE 04 00 00 00 01 25 C5
 */
char T_message[10],EC1_message[10];
unsigned char     TH[8] = {0x03, 0x03, 0x00, 0x00, 0x00, 0x02, 0xC5, 0xE9}; //温湿度传感器问询码
unsigned char     EC_1[8] = {0xFE, 0x04, 0x00, 0x00, 0x00, 0x01, 0x25, 0xC5}; //温湿度传感器问询码
char* ip = "47.105.157.158";
char* port = "1883";
char* id = "wenzheng";
char* user = "root";
char* passward = "citc2018";
char* TOPIC = "/device/NB/wz_rt_thread_test"; //定义数据topic
int error_flag =0; //失败重连标志位
int error_time =0; //失败的次数
char *empty = "";
char Find_msg[50] = {0};//用于缓存截取的字符串
/* 相关宏定义 */
extern char Usart2_Rx_Buf[USART2_RBUFF_SIZE];
extern char Usart3_Rx_Buf[USART3_RBUFF_SIZE];

/*
*************************************************************************
*                             函数声明
*************************************************************************
*/
/*函数声明*/
char *Str_To_ASC(char* a);
void send_MQTT(char* topic, char* message);
void connected_IP(char* IP,char*Port,char* ID,char* User,char* Pwd);
int Find_string(char *pcBuf,char*left,char*right, char *pcRes);
/*线程声明*/
static void led1_thread_entry(void* parameter);
static void usart2_thread_entry(void* parameter);
static void usart3_thread_entry(void* parameter);
/*
*************************************************************************
*                             main 函数
*************************************************************************
*/
/**
  * @brief  主函数
  * @param  无
  * @retval 无
  */
int main(void)
{	
    /* 
	 * 开发板硬件初始化，RTT系统初始化已经在main函数之前完成，
	 * 即在component.c文件中的rtthread_startup()函数中完成了。
	 * 所以在main函数中，只需要创建线程和启动线程即可。
	 */
  rt_kprintf("彭文正 RT-thread OS ！\n\n");

//*******************************************************************************//	
		led1_thread =                          /* 线程控制块指针 */
    rt_thread_create( "led1",              /* 线程名字 */
                      led1_thread_entry,   /* 线程入口函数 */
                      RT_NULL,             /* 线程入口函数参数 */
                      512,                 /* 线程栈大小 */
                      3,                   /* 线程的优先级 */
                      20);                 /* 线程时间片 */
                   
    /* 启动线程，开启调度 */
   if (led1_thread != RT_NULL)
        rt_thread_startup(led1_thread);
    else
        return -1;
//*******************************************************************************//	
		
		/* 创建一个信号量 */
	test_sem2 = rt_sem_create("test_sem2",/* 消息队列名字 */
                     0,     /* 信号量初始值，默认有一个信号量 */
                     RT_IPC_FLAG_FIFO); /* 信号量模式 FIFO(0x00)*/
	CSQ_sem = rt_sem_create("CSQ_sem",/* 消息队列名字 */
                     0,     /* 信号量初始值，默认有一个信号量 */
                     RT_IPC_FLAG_FIFO); /* 信号量模式 FIFO(0x00)*/	
  if (test_sem2 != RT_NULL)
    rt_kprintf("信号量2创建成功！\n\n");
    
  usart2_thread =                          /* 线程控制块指针 */
    rt_thread_create( "usart2",              /* 线程名字 */
                      usart2_thread_entry,   /* 线程入口函数 */
                      RT_NULL,             /* 线程入口函数参数 */
                      1024,                 /* 线程栈大小 */
                      4,                   /* 线程的优先级 */
                      20);                 /* 线程时间片 */
                   
    /* 启动线程，开启调度 */
   if (usart2_thread != RT_NULL)
        rt_thread_startup(usart2_thread);
    else
        return -1;
//*******************************************************************************//			
			/* 创建一个信号量 */
	test_sem3 = rt_sem_create("test_sem3",/* 消息队列名字 */
                     0,     /* 信号量初始值，默认有一个信号量 */
                     RT_IPC_FLAG_FIFO); /* 信号量模式 FIFO(0x00)*/
  if (test_sem3 != RT_NULL)
    rt_kprintf("信号量3创建成功！\n\n");
    
  usart3_thread =                          /* 线程控制块指针 */
    rt_thread_create( "usart3",              /* 线程名字 */
                      usart3_thread_entry,   /* 线程入口函数 */
                      RT_NULL,             /* 线程入口函数参数 */
                      512,                 /* 线程栈大小 */
                      5,                   /* 线程的优先级 */
                      20);                 /* 线程时间片 */
                   
    /* 启动线程，开启调度 */
   if (usart3_thread != RT_NULL)
        rt_thread_startup(usart3_thread);
    else
        return -1;	
}

/*
*************************************************************************
*                             线程定义
*************************************************************************
*/

static void led1_thread_entry(void* parameter)
{	
  
    while (1)
    {
        LED1_ON;
        rt_thread_delay(500);   /* 延时500个tick */
        //rt_kprintf("led1_thread running,LED1_ON\r\n");
        
        LED1_OFF;     
        rt_thread_delay(500);   /* 延时500个tick */		 		
        //rt_kprintf("led1_thread running,LED1_OFF\r\n");
    }
}

//*******************************************************************************//	
static void usart2_thread_entry(void* parameter)
{
  rt_err_t uwRet = RT_EOK;	
	
    /* 任务都是一个无限循环，不能返回 */
		NBiot_Init() ;
    connected_IP(ip,port,id,user,passward);
  while (1)
  {
	                                                          
		uwRet = rt_sem_take(test_sem2,	/* 获取串口中断的信号量 */
                        0); 	  /* 等待时间：0 */
		NBiot_SendCmd("AT+CSQ","OK", 1000);
    send_MQTT(TOPIC,T_message);  
		memset(T_message,0,sizeof(T_message));/* 清零 */
		rt_thread_delay(5000);
//    if(RT_EOK == uwRet)
//    {
//			
//			Usart2_SendString(USART2,"收到数据");
//			Usart2_SendString(USART2,Usart2_Rx_Buf);
//      memset(Usart2_Rx_Buf,0,USART2_RBUFF_SIZE);/* 清零 */
//    }
  }
}
//*******************************************************************************//	
static void usart3_thread_entry(void* parameter)  //暂时用作显示屏
{
  rt_err_t uwRet = RT_EOK;	
    /* 任务都是一个无限循环，不能返回 */
	rt_thread_delay(2000);
  while (1)
  {
		uwRet = rt_sem_take(CSQ_sem,	/* 获取串口中断的信号量 */
                        0); 	  /* 等待时间：0 */
		sprintf(T_message, "%d", RS485_SendCmd(TH, sizeof(TH),9,3,100));
		rt_thread_delay(200);
		sprintf(EC1_message, "%d", RS485_SendCmd(EC_1, sizeof(EC_1),7,3,100));
//	rt_kprintf("收到数据！\n");
//	rt_kprintf("T_M:%s\r\n",T_message);
//	rt_kprintf("EC1:%s\r\n",EC1_message);
		rt_thread_delay(1000);
    if(RT_EOK == uwRet)
    {
//		rt_kprintf("收到数据！\n");
//		rt_kprintf(T_message);
		//Usart3_SendString(USART3,"收到数据");
		//Usart3_SendString(USART3,Usart3_Rx_Buf);
    //memset(Usart3_Rx_Buf,0,USART3_RBUFF_SIZE);/* 清零 */
    }
  }
}
//***********************************自定义函数********************************************//	



/***********************************************************
  函数名称：send_MQTT
  函数功能：NB发送消息到MQTT服务器
  入口参数：
  char* topic:   发布消息的主题
  char* message: 发送的消息内容
  int len:       发送的消息长度
  出口参数：无
  备 注：连续发送时报错六次 复位NB模组重连！
  其中len参数必须传入 因为指针数据无法计算数据长度
  //AT+MQTTPUB="device/nb/citc",1,1,0,0,"##0112QN=20190306152401;ST=32;CN=51;PW=CITC2018+;MN=112019015222;Flag=5 ;CP=&&LA=30;TE=25;HU=75;&&1C80"
***********************************************************/
void send_MQTT(char* topic, char* message)
{
  char* head = "AT+MQTTPUB=\"";
  char* modle = "\",1,1,0,0";
  char* fu_L = ",\"";
  char* fu_R = "\"";
  char output [100];
  sprintf(output, "%s%s%s%s%s%s", head, topic, modle, fu_L, message, fu_R); //拼接字符串
  if (!NBiot_SendCmd(output, "+MQTTPUBACK", 4000))
  {
    error_time += 1; 
    rt_kprintf("error_time:%d\r\n", error_time);
  }
  else error_time = 0 ;
  if (error_time >= 5) // 如果连续error 5次  重连
  {
    error_flag = 1;  //测试用	
    connected_IP(ip,port,id,user,passward); 
		error_time = 0;
  }
  memset(output,0,sizeof(output));/*数组清零 */
}

/***********************************************************
  函数名称：Find_string(char *pcBuf,char*left,char*right, char *pcRes)
  函数功能：寻找特定字符串
  入口参数：
           char *pcBuf 为传入的字符串
           char*left   为搜索字符的左边标识符  例如："["
           char*right  为搜索字符的右边标识符  例如："]"
					 char *pcRes 为输出转存的字符串数组
  返回值：用来校验是否成功，无所谓的。
  备注：
 服务器下发命令举例：+MQTTPUBLISH: 0,0,0,0,/device/NB/zx99999999999999_back,6,[reastrobot]
***********************************************************/
int Find_string(char *pcBuf,char*left,char*right, char *pcRes)
{
	char *pcBegin = NULL;
	char *pcEnd = NULL;
	pcBegin = strstr(pcBuf, left);
	pcEnd = strstr(pcBuf, right);
	if(pcBegin == NULL || pcEnd == NULL || pcBegin > pcEnd)
	{
		rt_kprintf("Mail name not found!\n");
	}
	else
	{
		pcBegin += strlen(left);
		memcpy(pcRes, pcBegin, pcEnd-pcBegin);
	}
	return 0;
}


/***********************************************************
  函数名称：connected_IP
  函数功能：NB连接服务器   47.105.157.158  60.205.203.64
  入口参数：无
  出口参数：无
  备 注：分为第一次上电连接 与出错后的复位连接两种情况
       如果连续尝试200次尝试失败，则复位NB重连。
char* TOPIC = "/device/NB/zx11111111111111";                              //定义数据topic
char* SUB_TOPIC = "AT+MQTTSUB=\"/device/NB/zx11111111111111_back\",1";    //订阅回传主题
char* Connect = "AT+MQTTCFG=\"47.105.157.158\",1883,\"zx11111111111111\",60,\"root\",\"citc2018\",1"; //服务器配置指令
***********************************************************/
void connected_IP(char* IP,char*Port,char* ID,char* User,char* Pwd)
{
  int try_time = 0;
	char Connect [100];
	char SUB_TOPIC [100];
  rt_thread_delay(4000);
	sprintf(Connect,"%s%s%s%s%s%s%s%s%s%s%s","AT+MQTTCFG=\"",IP,"\",",Port,",\"",ID,"\",60,\"",User,"\",\"",Pwd,"\",1");
	sprintf(SUB_TOPIC,"%s%s%s","AT+MQTTSUB=\"",TOPIC,"_back\",1");
	//printf("Connect:%s",Connect);
	//printf("SUB_TOPIC:%s",SUB_TOPIC);
  if (error_flag == 0) //正常连接
  {
    Get_IP();  //循环连接，直到连接到服务器
    rt_thread_delay(1000);
		//IWDG_Feed(); //喂一次狗
    if (NBiot_SendCmd(Connect,"OK", 2000)) {
      rt_thread_delay(1000);
      if (NBiot_SendCmd("AT+MQTTOPEN=1,1,0,0,0,\"\",\"\"", "OK", 5000)) { //此处没有设置遗嘱！！确保收到返回值 延时加长！
        rt_kprintf("%s", "connected_success！");
        while (!NBiot_SendCmd(SUB_TOPIC,"OK",2000)) {
          try_time += 1;
          rt_kprintf("Try_Time:%d\r\n", try_time);
          if (try_time == 6) {
            try_time = 0;
            return;
          }
        }
        rt_kprintf("%s", "SUB_TOPIC_success！");
      } else rt_kprintf("%s", "connected_error！");
    }
  }
  else { //复位重连
    if (NBiot_SendCmd("AT+CMRB", "ATREADY", 1000)) {    //发现问题：如果返回校验设置为OK的话有可能会出现问题！！！！20190522
      Get_IP();  //循环连接，直到连接到服务器
       rt_thread_delay(1000);
      if (NBiot_SendCmd(Connect,"OK", 2000)) {
         rt_thread_delay(1000);
        if (NBiot_SendCmd("AT+MQTTOPEN=1,1,0,0,0,\"\",\"\"", "OK", 5000) != 0) //此处没有设置遗嘱！！确保收到返回值 延时加长！
          rt_kprintf("%s", "connected_success！");
        while (!NBiot_SendCmd(SUB_TOPIC, "OK", 2000)) {
          try_time += 1;
          rt_kprintf("Try_Time:%d\r\n", try_time);
          if (try_time == 6) {
            try_time = 0;
            return;
          }
        }
        rt_kprintf("%s", "SUB_TOPIC_success！");
      }
      error_flag = 0;
    }
  }
}


/********************************END OF FILE****************************/
