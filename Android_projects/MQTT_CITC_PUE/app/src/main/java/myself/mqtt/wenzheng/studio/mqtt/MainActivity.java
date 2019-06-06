package myself.mqtt.wenzheng.studio.mqtt;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowInsets;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.update.PgyUpdateManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
//import org.feezu.liuli.timeselector.TimeSelector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
//import org.feezu.liuli.timeselector.TimeSelector;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import myself.mqtt.wenzheng.studio.mqtt.EsptouchDemoActivity;
import myself.mqtt.wenzheng.studio.mqtt.MqttActivity;
import myself.mqtt.wenzheng.studio.mqtt.R;
import myself.mqtt.wenzheng.studio.mqtt.baiduActivity;

import static myself.mqtt.wenzheng.studio.mqtt.R.id.drawer_layout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {     //程序初始化
    private String host = "tcp://60.205.203.64:1883";  //ckgjppc.mqtt.iot.bj.baidubce.com   47.105.157.158:1883"
    private String userName = "root";
    private String passWord = "citc2018";
    private String messagg_val ;
    private Handler handler;
    private MqttClient client;
    private String PUE_Topic = "wenzheng/PUE";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private String imei;
    private LocationClient mLocationClient;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LinearLayout linlayout1;
    private LinearLayout map_layout;
    private TextView tempture;
    private TextView hem;
    private TextView A1;
    private TextView A2;
    private TextView A3;
    private TextView A4;
    private TextView A5;
    private TextView A6;
    private Button test111;
    private TextView positioning;
    private boolean isFirstLocate = true;

    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {       //程序启动操作
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        //MQTT初始化
        init();
        //bmob初始化
        Bmob.initialize(this, "c7bb112efe55d1f43973476f5e83c6ce");
        setContentView(R.layout.activity_main);
        //蒲公英加载
        PgyCrashManager.register(this);
        PgyUpdateManager.setIsForced(false); //设置是否强制更新。true为强制更新；false为不强制更新（默认值）。
        PgyUpdateManager.register(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linlayout1 = findViewById(R.id.linlayout1);
        map_layout = findViewById(R.id.map_layout);
        mapView = findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        initLocation();
        //非常好用的安卓权限动态授权！！！
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions= permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }
        FloatingActionButton fab = findViewById(R.id.fab);
          fab.setOnTouchListener(new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                  if(event.getAction() == MotionEvent.ACTION_DOWN){
                      //Toast.makeText(MainActivity.this,"按下了" ,Toast.LENGTH_SHORT).show();
                      map_layout.setVisibility(View.VISIBLE);//显示控件
                  }else if(event.getAction() == MotionEvent.ACTION_UP){
                     // Toast.makeText(MainActivity.this,"松开了" ,Toast.LENGTH_SHORT).show();
                      map_layout.setVisibility(View.GONE);  //隐藏控件
                  }
                  return true; //消耗掉当前事件
              }
          });
        DrawerLayout drawer = findViewById(drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                upda_time();
                if (msg.what == 1) {   //收到消息  并更新UI
                    messagg_val = (String) msg.obj;
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    if (messagg_val.indexOf(PUE_Topic)!= -1)
                     {
//                        // String regEx="[^0-9.]";  //正则表达式
//                         //Pattern p = Pattern.compile(regEx);
//                        // Matcher m = p.matcher(messagg_val);

                        String  T_VAL = messagg_val.substring(messagg_val.indexOf("T")+2,messagg_val.indexOf("H"));
                        String H_VAL = messagg_val.substring(messagg_val.indexOf("H:")+2,messagg_val.indexOf("E1"));
                        String val_A1 =  messagg_val.substring(messagg_val.indexOf("E1:")+3,messagg_val.indexOf("E2"));
                        String val_A2 =  messagg_val.substring(messagg_val.indexOf("E2:")+3);
//                        String val_A3 =  messagg_val.substring(messagg_val.indexOf("A2:")+3,messagg_val.indexOf("A3"));
//                        String val_A4 =  messagg_val.substring(messagg_val.indexOf("A3:")+3,messagg_val.indexOf("A4"));
//                        String val_A5 =  messagg_val.substring(messagg_val.indexOf("A4:")+3,messagg_val.indexOf("A5"));
//                        String val_A6 =  messagg_val.substring(messagg_val.indexOf("A5:")+3);
                        tempture.setText(work(T_VAL,1));
                         hem.setText(work(H_VAL,1));
                        A1.setText(work(val_A1,0));
                        A2.setText(work(val_A2,0));
//                        A3.setText(work(val_A3));
//                        A4.setText(work(val_A4));
//                        A5.setText(work(val_A5));
//                        A6.setText(work(val_A6));
                     }
                } else if (msg.what == 2) {
                    System.out.println("连接成功");
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe(PUE_Topic, 1);//订阅主题“PUE_Topic”
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 3) {
                    Toast.makeText(MainActivity.this, "连接失败，系统正在重连", Toast.LENGTH_SHORT).show();
                    System.out.println("连接失败，系统正在重连");
                }
            }
            private void upda_time() {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String format1 = format.format(date);

            }
        };
        //获取wifi服务
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);     //获取ip地址
        Toast.makeText(MainActivity.this, ip, Toast.LENGTH_SHORT).show();
        startReconnect();
        initlistener(); //设置监听
    }

    private void requestLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for (int result: grantResults
                            ) {
                        if(result !=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有的权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this, "发生了错误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuilder  currentPosition =  new StringBuilder();
                currentPosition.append("维度：").append(bdLocation.getLatitude()).append("\n");
                currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
                currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
                currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
                currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
                currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
                currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
                currentPosition.append("门牌号：").append(bdLocation.getStreetNumber()).append("\n");
                currentPosition.append("定位方式：");

                if(bdLocation.getLocType() == BDLocation.TypeGpsLocation ||bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                    if(isFirstLocate){
                        LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(18.0f);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        isFirstLocate = false;
                        //地图位置显示
                        Toast.makeText(MainActivity.this, bdLocation.getAddrStr(),Toast.LENGTH_LONG).show();
                       // weizhi.setText(bdLocation.getAddrStr()); //地理位置显示
                    }
                    MyLocationData.Builder  locationBuilder = new MyLocationData.Builder();
                    locationBuilder.latitude(bdLocation.getLatitude());
                    locationBuilder.longitude(bdLocation.getLongitude());
                    MyLocationData locationData = locationBuilder.build();
                    baiduMap.setMyLocationData(locationData);
                }
               positioning.setText(currentPosition);//经纬度显示
            }
        });
