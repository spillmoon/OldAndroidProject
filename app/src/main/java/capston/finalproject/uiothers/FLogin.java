package capston.finalproject.uiothers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class FLogin extends Activity {
    EditText etLoginID, etLoginPW;
    Button btnLogin, btnSign;
    int SUCCESS=1;
    int NOID=5;
    int NOPW=6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF14C1C7));
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_flogin);

        //http 통신을 위한 코드
        if(Build.VERSION.SDK_INT>9){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // SharedPreference 선언
        final SharedPreferences pref= getSharedPreferences("Test", 0);
        final SharedPreferences.Editor pedit=pref.edit();

        btnLogin=(Button)findViewById(R.id.loginBtn);
        btnSign=(Button)findViewById(R.id.signBtn);
        etLoginID=(EditText)findViewById(R.id.loginID);
        etLoginPW=(EditText)findViewById(R.id.loginPW);

        // 로그인
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> param= new ArrayList<NameValuePair>();
                ConnectServer conn=new ConnectServer();

                param.add(new BasicNameValuePair("memID", etLoginID.getText().toString()));
                param.add(new BasicNameValuePair("memPW", etLoginPW.getText().toString()));
                int flag = conn.login(new ServerUrl().getServerUrl() + "login.do", param);
                if(flag==SUCCESS){
                    pedit.putString("ID", etLoginID.getText().toString());
                    pedit.putInt("Session", 1);
                    pedit.putString("Room", "room");
                    pedit.putInt("Master", 0);
                    pedit.apply();
                    //로그인 성공 시 AppMain으로 이동
                    Intent act=new Intent(FLogin.this,FAppMain.class);
                    startActivity(act);
                    finish();
                }
                else if(flag==NOID){
                    Toast.makeText(getApplicationContext(),"ID가 틀렸습니다",Toast.LENGTH_SHORT).show();
                }
                else if(flag==NOPW){
                    Toast.makeText(getApplicationContext(),"PW가 틀렸습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //회원가입
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(FLogin.this,FSign.class);
                startActivity(act);
            }
        });
        //SNS 연동 로그인 미구현
    }
}
