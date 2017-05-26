package capston.finalproject.uicalander;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterCalendar;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.CalendarDate;
import capston.finalproject.utils.DayInfo;
import capston.finalproject.utils.ServerUrl;

public class RoomCalendar extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    public static int SUNDAY = 1;
    public static int MONDAY = 2;
    public static int TUESDAY = 3;
    public static int WEDNSESDAY = 4;
    public static int THURSDAY = 5;
    public static int FRIDAY = 6;
    public static int SATURDAY = 7;
    private TextView mTvCalendarTitle;
    private GridView mGvCalendar;
    private ArrayList<DayInfo> mDayList;
    private Vector<CalendarDate> MonthInfo;
    private MyAdapterCalendar mCalendarAdapter;
    SharedPreferences pref;
    Calendar mLastMonthCalendar;
    Calendar mThisMonthCalendar = Calendar.getInstance();
    Calendar mNextMonthCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomcalendar);

        Button bLastMonth = (Button) findViewById(R.id.calendarLast);
        Button bNextMonth = (Button) findViewById(R.id.calendarNext);

        mTvCalendarTitle = (TextView) findViewById(R.id.calendarTvTitle);
        mGvCalendar = (GridView) findViewById(R.id.calendarGv);

        pref = getSharedPreferences("Test", 0);
        setTitle(pref.getString("RoomName","")+" 캘린더");
        bLastMonth.setOnClickListener(this);
        bNextMonth.setOnClickListener(this);
        mGvCalendar.setOnItemClickListener(this);
        mDayList = new ArrayList<DayInfo>();
        MonthInfo = new Vector<CalendarDate>();

        mGvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent act = new Intent(RoomCalendar.this, ScheduleList.class);
                act.putExtra("SelectDay", String.valueOf(mDayList.get(position).getYear()) + "," + String.valueOf(mDayList.get(position).getMonth()) + "," + String.valueOf(mDayList.get(position).getDay()));
                startActivity(act);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 이번달 의 캘린더 인스턴스를 생성한다.
        mThisMonthCalendar = Calendar.getInstance();
        mThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        getCalendar(mThisMonthCalendar);
    }

    /**
     * 달력을 셋팅한다.
     *
     * @param calendar 달력에 보여지는 이번달의 Calendar 객체
     */
    private void getCalendar(Calendar calendar) {
        int lastMonthStartDay;
        int dayOfMonth;
        int thisMonthLastDay;

        ConnectServer ConServer = new ConnectServer();
        JsonParser JParser = new JsonParser();

        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
        info.add(new BasicNameValuePair("year", String.valueOf(mThisMonthCalendar.get(Calendar.YEAR))));
        info.add(new BasicNameValuePair("month", String.valueOf(mThisMonthCalendar.get(Calendar.MONTH) + 1)));

        MonthInfo=JParser.jsonParserMonthList(ConServer.getJsonArray(new ServerUrl().getServerUrl() + "calendarSelectList.do", info));
        mDayList.clear();

        // 이번달 시작일의 요일을 구한다. 시작일이 일요일인 경우 인덱스를 1(일요일)에서 8(다음주 일요일)로 바꾼다.)
        dayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, -1);

        // 지난달의 마지막 일자를 구한다.
        lastMonthStartDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, 1);

        if (dayOfMonth == SUNDAY) {
            dayOfMonth += 7;
        }

        lastMonthStartDay -= (dayOfMonth - 1) - 1;

        // 캘린더 타이틀(년월 표시)을 세팅한다.
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        DayInfo day;

        for (int i = 0; i < dayOfMonth - 1; i++) {
            int date = lastMonthStartDay + i;
            day = new DayInfo();
            day.setDay(date);
            day.setInMonth(false);
            mDayList.add(day);
        }
        boolean flag = false;
        for (int i = 1; i <= thisMonthLastDay; i++) {
            for (int d = 0; d < MonthInfo.size(); d++) {
                if (i == Integer.parseInt(MonthInfo.get(d).getDay())) {
                    flag = true;
                }
            }
            if (flag) {
                day = new DayInfo(mThisMonthCalendar.get(Calendar.YEAR), mThisMonthCalendar.get(Calendar.MONTH) + 1, i, 1);
                day.setInMonth(true);
                mDayList.add(day);
                flag = false;
            } else {
                day = new DayInfo(mThisMonthCalendar.get(Calendar.YEAR), mThisMonthCalendar.get(Calendar.MONTH) + 1, i, 0);
                day.setInMonth(true);
                mDayList.add(day);
            }
        }

        for (int i = 1;i < 42 - (thisMonthLastDay + dayOfMonth - 1) + 1; i++){
            day = new DayInfo();
            day.setDay(i);
            day.setInMonth(false);
            mDayList.add(day);
        }
        initCalendarAdapter();
        MonthInfo.clear();
    }
    /**
     * 지난달의 Calendar 객체를 반환합니다.

     */
    private Calendar getLastMonth(Calendar calendar) {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    /**
     * 다음달의 Calendar 객체를 반환합니다.
     */
    private Calendar getNextMonth(Calendar calendar) {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        mTvCalendarTitle.setText(mThisMonthCalendar.get(Calendar.YEAR) + "년 "
                + (mThisMonthCalendar.get(Calendar.MONTH) + 1) + "월");
        return calendar;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendarLast:
                mThisMonthCalendar = getLastMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
            case R.id.calendarNext:
                mThisMonthCalendar = getNextMonth(mThisMonthCalendar);
                getCalendar(mThisMonthCalendar);
                break;
        }
    }
    private void initCalendarAdapter() {
        mCalendarAdapter = new MyAdapterCalendar(this, R.layout.day, mDayList);
        mGvCalendar.setAdapter(mCalendarAdapter);
    }
}