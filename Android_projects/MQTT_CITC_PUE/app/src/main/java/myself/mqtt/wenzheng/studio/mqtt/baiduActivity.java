package myself.mqtt.wenzheng.studio.mqtt;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class baiduActivity extends AppCompatActivity  {
    private TextView txt;
    private Button luyin;
    private MediaRecorder mediaRecorder;
    private Boolean isRecording;   /* 录音状态 */
    private String fileName;       /* 名称 */
    private File filePath;         /* 存储路径 */
    private static final String serverURL = "http://vop.baidu.com/server_api";   //语音识别网关
    private static String token=null;
    private static final String apiKey = "cqm9pqN4juzIqEnln53z9tk2";             // API Key 
    private static final String secretKey = "foNcnq0AXZPoHwgMBDqgpU0KlfVRBArv";  // Secret Key
    private static final String cuid = "866412034656645"; //唯一表示码

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);
        MainActivity.activityList.add(this);
        filePath= new File("/mnt/sdcard", "myfile.amr");
       initUi();
        luyin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Toast.makeText(baiduActivity.this,"按下了" ,Toast.LENGTH_SHORT).show();
                    startRecord();
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    Toast.makeText(baiduActivity.this,"松开了" ,Toast.LENGTH_SHORT).show();
                    stopRecord();
                }
                return true;
            }
        });
    }
    private void initUi() {
         luyin = (Button)findViewById(R.id.luyin);
        //baidu = findViewById(R.id.baidu);
        txt = findViewById(R.id.txt);
    }
    protected void startRecord(){
        if (mediaRecorder==null){
            mediaRecorder=new MediaRecorder();
            if(filePath.exists()) {                                             /* 检测文件是否存在 */
                filePath.delete();
            }
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        /* 设置麦克风 */
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setAudioChannels(1);                                  /* 单声道采样 */
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   /* 设置输出格式 */
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   /* 设置采样波形 */
            mediaRecorder.setAudioEncodingBitRate(16000);
            mediaRecorder.setOutputFile(filePath.getAbsolutePath());             /* 存储路径 */
            try{
                mediaRecorder.prepare();
                mediaRecorder.start();     /* 开始录音 */
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    protected void stopRecord(){
        if (filePath!=null && filePath.exists()){
            mediaRecorder.stop();         /* 停止录音 */
            mediaRecorder.release();      /* 释放资源 */
            mediaRecorder=null;
            /* 开始识别 */
            try {
                getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void getToken(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +"&client_id=" + apiKey + "&client_secret=" + secretKey;
                try {
                    connection = (HttpURLConnection) new URL(getTokenURL).openConnection();
                    token = new JSONObject(printResponse(connection)).getString("access_token");
                    SpeechRecognition();    //开始语音识别
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(connection!=null) connection.disconnect();
                }
            }
        }).start();
    }
    private void SpeechRecognition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strc;
                try {
                    File pcmFile = new File(filePath.getAbsolutePath());
                    HttpURLConnection conn = (HttpURLConnection) new URL(serverURL+ "?cuid=" + cuid + "&token=" + token).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "audio/amr; rate=16000");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(loadFile(pcmFile));
                    wr.flush();
                    wr.close();
                    strc=printResponse(conn);
                    Message message = new Message();
                    message.what = 0x02;
                    message.obj =strc;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private  String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        Message message = new Message();
        message.what = 0x01;
        message.obj = new JSONObject(response.toString()).toString(4);
        handler.sendMessage(message);
        return response.toString();
    }
    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            String response=(String)msg.obj;
            String strc=null;
            switch (msg.what){
                case 0x01:
                    Log.e("return:",response);            //得到返回的所有结果
                      txt.setText(response);
                    break;
                case 0x02:
                    strc=getRectstr(response,"[","]");    //得到返回语音内容
                     txt.setText(response);
                    Log.e("return:",strc);
                    break;
                default:
                    break;
            }
        }
    };
    private String getRectstr(String str,String strStart, String strEnd){
        if (str.indexOf(strStart) < 0 || str.indexOf(strEnd) < 0) return "";
        return str.substring(str.indexOf(strStart) + strStart.length()+1, str.indexOf(strEnd)-2);
    }
}
