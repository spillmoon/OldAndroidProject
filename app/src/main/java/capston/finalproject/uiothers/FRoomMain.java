package capston.finalproject.uiothers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.uiboard.Board;
import capston.finalproject.uiboard.BoardMaster;
import capston.finalproject.uiboard.BoardUserAcceptPermit;
import capston.finalproject.uicalander.RoomCalendar;
import capston.finalproject.uigallary.GalleryMain;
import capston.finalproject.utils.ServerUrl;

public class FRoomMain extends Activity {
    Button config,board,gallery,calendar;
    SharedPreferences pref;
    int TRANSFERED=10;
    int LEADER=11;
    int NOMAL=12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_froommain);

        pref=getSharedPreferences("Test", 0);
        setTitle(pref.getString("RoomName",""));

        calendar=(Button)findViewById(R.id.roomMainCalander);
        config=(Button)findViewById(R.id.roomMainconf);
        board=(Button)findViewById(R.id.roomMainBoard);
        gallery=(Button)findViewById(R.id.roomMainGalary);

        ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();
        ConnectServer conn=new ConnectServer();
        user.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        user.add(new BasicNameValuePair("roomName", pref.getString("RoomName","")));
        int flag=conn.sendGet(new ServerUrl().getServerUrl()+"compLeader.do",user);
        if (flag==LEADER) {
            final SharedPreferences.Editor pedit = pref.edit();
            pedit.putString("MasterID", "Master");
            pedit.apply();
        } else {
            final SharedPreferences.Editor pedit = pref.edit();
            pedit.putString("MasterID", "NoMaster");
            pedit.apply();
        }

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();
                ConnectServer conn=new ConnectServer();
                user.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                user.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

                int flag=conn.sendGet(new ServerUrl().getServerUrl()+"compLeader.do",user);

                if (flag==TRANSFERED) {
                    Toast.makeText(getApplicationContext(), "권한을 양도 받았습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FRoomMain.this, BoardUserAcceptPermit.class);
                    startActivity(intent);
                } else if (flag==LEADER) {
                    Toast.makeText(getApplicationContext(), "방장입니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FRoomMain.this, BoardMaster.class);
                    startActivity(intent);
                } else if (flag==NOMAL) {
                    Toast.makeText(getApplicationContext(), "접근 불가", Toast.LENGTH_SHORT).show();
                }
            }
        });

        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FRoomMain.this, Board.class);
                startActivity(intent);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FRoomMain.this, GalleryMain.class);
                startActivity(intent);
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FRoomMain.this, RoomCalendar.class);
                startActivity(intent);
            }
        });
    }
}
