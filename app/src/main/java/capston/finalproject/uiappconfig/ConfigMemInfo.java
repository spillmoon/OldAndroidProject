package capston.finalproject.uiappconfig;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.uiothers.FLogin;
import capston.finalproject.utils.ServerUrl;

public class ConfigMemInfo extends Activity {
    EditText etSignName, etSignID, etSignPW, etPWChk, etSignEmail, etSignPhone, etSignAnswer;
    Button btnSignOk, btnCancel, bye, yes, no;
    RadioGroup agrp;
    Spinner signQuestion;
    ArrayAdapter adapter1;
    SharedPreferences pref;
    int pos;
    int SUCCESS=1;
    int FAIL=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configmeminfo);
        //http 통신을 위한 코드
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //사용할 변수 등록
        etSignName = (EditText) findViewById(R.id.changeName);//이름
        etSignID = (EditText) findViewById(R.id.changeID);//id
        etSignPW = (EditText) findViewById(R.id.changePW);//pw
        etPWChk = (EditText) findViewById(R.id.changepwChk);//pw check
        etSignEmail = (EditText) findViewById(R.id.changeEmail);//email
        etSignPhone = (EditText) findViewById(R.id.changePhone);//phone
        etSignAnswer = (EditText) findViewById(R.id.changeAnswer);//answer
        agrp=(RadioGroup)findViewById(R.id.changealrg);
        btnSignOk = (Button) findViewById(R.id.changeOkBtn);
        btnCancel = (Button) findViewById(R.id.changeCancel);
        bye=(Button)findViewById(R.id.byebye);
        signQuestion = (Spinner) findViewById(R.id.changeQuestion);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.questions, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signQuestion.setAdapter(adapter1);
        pref=getSharedPreferences("Test",0);

        ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
        ConnectServer conn=new ConnectServer();

        signInfo.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        jsonParser(conn.getJsonArray(new ServerUrl().getServerUrl()+"memberDataBring.do",signInfo));

        signQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = parent.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSignOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();

                signInfo.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                signInfo.add(new BasicNameValuePair("memPW", etSignPW.getText().toString()));
                signInfo.add(new BasicNameValuePair("memEmail", etSignEmail.getText().toString()));
                signInfo.add(new BasicNameValuePair("memPhone", etSignPhone.getText().toString()));
                signInfo.add(new BasicNameValuePair("alertChk", String.valueOf(agrp.indexOfChild(agrp.findViewById(agrp.getCheckedRadioButtonId())))));
                signInfo.add(new BasicNameValuePair("identiQ", String.valueOf(pos)));
                signInfo.add(new BasicNameValuePair("identiA", etSignAnswer.getText().toString()));
                if (etSignPW.getText().toString().equals(etPWChk.getText().toString())) {//비번이 일치하면
                    int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "memberEdit.do", signInfo);

                    if (flag==SUCCESS) {
                        Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (flag==FAIL) {
                        Toast.makeText(getApplicationContext(), "수정 실패", Toast.LENGTH_SHORT).show();
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
        bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ConfigMemInfo.this);
                dialog.setContentView(R.layout.activity_configappoutchk);
                dialog.setTitle("탈퇴 확인");
                dialog.show();

                yes=(Button)dialog.findViewById(R.id.appoutY);
                no=(Button)dialog.findViewById(R.id.appoutN);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectServer conn = new ConnectServer();
                        ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
                        signInfo.add(new BasicNameValuePair("memID", pref.getString("memID","")));
                        int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "memberWithdraw.do", signInfo);

                        if (flag == SUCCESS) {
                            Toast.makeText(getApplicationContext(), "withdraw success", Toast.LENGTH_SHORT).show();
                            Intent new_intent = new Intent(ConfigMemInfo.this, FLogin.class);
                            new_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(new_intent);
                        } else if (flag == FAIL) {
                            Toast.makeText(getApplicationContext(), "withdraw fail", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "생성한 방 지워주세요, 참여한 방 나가주세요, 신청 취소해주세요", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            etSignName.setText(jsoninfo.getString("memName"));
            etSignID.setText(jsoninfo.getString("memID"));
            etSignEmail.setText(jsoninfo.getString("memEmail"));
            etSignPhone.setText(jsoninfo.getString("memPhone"));
            if(jsoninfo.getString("alertChk").equals("0"))
                agrp.check(R.id.radio1);
            else if(jsoninfo.getString("alertChk").equals("1"))
                agrp.check(R.id.radio2);
            signQuestion.setSelection(Integer.parseInt(jsoninfo.getString("identiQ")));
            etSignAnswer.setText(jsoninfo.getString("identiA"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etSignName.setEnabled(false);
        etSignID.setEnabled(false);
    }
}
