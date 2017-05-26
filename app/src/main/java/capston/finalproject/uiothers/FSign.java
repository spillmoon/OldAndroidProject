package capston.finalproject.uiothers;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;public class FSign extends Activity {
    //변수 선언
    EditText etSignName, etSignID, etSignPW, etPWChk, etSignEmail, etSignPhone, etSignAnswer;
    Button btnSignOk, btnIDChk, btnCancel;
    CheckBox ctSport, ctStudy, ctTravel, ctMovie, ctSong, ctinfant;
    RadioGroup agrp;
    Spinner signQuestion;
    int pos;
    int SUCCESS=1;
    int FAIL=2;
    int VALID=3;
    int INVALID=4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        getActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_fsign);
        //http 통신을 위한 코드
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //변수 초기화
        etSignName = (EditText) findViewById(R.id.signName);//이름
        etSignID = (EditText) findViewById(R.id.signID);//id
        etSignPW = (EditText) findViewById(R.id.signPW);//pw
        etPWChk = (EditText) findViewById(R.id.pwChk);//pw check
        etSignEmail = (EditText) findViewById(R.id.signEmail);//email
        etSignPhone = (EditText) findViewById(R.id.signPhone);//phone
        etSignAnswer = (EditText) findViewById(R.id.signAnswer);//answer
        ctSport = (CheckBox) findViewById(R.id.signctSport);
        ctStudy = (CheckBox)findViewById(R.id.signctStudy);
        ctTravel = (CheckBox)findViewById(R.id.signctTravel);
        ctMovie = (CheckBox)findViewById(R.id.signctMovie);
        ctSong = (CheckBox)findViewById(R.id.signctSong);
        ctinfant = (CheckBox)findViewById(R.id.signctInfant);
        agrp=(RadioGroup)findViewById(R.id.signalrg);
        btnSignOk = (Button) findViewById(R.id.signOkBtn);
        btnIDChk = (Button) findViewById(R.id.idChk);
        btnCancel = (Button) findViewById(R.id.signCancel);
        signQuestion = (Spinner) findViewById(R.id.signQuestion);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.questions, android.R.layout.simple_spinner_item);

        btnSignOk.setEnabled(false);//가입 버튼 비활성화
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signQuestion.setAdapter(adapter1);
        signQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = parent.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //id check
        btnIDChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> idvalue = new ArrayList<NameValuePair>();
                idvalue.add(new BasicNameValuePair("isID", etSignID.getText().toString()));
                ConnectServer conn = new ConnectServer();
                int flag = conn.doCheck(new ServerUrl().getServerUrl() + "idCheck.do", idvalue);
                System.out.println(flag);
                if(flag == VALID) {
                    Toast.makeText(getApplicationContext(), "사용 가능", Toast.LENGTH_SHORT).show();
                    btnSignOk.setEnabled(true);
                }
                else if(flag == INVALID){
                    Toast.makeText(getApplicationContext(),"사용 불가", Toast.LENGTH_SHORT).show();
                    btnSignOk.setEnabled(false);//가입 비활성화
                    etSignID.setText("");
                }
            }
        });
        //가입 정보 전송
        btnSignOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전송 정보 저장
                ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
                signInfo.add(new BasicNameValuePair("memName", etSignName.getText().toString()));
                signInfo.add(new BasicNameValuePair("memID", etSignID.getText().toString()));
                signInfo.add(new BasicNameValuePair("memPW", etSignPW.getText().toString()));
                signInfo.add(new BasicNameValuePair("memEmail", etSignEmail.getText().toString()));
                signInfo.add(new BasicNameValuePair("memPhone", etSignPhone.getText().toString()));
                String chkboxval = "";
                if (ctSport.isChecked())
                    chkboxval += "스포츠,";
                if (ctStudy.isChecked())
                    chkboxval += "학습,";
                if (ctTravel.isChecked())
                    chkboxval += "여행,";
                if (ctMovie.isChecked())
                    chkboxval += "영화,";
                if (ctSong.isChecked())
                    chkboxval += "음악,";
                if (ctinfant.isChecked())
                    chkboxval += "육아";
                signInfo.add(new BasicNameValuePair("memInterest", chkboxval));
                signInfo.add(new BasicNameValuePair("alertChk", String.valueOf(agrp.indexOfChild(agrp.findViewById(agrp.getCheckedRadioButtonId())))));
                signInfo.add(new BasicNameValuePair("identiQ", String.valueOf(pos)));
                signInfo.add(new BasicNameValuePair("identiA", etSignAnswer.getText().toString()));
                if (etSignPW.getText().toString().equals(etPWChk.getText().toString())) {//비번이 일치하면
                    ConnectServer conn=new ConnectServer();
                    int flag =conn.getSuccessFail(new ServerUrl().getServerUrl()+"register.do",signInfo);
                    if(flag==SUCCESS) {
                        finish();
                    }
                    else if(flag==FAIL){
                        Toast.makeText(getApplicationContext(),"가입 실패",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "check password", Toast.LENGTH_SHORT).show();
                    etSignPW.setText("");
                    etPWChk.setText("");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
