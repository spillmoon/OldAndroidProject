package capston.finalproject.uiappconfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.uiothers.FLogin;
import capston.finalproject.utils.ServerUrl;

public class ConfigAppOutChk extends Activity {
    int SUCCESS=1;
    int FAIL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configappoutchk);

        Display display= ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width=400;
        getWindow().getAttributes().height=300;

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Button yes=(Button)findViewById(R.id.appoutY);
        Button no=(Button)findViewById(R.id.appoutN);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String id=intent.getStringExtra("memID");
                ConnectServer conn=new ConnectServer();
                ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
                signInfo.add(new BasicNameValuePair("memID",id));
                int flag=conn.getSuccessFail(new ServerUrl().getServerUrl() + "memberWithdraw.do", signInfo);

                if (flag==SUCCESS) {
                    Toast.makeText(getApplicationContext(), "withdraw success", Toast.LENGTH_SHORT).show();
                    Intent new_intent=new Intent(ConfigAppOutChk.this, FLogin.class);
                    new_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(new_intent);
                } else if (flag==FAIL) {
                    Toast.makeText(getApplicationContext(), "withdraw fail", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "생성한 방 지워주세요, 참여한 방 나가주세요, 신청 취소해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
