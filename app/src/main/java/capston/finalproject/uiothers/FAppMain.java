package capston.finalproject.uiothers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import capston.finalproject.R;
import capston.finalproject.uiappconfig.ConfigApp;
import capston.finalproject.uimyroom.MyRoom;
import capston.finalproject.uiroomlist.RoomList;

public class FAppMain extends Activity {
    Button btnSearchRoom, btnMyRoom, btnRegistRoom, btnConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF14C1C7));
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_fappmain);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnSearchRoom=(Button)findViewById(R.id.appMainSearchRoom);
        btnMyRoom=(Button)findViewById(R.id.appMainMyRoom);
        btnRegistRoom=(Button)findViewById(R.id.appMainRegistRoom);
        btnConfig=(Button)findViewById(R.id.appMainConfigure);
        //방찾기
        btnSearchRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(FAppMain.this, RoomList.class);
                startActivity(act);
            }
        });
        //만든 방, 참여한 방 목록
        btnMyRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(FAppMain.this, MyRoom.class);
                startActivity(act);
            }
        });
        //방등록 화면으로
        btnRegistRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(FAppMain.this, FCreateRoom.class);
                startActivity(act);
            }
        });
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(FAppMain.this, ConfigApp.class);
                startActivity(act);
            }
        });
    }
}
