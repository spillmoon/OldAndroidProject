package capston.finalproject.uicalander;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.DateInfo;
import capston.finalproject.utils.ScheduleTime;
import capston.finalproject.utils.ServerUrl;

public class ScheduleModification extends Activity {
    TextView SName, SPlace, SStartDate, STime, SExplain;
    Button SModificationBtn,Can;
    String getCalNo;
    String SelScheduleNO;
    DateInfo start;
    ScheduleTime Time;
    int y,m,d,dat,ti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedulemodification);
        SName = (TextView)findViewById(R.id.ModificationScheduleName);
        SPlace = (TextView)findViewById(R.id.ModificationSchedulePlace);
        SStartDate = (TextView)findViewById(R.id.ModificationStartDate);
        STime = (TextView)findViewById(R.id.ModificationTime);
        SExplain = (TextView)findViewById(R.id.ModificationScheduleExplain);
        SModificationBtn = (Button)findViewById(R.id.ModificationSendScheduleBtn);
        Can = (Button)findViewById(R.id.ModificationSendScheduleBtnCan);
        Intent i = getIntent();
        getCalNo = i.getStringExtra("calNo");

        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        info.add(new BasicNameValuePair("calNo", getCalNo));
        try {

            HttpClient http=new DefaultHttpClient();

            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"calendarRead.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");

            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);

            HttpEntity resEntity=responsePost.getEntity();
            InputStream stream = resEntity.getContent();

            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream,"EUC-KR"));
            String line;
            String result="";

            while((line=bufreader.readLine())!=null){
                result+=line;
            };
            jsonParser(result);//json parsing
            start=new DateInfo(y,m,d);
            Time=new ScheduleTime(dat,m);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ScheduleModification.this, dateSetListener, start.getYear(), start.getMonth() - 1, start.getDay()).show();
            }
        });
        STime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ScheduleModification.this, timeListener, Time.getHour(), Time.getMinute() ,true).show();
            }
        });



        SModificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("calNo",SelScheduleNO));
                info.add(new BasicNameValuePair("calName", SName.getText().toString()));
                info.add(new BasicNameValuePair("calDate", SStartDate.getText().toString()+" "+STime.getText().toString()));
                info.add(new BasicNameValuePair("calLoc", SPlace.getText().toString()));
                info.add(new BasicNameValuePair("calContent", SExplain.getText().toString()));

                ConnectServer ConServer = new ConnectServer();
                if (ConServer.ScheduleModif(new ServerUrl().getServerUrl(),info)==0) {// 사용가능하면 등록 활성화
                    Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (ConServer.ScheduleModif(new ServerUrl().getServerUrl(),info)==1) {// 불가능하면 비활성화
                    Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private TimePickerDialog.OnTimeSetListener timeListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Time.setHour(hourOfDay);
                    Time.setMinute(minute);
                    STime.setText(hourOfDay+":"+minute);
                }
            };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            start.setYear(year);
            start.setMonth(monthOfYear + 1);
            start.setDay(dayOfMonth);
            SStartDate.setText(start.getYear() + "/" + start.getMonth() + "/" + start.getDay());
        }
    };
    public void jsonParser(String fromServer) {
        try {
            String date,time;
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jarr = jsonob.getJSONObject("info");
            String[] str = jarr.getString("calDate").split(" ");
            date = str[0];
            time = str[1];

            SName.setText(jarr.getString("calName"));
            SPlace.setText((jarr.getString("calLoc")));
            SStartDate.setText(date);
            STime.setText(time);
            SExplain.setText(jarr.getString("calContent"));
            SelScheduleNO = jarr.getString("calNo");
            String[] str0 = date.split("/");

            y=Integer.parseInt(str0[0].substring(0,4));
            m=Integer.parseInt(str0[1].substring(0,2));
            d=Integer.parseInt(str0[2].substring(0,2));
            String [] str1 = time.split(":");
            dat=Integer.parseInt(str1[0].substring(0,2));
            ti=Integer.parseInt(str1[1]);
            System.out.println("Date-"+y+"/"+m+"/"+d);
            System.out.println("time-"+dat+":"+ti);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
