/**
  *********************************************************************
  * @file    main.c
  * @author  fire
  * @version V1.0
  * @date    2018-xx-xx
  * @brief   RT-Thread 3.0 + STM32 线程管理
  *********************************************************************
  * @attention
  *
  * 实验平台:野火 F103-霸道 STM32 开发板 
  * 论坛    :http://www.firebbs.cn
  * 淘宝    :https://fire-stm32.taobao.com
  *
  **********************************************************************
  */ 

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
static rt_thread_t key_thread = RT_NULL;  //按键线程
static rt_thread_t usart2_thread = RT_NULL;//串口2线程
static rt_thread_t usart3_thread = RT_NULL;//串口3线程

/* 定义信号量控制块 */
rt_sem_t test_sem2 = RT_NULL;
rt_sem_t test_sem3 = RT_NULL;
rt_sem_t CSQ_sem = RT_NULL;
/************************* 全局变量声明 ****************************/
/*
 * 当我们在写应用程序的时候，可能需要用到一些全局变量。
 */
char* TOPIC = "/device/NB/wz_rt_thread_test"; //定义数据topic
int error_flag =0; //失败重连标志位
int error_time =0; //失败的次数
char *empty = "";
char ALL_message[700];
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
void connected_IP(void);
char *Str_To_ASC(char* a);
void send_MQTT(char* topic,char* message,int len);
int Find_string(char *pcBuf,char*left,char*right, char *pcRes);
/*线程声明*/
static void led1_thread_entry(void* parameter);
static void key_thread_entry(void* parameter);
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
  rt_kprintf("这是一个[野火]-STM32F103霸道-RTT线程管理实验！\n\n");
  rt_kprintf("按下K1挂起线程，按下K2恢复线程\n");
//*******************************************************************************//	
 key_thread =                          /* 线程控制块指针 */
    rt_thread_create( "key",              /* 线程名字 */
                      key_thread_entry,   /* 线程入口函数 */
                      RT_NULL,             /* 线程入口函数参数 */
                      512,                 /* 线程栈大小 */
                      2,                   /* 线程的优先级 */
                      20);                 /* 线程时间片 */
                   
    /* 启动线程，开启调度 */
   if (key_thread != RT_NULL)
        rt_thread_startup(key_thread);
    else
        return -1;
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
    rt_kprintf("信号量创建成功！\n\n");
    
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
    rt_kprintf("信号量创建成功！\n\n");
    
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
       // rt_kprintf("led1_thread running,LED1_ON\r\n");
        
        LED1_OFF;     
        rt_thread_delay(500);   /* 延时500个tick */		 		
        //rt_kprintf("led1_thread running,LED1_OFF\r\n");
    }
}
//*******************************************************************************//	
static void key_thread_entry(void* parameter)
{	
  rt_err_t uwRet = RT_EOK;	
    while (1)
    {
      if( Key_Scan(KEY1_GPIO_PORT,KEY1_GPIO_PIN) == KEY_ON )/* K1 被按下 */
      {
        rt_kprintf("挂起LED1线程！\n");
        uwRet = rt_thread_suspend(led1_thread);/* 挂起LED1线程 */
        if(RT_EOK == uwRet)
        {
          rt_kprintf("挂起LED1线程成功！\n");
        }
        else
        {
          rt_kprintf("挂起LED1线程失败！失败代码：0x%lx\n",uwRet);
        }
      } 
      if( Key_Scan(KEY2_GPIO_PORT,KEY2_GPIO_PIN) == KEY_ON )/* K1 被按下 */
      {
        rt_kprintf("恢复LED1线程！\n");
        uwRet = rt_thread_resume(led1_thread);/* 恢复LED1线程！ */
        if(RT_EOK == uwRet)
        {
          rt_kprintf("恢复LED1线程成功！\n");
        }
        else
        {
          rt_kprintf("恢复LED1线程失败！失败代码：0x%lx\n",uwRet);
        }
      }
      rt_thread_delay(20);
    }
}

