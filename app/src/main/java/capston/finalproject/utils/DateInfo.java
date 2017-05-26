package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-19.
 */
public class DateInfo {
    int year,month,day;
    public DateInfo(int year,int month,int day){
        this.year=year;
        this.month=month;
        this.day=day;
    }
    public int getYear(){
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public  int getMonth(){
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay(){
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
