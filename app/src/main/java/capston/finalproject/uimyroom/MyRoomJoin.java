package capston.finalproject.uimyroom;

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
import capston.finalproject.adapter.MyAdapterMyJoin;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.Room;
import capston.finalproject.utils.ServerUrl;

public class MyRoomJoin extends Fragment {
    ListView listview;
    MyAdapterMyJoin mAdapter;
    Vector<Room> rooms;
    SharedPreferences pref;
    ArrayList<NameValuePair> info;
    ConnectServer conn;
    JsonParser parser;

    public MyRoomJoin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View view = inflater.inflate(R.layout.activity_myroomjoin, null);
        listview=(ListView)view.findViewById(R.id.myJoinRoom);
        rooms=new Vector<Room>();
        pref =getActivity().getSharedPreferences("Test", 0);
        info = new ArrayList<NameValuePair>();
        conn=new ConnectServer();
        parser=new JsonParser();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        info.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        rooms.clear();
        rooms=parser.myRoomParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "roomJoinList.do",info));
        mAdapter=new MyAdapterMyJoin(getActivity(),R.layout.listitem_myroomjoin,rooms);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(mAdapter);
    }
}