//*******************************************************************************//	
static void usart2_thread_entry(void* parameter)
{
  rt_err_t uwRet = RT_EOK;	
	
    /* 任务都是一个无限循环，不能返回 */
	Usart2_SendString(USART2,"usart2 runing");
	
		NBiot_Init() ;
    connected_IP();
  while (1)
  {
//	                                                          
//		uwRet = rt_sem_take(test_sem2,	/* 获取串口中断的信号量 */
//                        0); 	  /* 等待时间：0 */
		Usart3_SendString(USART3,"\r\nCLS(0);\r\n");  //清屏
		NBiot_SendCmd("AT+CSQ","OK", 1000);
		
    send_MQTT(TOPIC,Str_To_ASC("123156"),strlen(ALL_message)); 
		sprintf(ALL_message,"%s",empty);   
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
	char cRes[20] = {0};//用于缓存下发命令
	Usart3_SendString(USART3,"usart3 runing");
	Usart3_SendString(USART3,"\r\nCLS(0);\r\n");  //清屏
	rt_thread_delay(2000);
  while (1)
  {
		uwRet = rt_sem_take(CSQ_sem,	/* 获取串口中断的信号量 */
                        0); 	  /* 等待时间：0 */
    if(RT_EOK == uwRet)
    {
			Usart3_SendString(USART3,"收到数据");
			//DS12(10,20,'晴天',4,0);
			Find_string(Usart2_Rx_Buf,"+","OK", cRes);
		Usart3_SendString(USART3,"\r\nDS12(10,20,'");
		Usart3_SendString(USART3,cRes);
		Usart3_SendString(USART3,"',4,0);\r\n");
		memset(cRes,0,sizeof(cRes));/* 清零 */
			
      memset(Usart3_Rx_Buf,0,USART3_RBUFF_SIZE);/* 清零 */
    }
  }
}
//***********************************自定义函数********************************************//	

/***********************************************************
函数名称：connected_IP
函数功能：NB连接服务器   47.105.157.158  60.205.203.64
入口参数：无
出口参数：无
备 注：分为第一次上电连接 与出错后的复位连接两种情况
***********************************************************/
void connected_IP(void)
{  
		rt_thread_delay(3000);
		 if (error_flag ==0)  //正常连接
		 {
			 
		   while(!NBiot_SendCmd("AT+CEREG?","+CEREG: 0,1", 2000)); //循环连接，直到连接到服务器
			 
			 while(!NBiot_SendCmd("AT+EMQNEW=\"60.205.203.64\",\"1883\",12000,1000","OK", 2000)); //循环连接，直到连接到服务器
			rt_thread_delay(1000);
		 if(NBiot_SendCmd("AT+EMQCON=0,3,\"rt_thread_test\",80000,1,0,\"root\",\"citc2018\"","OK", 2000)!=0){
				 rt_kprintf("%s","connected_success！");
		    }
		 }
		 else{  //复位重连
		 if(NBiot_SendCmd("AT+ZRST","OK", 1000)){ 
		 while(!NBiot_SendCmd("AT+EMQNEW=\"60.205.203.64\",\"1883\",12000,1000","OK", 2000));
		rt_thread_delay(1000);
		 if(NBiot_SendCmd("AT+EMQCON=0,3,\"rt_thread_test\",80000,1,0,\"root\",\"citc2018\"","OK", 2000)!=0)
		 {
		  rt_thread_delay(1000);
				 rt_kprintf("%s\r\n","connected_success！");
						}
				}
			 error_flag = 0;
			}
	}

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
***********************************************************/
void send_MQTT(char* topic,char* message,int len)
{
	char* head = "AT+EMQPUB=0,\"";
	char* modle = "\",1,0,0," ;
	char* fu_L = ",\"" ;
	char* fu_R = "\"" ;
	char char_len[10];
	char output [100];
	sprintf(output,"%s",""); 
	sprintf(char_len,"%s",""); 
	sprintf(char_len,"%d",len/2);    //转换为字符串
	sprintf(output, "%s%s%s%s%s%s%s", head,topic,modle,char_len,fu_L,message,fu_R); //拼接字符串
	if (!NBiot_SendCmd(output,"OK",3000))
		{
		 error_time += 1;
	}	
	else error_time = 0 ;
	if (error_time >6) // 如果连续error 6次  重连
	{
	 error_flag = 1;  //测试用
	 connected_IP();
	 error_time =0;
	}		
}	
	
/***********************************************************
函数名称：Str_To_ASC
函数功能：遍历字符串   逐个处理分析转为ASCII格式给NB
入口参数：char* a
出口参数：无
备 注：
	##0112QN=20190306152401;ST=32;CN=51;PW=CITC2018+;MN=112019015222;Flag=5 ;CP=&&LA=30;TE=25;HU=75;&&1C80\r\n
***********************************************************/
char *Str_To_ASC(char* a)  
{
	int i;
	int w;
	int x;
	char letter_out;
	char val[10];
	char letter[10];
  char mess[110];	
	static char out_msg[110];
	sprintf(out_msg,"%s",empty);
    for(i=0;a[i]!='\0';i++)
		{
			//特殊字符处理
			if(a[i]=='&')
			  strcat(mess,"26"); 
			else if(a[i]=='+') 
				strcat(mess,"2b"); 
			else if(a[i]=='=')
				strcat(mess,"3d"); 
			else if(a[i]==';')
				strcat(mess,"3b");
			else if(a[i]=='#')	
				strcat(mess,"23");
			else if(a[i]=='-')
				strcat(mess,"2d");  //拼接字符   右边的拼到左边
			else if (islower(a[i])) // islower(a[i]) 小写字母处理 
			{					
				if(a[i] >='a' && a[i]<='i'){
					x = a[i] - 'a'+61;
					sprintf(letter,"%d",x);
					strcat(mess,letter);   //拼接字符	
				}
				if(a[i] >='j' && a[i]<='o'){
					switch (a[i] - 'a'+61){
						case 70 : 
					    strcat(mess,"6a"); break;  //拼接字符	
					  case 71 : 
					    strcat(mess,"6b"); break;  //拼接字符
						case 72 : 
					    strcat(mess,"6c"); break;  //拼接字符
						case 73 : 
					    strcat(mess,"6d"); break;  //拼接字符
						case 74 : 
					    strcat(mess,"6e"); break;  //拼接字符
						case 75 : 
					    strcat(mess,"6f"); break;  //拼接字符
					}
				}
				if(a[i] >='p' && a[i]<='y'){
					x = a[i] - 'a'+61-6;
					sprintf(letter,"%d",x);
					strcat(mess,letter);   //拼接字符	
				}
				if(a[i] == 'z') strcat(mess,"7a");
				
			}
			else if (isupper(a[i])) //  isupper(c) 大写字母处理
			{		
				if(a[i] >='A' && a[i]<='I'){
					x = a[i] - 'A'+41;
					sprintf(letter,"%d",x);
					strcat(mess,letter);   //拼接字符	
				}
				if(a[i] >='J' && a[i]<='O'){
					letter_out = a[i] -42 + 'A';
					sprintf(letter,"%d%s",4,&letter_out);
					strcat(mess,letter);   //拼接字符	
				}
				if(a[i] >='P' && a[i]<='Y'){
					x = a[i] - 'A'+41-6;
					sprintf(letter,"%d",x);
					strcat(mess,letter);   //拼接字符	
				}
				if(a[i] == 'Z') strcat(mess,"5a");
			}	
			else if (a[i] >='0' && a[i]<='9')  //数字处理
					{
						w = a[i] - '0'+30;  //单个字符转  int
						sprintf(val,"%d",w);
						strcat(mess,val);   //拼接字符
					}
		}
		strcpy(out_msg,mess);
		sprintf(letter,"%s",empty);
		sprintf(mess,"%s",empty);
		return out_msg;
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
		printf("Mail name not found!\n");
	}
	else
	{
		pcBegin += strlen(left);
		memcpy(pcRes, pcBegin, pcEnd-pcBegin);
	}
 
	return 0;
}
/********************************END OF FILE****************************/
