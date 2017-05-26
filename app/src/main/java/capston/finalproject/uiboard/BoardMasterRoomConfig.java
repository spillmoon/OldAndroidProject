package capston.finalproject.uiboard;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class BoardMasterRoomConfig extends Fragment {
    String strMaxmem, strSi, strGu;
    Button btnok, btnno;
    EditText etRoomName, etKeyword1, etKeyword2, etKeyword3, etInformation;
    SharedPreferences pref;
    Spinner locateSi,locateGu,maxMem,roomCategory;
    ArrayAdapter adapter1,adapter2,adapter3,adapter4;
    int SUCCESS=1;
    int FAIL=2;

    public BoardMasterRoomConfig(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_boardmasterroomconfig,null);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //사용 변수 등록
        pref=getActivity().getSharedPreferences("Test", 0);
        etRoomName = (EditText)view.findViewById(R.id.roomnamechange);//이름
        etKeyword1 = (EditText)view.findViewById(R.id.roomkeyword1change);
        etKeyword2 = (EditText)view.findViewById(R.id.roomkeyword2change);
        etKeyword3 = (EditText)view.findViewById(R.id.roomkeyword3change);
        etInformation = (EditText)view.findViewById(R.id.roomInformationchange);

        roomCategory = (Spinner)view.findViewById(R.id.roomcategorychange);
        adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.ints, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomCategory.setAdapter(adapter1);
        maxMem = (Spinner)view.findViewById(R.id.roomMaxMemberchange);
        adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.peo, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxMem.setAdapter(adapter2);
        locateSi = (Spinner)view.findViewById(R.id.roomLocateSichange);
        adapter3 = ArrayAdapter.createFromResource(getActivity(), R.array.si, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locateSi.setAdapter(adapter3);
        locateGu = (Spinner)view.findViewById(R.id.roomLocateGuchange);
        adapter4 = ArrayAdapter.createFromResource(getActivity(), R.array.gu, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locateGu.setAdapter(adapter4);

        btnok = (Button)view.findViewById(R.id.changeRoombtn);
        btnno = (Button)view.findViewById(R.id.nochangeRoombtn);

        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        ConnectServer conn=new ConnectServer();
        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

        jsonParser(conn.getJsonArray(new ServerUrl().getServerUrl() + "getRoomInfoForEdit.do", info));

        //인원 설정
        maxMem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strMaxmem = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //활동 지역
        locateSi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSi = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        locateGu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strGu = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String strRoomName = etRoomName.getText().toString();
//                String strKeyword="";
//                if(!etKeyword1.getText().equals(""))
//                    strKeyword += etKeyword1.getText().toString()+",";
//                if(!etKeyword2.getText().equals(""))
//                    strKeyword += etKeyword2.getText().toString()+",";
//               if(!etKeyword3.getText().equals(""))
//                    strKeyword += etKeyword3.getText().toString()+",";
//                String strLocate = strSi + " " + strGu;
//                String strRoomInfo = etInformation.getText().toString();
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();
                info.add(new BasicNameValuePair("roomName", etRoomName.getText().toString()));
                info.add(new BasicNameValuePair("roomLocate", strSi + " " + strGu));
                info.add(new BasicNameValuePair("roomMemCnt", strMaxmem));
                info.add(new BasicNameValuePair("roomContent", etInformation.getText().toString()));

                int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "editRoomInfo.do", info);

                if (flag==SUCCESS) {
                    Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else if (flag==FAIL) {
                    Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
                    btnok.setEnabled(false);
                    etRoomName.setText("");
                    etKeyword1.setText("");
                    etKeyword2.setText("");
                    etKeyword3.setText("");
                    etInformation.setText("");
                }
            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }
    public void jsonParser(String fromServer) {
//        String keyword,category,locate,memcnt;
        StringTokenizer st;
        int pos;
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            etRoomName.setText(jsoninfo.getString("roomName"));
//            category=jsoninfo.getString("roomCategory");
//            keyword=jsoninfo.getString("roomKeyword");
//            locate=jsoninfo.getString("roomLocate");
//            memcnt=jsoninfo.getString("roomMemCnt");
            pos=adapter1.getPosition(jsoninfo.getString("roomCategory"));
            roomCategory.setSelection(pos);
            st=new StringTokenizer(jsoninfo.getString("roomKeyword"),",");
            if(st.hasMoreTokens())
                etKeyword1.setText(st.nextToken());
            if(st.hasMoreTokens())
                etKeyword2.setText(st.nextToken());
            if(st.hasMoreTokens())
                etKeyword3.setText(st.nextToken());
            pos=adapter2.getPosition(jsoninfo.getString("roomMemCnt"));
            maxMem.setSelection(pos);
            st=new StringTokenizer(jsoninfo.getString("roomLocate")," ");
            if(st.hasMoreTokens()) {
                pos=adapter3.getPosition(st.nextToken());
                locateSi.setSelection(pos);
            }
            if(st.hasMoreTokens()){
                pos=adapter4.getPosition(st.nextToken());
                locateGu.setSelection(pos);
            }
            etInformation.setText(jsoninfo.getString("roomContent"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etRoomName.setEnabled(false);
        roomCategory.setEnabled(false);
        etKeyword1.setEnabled(false);
        etKeyword2.setEnabled(false);
        etKeyword3.setEnabled(false);
    }
}
