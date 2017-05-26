package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-20.
 */
public class DaySchedule {
    String ScheduleName;
    String SchedulePlace;
    String ScheduleStartDate;
    String ScheduleExplain;
    String ScheduleNo;

    public DaySchedule(String Name, String Place, String StartDate, String Explain,String ScheduleNo) {
        this.ScheduleName = Name;
        this.SchedulePlace = Place;
        this.ScheduleStartDate = StartDate;
        this.ScheduleExplain = Explain;
        this.ScheduleNo = ScheduleNo;
    }

    public String getScheduleName() {
        return ScheduleName;
    }

    public String getSchedulePlace() {
        return SchedulePlace;
    }

    public String getScheduleStartDate() {
        return ScheduleStartDate;
    }

    public String getScheduleExplain() {
        return ScheduleExplain;
    }

    public String getScheduleNo(){
        return ScheduleNo;
    }
    public void setScheduleName(String Name) {
        this.ScheduleName = Name;
    }

    public void setSchedulePlace(String Place) {
        this.SchedulePlace = Place;
    }

    public void setScheduleStartDate(String SDate) {
        this.ScheduleStartDate = SDate;
    }
    public void setScheduleNo(String ScheduleNo){
        this.ScheduleNo = ScheduleNo;
    }

}
