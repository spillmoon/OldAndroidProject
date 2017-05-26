package capston.finalproject.uiroomlist;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterRoomList;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.Room;
import capston.finalproject.utils.ServerUrl;

public class RoomListSearch extends Fragment {
    String flag;
    ListView listview;
    Vector<Room> rooms;
    MyAdapterRoomList mAdapter;
    Spinner searchList;
    EditText searchinfo;

    public RoomListSearch() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        View view = inflater.inflate(R.layout.activity_roomlistsearch, null);
        searchList = (Spinner) view.findViewById(R.id.roomListsearchMenu);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),R.array.searchroom,android.R.layout.simple_spinner_item);
        searchinfo=(EditText)view.findViewById(R.id.roomListsearchInfo);
        ImageButton searchBtn=(ImageButton)view.findViewById(R.id.roomListSearchBtn);
        listview=(ListView)view.findViewById(R.id.roomListSearch);
        rooms=new Vector<Room>();

        //searchlist using spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchList.setAdapter(adapter);
        searchList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flag = String.valueOf(parent.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref =getActivity().getSharedPreferences("Test",0);
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                ConnectServer conn=new ConnectServer();
                JsonParser parser=new JsonParser();
                rooms.removeAllElements();

                info.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                info.add(new BasicNameValuePair("flag", flag));
                info.add(new BasicNameValuePair("search", searchinfo.getText().toString()));
                rooms = parser.roomListParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "roomFindList.do", info));

                mAdapter = new MyAdapterRoomList(getActivity(), R.layout.listitem_roomlist, rooms);
                listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listview.setAdapter(mAdapter);
            }
        });

        return view;
    }
}
