package capston.finalproject.uicalander;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.DateInfo;
import capston.finalproject.utils.ScheduleTime;
import capston.finalproject.utils.ServerUrl;

public class ScheduleAdd extends Activity {

    int year, month, day,hour,minute;
    TextView ScheduleName,SchedulePlace,StartDate,ScheduleTimeTv,Explain;
    Button ScheduleSend;
    DateInfo start;
    ScheduleTime Time;
    SharedPreferences pref;
    String getDate;
    int selyear,selmonth,selday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduleadd);

        ScheduleName = (TextView)findViewById(R.id.ScheduleName);
        SchedulePlace = (TextView)findViewById(R.id.SchedulePlll);
        StartDate = (TextView)findViewById(R.id.StartDate);
        ScheduleTimeTv = (TextView)findViewById(R.id.EndDate);
        Explain = (TextView)findViewById(R.id.ScheduleExplain);
        ScheduleSend = (Button)findViewById(R.id.senScheduleBtn);


        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        pref= getSharedPreferences("Test", 0);
        Intent i = getIntent();
        getDate = i.getStringExtra("SelectDay");//이미지 정보 받아오기

        String[] str = getDate.split(",");
        selyear=Integer.parseInt(str[0]);
        selmonth=(Integer.parseInt(str[1]));
        selday=Integer.parseInt(str[2]);

        start = new DateInfo(selyear,selmonth,selday);
        Time = new ScheduleTime(hour,minute);

        StartDate.setText(selyear + "/" + selmonth + "/" + selday);
        ScheduleTimeTv.setText(hour + ":" + minute);

        ScheduleSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
                info.add(new BasicNameValuePair("calName", ScheduleName.getText().toString()));
                info.add(new BasicNameValuePair("calDate", StartDate.getText().toString()+" "+ScheduleTimeTv.getText().toString()));
                info.add(new BasicNameValuePair("calLoc", SchedulePlace.getText().toString()));
                info.add(new BasicNameValuePair("calContent", Explain.getText().toString()));
                ConnectServer ConServer = new ConnectServer();
                if(ConServer.getSuccessFail(new ServerUrl().getServerUrl() + "calendarInsert.do",info)==1){
                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                    finish();
                }else if(ConServer.getSuccessFail(new ServerUrl().getServerUrl() + "calendarInsert.do",info)==2){
                    Toast.makeText(getApplicationContext(),"실패, 다시 시도하세요",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"에러",Toast.LENGTH_LONG).show();
                }
            }
        });

        StartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduleAdd.this, dateSetListener, start.getYear(), start.getMonth() - 1, start.getDay()).show();
            }
        });
        ScheduleTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ScheduleAdd.this, timeListener, Time.getHour(), Time.getMinute() ,true).show();
            }
        });

    }
    private TimePickerDialog.OnTimeSetListener timeListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Time.setHour(hourOfDay);
                    Time.setMinute(minute);
                    if(hourOfDay<10) {
                        if (minute < 10) {
                            ScheduleTimeTv.setText("0" + hourOfDay + ":" + "0" + minute);
                        }else {
                            ScheduleTimeTv.setText("0" + hourOfDay + ":" + minute);
                        }
                    }else {
                        if(minute<10){
                            ScheduleTimeTv.setText(hourOfDay + ":" + "0" + minute);
                        }else {
                            ScheduleTimeTv.setText(hourOfDay + ":" + minute);
                        }
                    }
                }
            };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
                start.setYear(year);
                start.setMonth(monthOfYear + 1);
                start.setDay(dayOfMonth);
                StartDate.setText(start.getYear() + "/" + start.getMonth() + "/" + start.getDay());
        }
    };
}
