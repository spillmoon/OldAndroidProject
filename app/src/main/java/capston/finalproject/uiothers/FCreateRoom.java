package capston.finalproject.uiothers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class FCreateRoom extends Activity {
    String strCategory, strMaxmem, strSi, strGu;
    Button btnRoomNamdChk, btnCreateRoom, btnCreateCancel;
    EditText etRoomName, etKeyword1, etKeyword2, etKeyword3, etInformation;
    SharedPreferences pref;
    int SUCCESS=1;
    int FAIL=2;
    int VALID=3;
    int INVALID=4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcreateroom);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //사용 변수 등록
        pref=getSharedPreferences("Test",0);
        etRoomName = (EditText) findViewById(R.id.createRoomName);//이름
        etKeyword1 = (EditText) findViewById(R.id.createRoomKeyword1);
        etKeyword2 = (EditText) findViewById(R.id.createRoomKeyword2);
        etKeyword3 = (EditText) findViewById(R.id.createRoomKeyword3);
        etInformation = (EditText) findViewById(R.id.createRoomInformation);
        btnRoomNamdChk = (Button) findViewById(R.id.createRoomNameChk);
        btnCreateRoom = (Button) findViewById(R.id.createRoomOk);
        btnCreateCancel=(Button)findViewById(R.id.createRoomCancel);
        Spinner roomCategory = (Spinner) findViewById(R.id.createRoomCategory);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.ints, android.R.layout.simple_spinner_item);
        Spinner maxMem = (Spinner) findViewById(R.id.createRoomMaxMember);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.peo, android.R.layout.simple_spinner_item);
        Spinner locateSi = (Spinner) findViewById(R.id.createRoomLocateSi);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this, R.array.si, android.R.layout.simple_spinner_item);
        Spinner locateGu = (Spinner) findViewById(R.id.createRoomLocateGu);
        ArrayAdapter adapter4 = ArrayAdapter.createFromResource(this, R.array.gu, android.R.layout.simple_spinner_item);

        btnCreateRoom.setEnabled(false);//방등록 비활성화
        //방 이름 중복 검사
        btnRoomNamdChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> roomChk = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();

                roomChk.add(new BasicNameValuePair("roomName", etRoomName.getText().toString()));
                int flag = conn.doCheck(new ServerUrl().getServerUrl() + "roomCheck.do", roomChk);

                if (flag == VALID) {// 사용가능하면 등록 활성화
                    Toast.makeText(getApplicationContext(), "생성 가능", Toast.LENGTH_SHORT).show();
                    btnCreateRoom.setEnabled(true);
                } else if (flag == INVALID) {// 불가능하면 비활성화
                    Toast.makeText(getApplicationContext(), "생성 불가", Toast.LENGTH_SHORT).show();
                    btnCreateRoom.setEnabled(false);
                    etRoomName.setText("");
                    etKeyword1.setText("");
                    etKeyword2.setText("");
                    etKeyword3.setText("");
                    etInformation.setText("");
                }
            }
        });

        //방 등록
        btnCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectServer conn=new ConnectServer();
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();

                String strKeyword="";
                if(!etKeyword1.getText().equals(""))
                    strKeyword += etKeyword1.getText().toString()+",";
                if(!etKeyword2.getText().equals(""))
                    strKeyword += etKeyword2.getText().toString()+",";
                if(!etKeyword3.getText().equals(""))
                    strKeyword += etKeyword3.getText().toString()+",";
                info.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                info.add(new BasicNameValuePair("roomName", etRoomName.getText().toString()));
                info.add(new BasicNameValuePair("roomCategory", strCategory));
                info.add(new BasicNameValuePair("roomKeyword", strKeyword));
                info.add(new BasicNameValuePair("roomLocate", strSi + " " + strGu));
                info.add(new BasicNameValuePair("roomMemCnt", strMaxmem));
                info.add(new BasicNameValuePair("roomContent", etInformation.getText().toString()));
                int flag = conn.login(new ServerUrl().getServerUrl() + "roomRegister.do", info);

                if (flag==SUCCESS) {
                    finish();
                }
                else if (flag==FAIL) {
                    Toast.makeText(getApplicationContext(), "방 등록 실패", Toast.LENGTH_SHORT).show();
                    btnCreateRoom.setEnabled(false);
                    etRoomName.setText("");
                    etKeyword1.setText("");
                    etKeyword2.setText("");
                    etKeyword3.setText("");
                    etInformation.setText("");
                }
            }
        });
        //방 등록 취소
        btnCreateCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //방 관심분야
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomCategory.setAdapter(adapter1);
        roomCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategory = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //인원 설정
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxMem.setAdapter(adapter2);
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
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locateSi.setAdapter(adapter3);
        locateSi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSi = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locateGu.setAdapter(adapter4);
        locateGu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strGu = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}