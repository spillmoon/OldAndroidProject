package capston.finalproject.uiroomlist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class RoomApplyCancel extends Activity {
    String intentname;
    TextView tvInfoName, tvInfoInteresting, tvKeyword, tvMaster, tvLocate, tvCurrentMem,tvInfo;
    Button btnApply;
    ConnectServer conn;
    int SUCCESS=1;
    int FAIL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomapplycancel);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent intent = getIntent();
        tvInfoName=(TextView)findViewById(R.id.roomApplyCancelroomname);
        tvInfoInteresting=(TextView)findViewById(R.id.roomApplyCancelinteresting);
        tvKeyword=(TextView)findViewById(R.id.roomApplyCancelkey1);
        tvMaster=(TextView)findViewById(R.id.roomApplyCancelroommaster);
        tvLocate=(TextView)findViewById(R.id.roomApplyCancelarea);
        tvCurrentMem=(TextView)findViewById(R.id.roomApplyCancelpeostate);
        tvInfo=(TextView)findViewById(R.id.roomApplyCancelinformation);
        btnApply=(Button)findViewById(R.id.roomApplyCancelbtn);
        conn=new ConnectServer();
        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();

        intentname = intent.getStringExtra("name");
        info.add(new BasicNameValuePair("roomName", intentname));
        jsonParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "roomInfo.do", info));

        // try apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref=getSharedPreferences("Test",0);
                ArrayList<NameValuePair> apply = new ArrayList<NameValuePair>();
                apply.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                apply.add(new BasicNameValuePair("roomName", intentname));

                int flag= conn.getSuccessFail(new ServerUrl().getServerUrl() + "roomApplyCancel.do",apply);

                if (flag==SUCCESS) {
                    Toast.makeText(getApplicationContext(), "신청 취소", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (flag==FAIL) {
                    Toast.makeText(getApplicationContext(), "신청 취소 실패", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }//end of onCreate

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            tvInfoName.setText(jsoninfo.getString("roomName"));
            tvInfoInteresting.setText(jsoninfo.getString("roomCategory"));
            tvKeyword.setText(jsoninfo.getString("roomKeyword"));
            tvMaster.setText(jsoninfo.getString("leaderID"));
            tvLocate.setText(jsoninfo.getString("roomLocate"));
            tvCurrentMem.setText(jsoninfo.getString("currentMemCnt")+"/"+jsoninfo.getString("roomMemCnt"));
            tvInfo.setText(jsoninfo.getString("roomContent"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}