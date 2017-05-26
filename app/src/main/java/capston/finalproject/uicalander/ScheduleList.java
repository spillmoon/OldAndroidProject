package capston.finalproject.uicalander;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterScheduleList;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.DaySchedule;
import capston.finalproject.utils.ServerUrl;

public class ScheduleList extends Activity {
    String tos, newdate;
    Button ScheduleAdd;
    SharedPreferences pref;
    ListView ScheduleListView;
    MyAdapterScheduleList SAdapter;
    Vector<DaySchedule> ScheduleData;
    ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();

    @Override
    protected void onResume() {
        super.onResume();
        setList(info);

    }

    private void setList(ArrayList<NameValuePair> info) {
        ConnectServer ConServer = new ConnectServer();
        JsonParser JParser = new JsonParser();
        ScheduleData = JParser.DayScheduleParser(ConServer.getJsonArray(new ServerUrl().getServerUrl() + "calendarSelectInfo.do", info));

        SAdapter = new MyAdapterScheduleList(ScheduleList.this, R.layout.schedulelistcustom, ScheduleData);
        ScheduleListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ScheduleListView.setAdapter(SAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedulelist);
        pref = getSharedPreferences("Test", 0);
        ScheduleAdd = (Button) findViewById(R.id.ScheduleAddBtn);
        ScheduleListView = (ListView) findViewById(R.id.SelScheduleList);


        ScheduleData = new Vector<DaySchedule>();
        Intent i = getIntent();
        tos = i.getStringExtra("SelectDay");//이미지 정보 받아오기

        String[] str = tos.split(",");
        newdate = str[0] + "/" + str[1] + "/" + str[2];

        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
        info.add(new BasicNameValuePair("calDate", newdate));
        ScheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (pref.getString("MasterID", "").equals("Master")) {
                    final String value = ScheduleListView.getItemAtPosition(position).toString();   //선택한 리스트item 저장
                    final Dialog dialog = new Dialog(ScheduleList.this);
                    dialog.setContentView(R.layout.schedulelistdialog);
                    dialog.setTitle("일정 수정 삭제");
                    dialog.show();

                    Button ScheduleModificationButton = (Button) dialog.findViewById(R.id.SheduleModificationButton);
                    Button ScheduleDeleteButton = (Button) dialog.findViewById(R.id.ScheduleDeleteButton);
                    Button ScheduleModificationCancel = (Button) dialog.findViewById(R.id.ScheduleModificationCancelButton);

                    ScheduleModificationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent act = new Intent(ScheduleList.this, ScheduleModification.class);
                            act.putExtra("calNo", String.valueOf(value));
                            startActivity(act);
                            dialog.dismiss();
                        }
                    });

                    ScheduleModificationCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    ScheduleDeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPause();
                            ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                            info.add(new BasicNameValuePair("calNo", value));

                            ConnectServer ConServer = new ConnectServer();
                            if (ConServer.ScheduleDel(new ServerUrl().getServerUrl(),info)==0) {// 사용가능하면 등록 활성화
                                Toast.makeText(getApplicationContext(), "삭제성공", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if (ConServer.ScheduleDel(new ServerUrl().getServerUrl(),info)==0) {// 불가능하면 비활성화
                                Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                            }
                            onResume();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "방장이 아닙니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ScheduleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString("MasterID", "").equals("Master")) {
                    Intent intent = new Intent(ScheduleList.this, ScheduleAdd.class);
                    intent.putExtra("SelectDay", tos);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "방장이 아닙니다", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
