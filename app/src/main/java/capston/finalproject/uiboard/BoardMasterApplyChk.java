package capston.finalproject.uiboard;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterApplyList;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.Member;
import capston.finalproject.utils.ServerUrl;

public class BoardMasterApplyChk extends Fragment {
     ListView listview;
    Vector<Member> mems;
    MyAdapterApplyList mAdapter;

    public BoardMasterApplyChk() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_boardmasterapplychk, null);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        listview=(ListView)view.findViewById(R.id.boardApplyMemList);
        mems=new Vector<Member>();

        SharedPreferences pref =getActivity().getSharedPreferences("Test",0);
        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        ConnectServer conn=new ConnectServer();
        JsonParser parser=new JsonParser();
        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

        mems=parser.roomApplyChkParser(conn.getJsonArray(new ServerUrl().getServerUrl()+"applyingToMyRoomList.do",info));
        mAdapter=new MyAdapterApplyList(getActivity(),R.layout.listitem_applyingmembers,mems);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(mAdapter);

        return view;
    }
}
