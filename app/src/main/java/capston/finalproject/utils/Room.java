package capston.finalproject.utils;

public class Room {
    private String roomCategory;
    private String roomName;
    private String roomLocate;
    private String roomKeyword;
    private int currentMemCnt;
    private int roomMemCnt;
    private int reqStatus;

    public Room(String roomName, String roomCategory, String roomKeyword,
                String roomLocate, int currentMemCnt, int roomMemCnt, int reqStatus){
        this.roomName=roomName;
        this.roomCategory=roomCategory;
        this.roomKeyword=roomKeyword;
        this.roomLocate=roomLocate;
        this.currentMemCnt=currentMemCnt;
        this.roomMemCnt=roomMemCnt;
        this.reqStatus=reqStatus;
    }

    public Room(String roomName, String roomCategory, String roomKeyword,
                String roomLocate, int currentMemCnt, int roomMemCnt){
        this.roomName=roomName;
        this.roomCategory=roomCategory;
        this.roomKeyword=roomKeyword;
        this.roomLocate=roomLocate;
        this.currentMemCnt=currentMemCnt;
        this.roomMemCnt=roomMemCnt;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public String getRoomLocate() {
        return roomLocate;
    }

    public String getRoomKeyword() {
        return roomKeyword;
    }

    public int getRoomMemCnt() {
        return roomMemCnt;
    }

    public int getCurrentMemCnt() {
        return currentMemCnt;
    }

    public int getReqStatus() {
        return reqStatus;
    }
}
