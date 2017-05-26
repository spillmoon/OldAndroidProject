package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-25.
 */
public class CalendarDate {
    String ScheduleNo;
    String Day;
    public CalendarDate(String NO,String day){
        this.ScheduleNo=NO;
        String[] str = day.split(" ");
        String[] str1 = str[0].split("-");
        this.Day=str1[2];
    }

    public String getDay() {
        return Day;
    }

    public String getScheduleNo() {
        return ScheduleNo;
    }

    public void setDay(String day) {
        Day = day;
    }

    public void setScheduleNo(String scheduleNo) {
        ScheduleNo = scheduleNo;
    }
}
