package capston.finalproject.utils;

public class Member {
    String memID;
    String memCategory;
    String memPhone;
    String memEmail;

    public Member(String memID, String memCategory, String memPhone, String memEmail) {
        this.memID=memID;
        this.memCategory=memCategory;
        this.memPhone=memPhone;
        this.memEmail=memEmail;
    }

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public String getMemCategory() {
        return memCategory;
    }

    public void setMemCategory(String memCategory) {
        this.memCategory = memCategory;
    }

    public String getMemPhone() {
        return memPhone;
    }

    public void setMemPhone(String memPhone) {
        this.memPhone = memPhone;
    }

    public String getMemEmail() {
        return memEmail;
    }

    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }
}
