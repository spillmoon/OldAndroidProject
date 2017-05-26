package capston.finalproject.uiboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterBoardList;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.service.JsonParser;
import capston.finalproject.utils.BoardUtil;
import capston.finalproject.utils.ServerUrl;

public class Board extends Activity {
    Button upload, config;
    ImageButton search;
    EditText searchkeyinfo;
    Spinner searchkey;
    ListView listview;
    MyAdapterBoardList mAdapter;
    Vector<BoardUtil> boardList;
    String flag;
    int LEADER=11;
    SharedPreferences pref;
    ArrayList<NameValuePair> info;
    ConnectServer conn;
    JsonParser parser;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        if(Build.VERSION.SDK_INT>9){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        config = (Button) findViewById(R.id.boardconfig);
        upload = (Button) findViewById(R.id.boarduploadcontentbtn);
        search = (ImageButton) findViewById(R.id.boardsearchbtn);
        searchkey = (Spinner) findViewById(R.id.boardsearchkey);
        searchkeyinfo = (EditText) findViewById(R.id.boardsearchinfo);
        listview = (ListView) findViewById(R.id.boardList);
        boardList = new Vector<BoardUtil>();
        pref = getSharedPreferences("Test", 0);
        setTitle(pref.getString("RoomName","")+" 게시판");

        info = new ArrayList<NameValuePair>();
        conn = new ConnectServer();
        parser = new JsonParser();
    }

    @Override
    public void onResume() {
        super.onResume();

        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
        boardList.clear();
        boardList = parser.boardParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "boardAllList.do", info));
        mAdapter = new MyAdapterBoardList(Board.this, R.layout.listitem_board, boardList);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(mAdapter);

        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.searchboard, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchkey.setAdapter(adapter);
        searchkey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = parent.getSelectedItemPosition();
                flag = String.valueOf(i);
                parent.setBackgroundColor(0xFF14C1C7);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();
                user.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                user.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

                int flag = conn.sendGet(new ServerUrl().getServerUrl() + "compLeader.do", user);

                if (flag == LEADER) {
                    Intent act = new Intent(Board.this, BoardFolderConf.class);
                    startActivity(act);
                } else {
                    Toast.makeText(getApplicationContext(), "양도받지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(Board.this, BoardContentUpload.class);
                startActivity(act);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
                info.add(new BasicNameValuePair("flag", flag));
                info.add(new BasicNameValuePair("boardSearch", searchkeyinfo.getText().toString()));
                searchkeyinfo.setText("");
                boardList.clear();
                boardList = parser.boardParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "boardSearch.do", info));
                mAdapter = new MyAdapterBoardList(Board.this, R.layout.listitem_board, boardList);
                listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listview.setAdapter(mAdapter);
            }
        });
    }
}
