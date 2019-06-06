package myself.mqtt.wenzheng.studio.mqtt;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

// 这里利用另一种点击响应事件的方法来操作，即自定义为全局响应。然后判断id来实现监听
public class MqttActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
        MainActivity.activityList.add(this);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.huancun).setOnClickListener(this);
    }
    // 向SD卡写入数据
    private void writeSDcard(String str) {
        try {
            // 判断是否存在SD卡
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 获取SD卡的目录
                File sdDire = Environment.getExternalStorageDirectory();
                FileOutputStream outFileStream = new FileOutputStream(
                        sdDire.getCanonicalPath() + "/test.txt");
                outFileStream.write(str.getBytes());
                outFileStream.close();
                Toast.makeText(this, "数据保存到text.txt文件了", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 从SD卡中读取数据
    private void readSDcard() {
        StringBuffer strsBuffer = new StringBuffer();
        try {
            // 判断是否存在SD
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory()
                        .getCanonicalPath() + "/test.txt");
                // 判断是否存在该文件
                if (file.exists()) {
                    // 打开文件输入流
                    FileInputStream fileR = new FileInputStream(file);
                    BufferedReader reads = new BufferedReader(
                            new InputStreamReader(fileR));
                    String st = null;
                    while ((st = reads.readLine()) != null) {
                        strsBuffer.append(st);
                    }
                    fileR.close();
                } else {
                    Toast.makeText(this, "该目录下文件不存在", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "读取到的数据是：" + strsBuffer.toString() + "",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.huancun :
                Toast.makeText(MqttActivity.this, "已点击", Toast.LENGTH_SHORT).show();
                writeSDcard("123456");
                break;
        }

    }
}
