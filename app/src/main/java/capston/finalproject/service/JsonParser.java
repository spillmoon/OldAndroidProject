package capston.finalproject.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.utils.BoardUtil;
import capston.finalproject.utils.CalendarDate;
import capston.finalproject.utils.DaySchedule;
import capston.finalproject.utils.Member;
import capston.finalproject.utils.Room;

public class JsonParser {
    Vector<Room> roomList;
    Vector<Room> myRoom;
    Vector<BoardUtil> boardList;
    Vector<Member> mems;
    ArrayList folder;
    Vector<CalendarDate> MonthInfo;
    Vector<DaySchedule> ScheduleData;


    public JsonParser(){
        roomList=new Vector<Room>();
        myRoom=new Vector<Room>();
        boardList=new Vector<BoardUtil>();
        mems=new Vector<Member>();
        folder=new ArrayList();
        MonthInfo=new Vector<CalendarDate>();
        ScheduleData=new Vector<DaySchedule>();
    }
    public Vector<CalendarDate> jsonParserMonthList(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            System.out.println(jarr);
            String[] roomInfo = {"calNo", "calDate"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                MonthInfo.add(new CalendarDate(parsered[i][0], parsered[i][1]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MonthInfo;
    };
    public Vector<DaySchedule> DayScheduleParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"calName", "calLoc", "calDate", "calContent","calNo"};
            //System.out.println(roomInfo);
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            //JSON �Ľ� �� �� ������ Vector�� ���� -> MyAdapterRoomList.getView�� ����Ʈ �ѷ���
            for (int i = 0; i < parsered.length; i++) {
                ScheduleData.add(new DaySchedule(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3],parsered[i][4]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ScheduleData;
    }

    public Vector<Room> roomListParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"roomName", "roomCategory", "roomKeyword", "roomLocate", "currentMemCnt", "roomMemCnt", "reqStatus"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                roomList.add(new Room(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3],
                        Integer.parseInt(parsered[i][4]), Integer.parseInt(parsered[i][5]), Integer.parseInt(parsered[i][6])));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomList;
    }

    public Vector<Room> myRoomParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"roomName", "roomCategory", "roomKeyword", "roomLocate", "currentMemCnt", "roomMemCnt"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                myRoom.add(new Room(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3],
                        Integer.parseInt(parsered[i][4]), Integer.parseInt(parsered[i][5])));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myRoom;
    }

    public Vector<BoardUtil> boardParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"boardNo","folderName", "boardTitle", "boardMem", "boardDate"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                boardList.add(new BoardUtil(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3], parsered[i][4]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return boardList;
    }

    public Vector<Member> roomApplyChkParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"memID", "memInterest", "memPhone", "memEmail"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                mems.add(new Member(parsered[i][0],parsered[i][1],parsered[i][2],parsered[i][3]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mems;
    }

    public ArrayList folderParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"folderNo", "folderName"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
                folder.add(parsered[i][1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return folder;
    }
}
