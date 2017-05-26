package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-17.
 */
public class DayInfo {
    private int day;
    private int  month;
    private int year;
    private boolean inMonth;
    private int flag;


    public int getMonth(){
        return month;
    }

    public int getYear(){
        return year;
    }

    public int getDay() {
        return day;
    }
    public DayInfo(){

    }


    public DayInfo(int year,int month, int day ,int calDate){
        this.year=year;
        this.month=month;
        this.day=day;
        this.flag=calDate;
    }

    public int getFlag() {
        return flag;
    }

    public void setDay(int day) {
        this.day = day;
    }

    /**
     * 이번달의 날짜인지 정보를 반환한다.
     *
     * @return inMonth(true/false)
     */
    public boolean isInMonth() {
        return inMonth;
    }

    /**
     * 이번달의 날짜인지 정보를 저장한다.
     *
     * @param inMonth(true/false)
     */
    public void setInMonth(boolean inMonth) {
        this.inMonth = inMonth;
    }

}