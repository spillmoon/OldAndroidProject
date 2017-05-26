package capston.finalproject.uiappconfig;

import android.app.Activity;
import android.app.Dialog;
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
import capston.finalproject.uiothers.FLogin;
import capston.finalproject.utils.ServerUrl;

public class ConfigApp extends Activity {
    Button isyou, change, logout;
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
        setContentView(R.layout.activity_configapp);

        pref = getSharedPreferences("Test", 0);
        isyou=(Button)findViewById(R.id.appconfigmembercheck);
        change=(Button)findViewById(R.id.appconfigchangecategory);
        logout=(Button)findViewById(R.id.appconfiglogout);

        isyou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ConfigApp.this);
                dialog.setContentView(R.layout.activity_configmemchk);
                dialog.setTitle("본인 인증");
                dialog.show();


                question = (Spinner)dialog.findViewById(R.id.CheckQuestion);
                answer = (EditText)dialog.findViewById(R.id.CheckAnswer);
                ok=(Button)dialog.findViewById(R.id.checkyoubtn);
                can=(Button)dialog.findViewById(R.id.checknobtn);
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                ArrayAdapter adapter1 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.questions, android.R.layout.simple_spinner_item);
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

                        if (flag == SUCCESS) {
                            Intent intent = new Intent(ConfigApp.this, ConfigMemInfo.class);
                            startActivity(intent);
                        } else if (flag == FAIL) {
                            Toast.makeText(getApplicationContext(), "인증정보가 틀렸습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(ConfigApp.this, ConfigCategory.class);
                startActivity(act);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act=new Intent(ConfigApp.this, FLogin.class);
                act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(act);
            }
        });
    }
}