//        mLocationClient.start();
    }
    public static List<Activity> activityList = new LinkedList();
    public void exit()
    {

        for(Activity act:activityList)
        {
            act.finish();
        }

        System.exit(0);

    }
    public static Boolean existsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei = (String) method.invoke(manager, slotId);
            return imei;
        } catch (Exception e) {
            return "";
        }
    }
    private String intToIp(int i) {      // 整型转ip地址
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    private  void initlistener(){      //初始化监听
        tempture = findViewById(R.id.tempture);
        hem = findViewById(R.id.hem);
      A1 = findViewById(R.id.A1);
      A2 = findViewById(R.id.A2);
      A3 = findViewById(R.id.A3);
      A4 = findViewById(R.id.A4);
      A5 = findViewById(R.id.A5);
      A6 = findViewById(R.id.A6);
      test111 = findViewById(R.id.test111);
      positioning = findViewById(R.id.positioning);
      positioning.setSelected(true);
    }
    //新建person表格式
        public class Person extends BmobObject {
        private String name;
        private String address;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getAddress() {
            return address;
        }
        public void setAddress(String address) {
            this.address = address;
        }
    }

    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }
    private void init() {
        try {
            imei = getIMEI(MainActivity.this,0);
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, imei,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 1;   //收到消息标志位
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);    // hander 回传
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void makeText(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
    private String work(String meg,int x){

        if (x==0){   //电流处理
            int int_val1 = Integer.parseInt(meg);
            if(int_val1 == 0) {
                return String.valueOf(0);
            }else{
                double double_val1 = int_val1*3.125-12500;
                return String.valueOf(double_val1);
            }
        }else if (x==1){  //温湿度处理
            double double_value = Double.valueOf(meg);
            if(double_value == 0) {
                return String.valueOf(0);
            }else{
                return String.valueOf(double_value/10);
            }
        }
       return "0";
    }
    private void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    private void publishmessageplus(String topic,String message2)
    {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (client != null && keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                client.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        try {
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            exit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
///////////////////////////////////////////////////
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            Toast.makeText(MainActivity.this, "正在跳转页面", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,MqttActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,baiduActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,EsptouchDemoActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            // 以对话框的形式弹出
            PgyFeedback.getInstance().showDialog(MainActivity.this);
            // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
            // 打开沉浸式,默认为false
             // FeedbackActivity.setBarImmersive(true);
            //PgyFeedback.getInstance().showActivity(MainActivity.this);
        } else if (id == R.id.nav_send) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

}
