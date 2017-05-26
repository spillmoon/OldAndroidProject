package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-24.
 */
public class ScheduleTime {
    int hour;
    int minute;
    public ScheduleTime(int hour,int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
