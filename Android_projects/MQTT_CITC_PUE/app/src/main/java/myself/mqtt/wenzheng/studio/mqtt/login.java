package myself.mqtt.wenzheng.studio.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class login extends AppCompatActivity {
    private ImageButton login;
    private EditText user;
    private EditText psd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        user = findViewById(R.id.user);
        psd = findViewById(R.id.psd);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String use = user.getText().toString();
                String pdd = psd.getText().toString();
                if (pdd.equals("1234") && pdd.equals("1234")){
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "账号或者密码错误" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
