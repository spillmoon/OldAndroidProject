package capston.finalproject.uiappconfig;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class ConfigCategory extends Activity {
    CheckBox ctSport, ctStudy, ctTravel, ctMovie, ctSong, ctinfant;
    Button ok, no;
    SharedPreferences pref;
    int SUCCESS=1;
    int FAIL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configcategory);
        ctSport = (CheckBox) findViewById(R.id.changectSport);
        ctStudy = (CheckBox) findViewById(R.id.changectStudy);
        ctTravel = (CheckBox) findViewById(R.id.changectTravel);
        ctMovie = (CheckBox) findViewById(R.id.changectMovie);
        ctSong = (CheckBox) findViewById(R.id.changectSong);
        ctinfant = (CheckBox) findViewById(R.id.changectInfant);
        ok=(Button)findViewById(R.id.categorychangeok);
        no=(Button)findViewById(R.id.categorychangeno);
        pref=getSharedPreferences("Test",0);

        ArrayList<NameValuePair> idvalue = new ArrayList<NameValuePair>();
        idvalue.add(new BasicNameValuePair("memID", pref.getString("ID", "")));

        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"memberInterestBring.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(idvalue, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            String result = EntityUtils.toString(resEntity);
            result = result.trim();
            System.out.println(result);
            StringTokenizer st=new StringTokenizer(result,",");
            while(st.hasMoreTokens()){
                String token=st.nextToken();
                if(token.equals(ctSport.getText().toString()))
                    ctSport.setChecked(true);
                if(token.equals(ctStudy.getText().toString()))
                    ctStudy.setChecked(true);
                if(token.equals(ctTravel.getText().toString()))
                    ctTravel.setChecked(true);
                if(token.equals(ctMovie.getText().toString()))
                    ctMovie.setChecked(true);
                if(token.equals(ctSong.getText().toString()))
                    ctSong.setChecked(true);
                if(token.equals(ctinfant.getText().toString()))
                    ctinfant.setChecked(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectServer conn=new ConnectServer();
                ArrayList<NameValuePair> signInfo = new ArrayList<NameValuePair>();
                String chkboxval = "";
                if (ctSport.isChecked())
                    chkboxval += "스포츠,";
                if (ctStudy.isChecked())
                    chkboxval += "학습,";
                if (ctTravel.isChecked())
                    chkboxval += "여행,";
                if (ctMovie.isChecked())
                    chkboxval += "여행,";
                if (ctSong.isChecked())
                    chkboxval += "음악,";
                if (ctinfant.isChecked())
                    chkboxval += "육아,";
                System.out.println("보내기"+chkboxval);
                signInfo.add(new BasicNameValuePair("memID",pref.getString("ID","")));
                signInfo.add(new BasicNameValuePair("memInterest", chkboxval));
                int flag=conn.getSuccessFail(new ServerUrl().getServerUrl()+"memberInterestUpdate.do", signInfo);

                if (flag==SUCCESS) {
                    Toast.makeText(getApplicationContext(), "change success", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (flag==FAIL) {
                    Toast.makeText(getApplicationContext(), "change fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
