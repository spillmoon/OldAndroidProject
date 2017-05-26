package capston.finalproject.uiappconfig;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ConfigMemChk extends Activity {
    int pos;
    Spinner question;
    EditText answer;
    Button ok, can;
    SharedPreferences pref;
    int SUCCESS=1;
    int FAIL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configmemchk);


        pref = getSharedPreferences("Test", 0);

        question = (Spinner)findViewById(R.id.CheckQuestion);
        answer = (EditText)findViewById(R.id.CheckAnswer);
        ok=(Button)findViewById(R.id.checkyoubtn);
        can=(Button)findViewById(R.id.checknobtn);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.questions, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        question.setAdapter(adapter1);
        question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = parent.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();
                info.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
                info.add(new BasicNameValuePair("identiQ", String.valueOf(pos)));
                info.add(new BasicNameValuePair("identiA", answer.getText().toString()));

                int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "memberIdentify.do", info);

                if (flag==SUCCESS) {
                    Intent intent = new Intent(ConfigMemChk.this,ConfigMemInfo.class);
                    startActivity(intent);
                    finish();
                } else if (flag==FAIL) {
                    Toast.makeText(getApplicationContext(), "인증정보가 틀렸습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
