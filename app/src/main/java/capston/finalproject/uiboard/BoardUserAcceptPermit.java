package capston.finalproject.uiboard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.utils.ServerUrl;

public class BoardUserAcceptPermit extends Activity {
    Button yes,no;
    TextView roomname, interesting, keyword, master, locate, memcnt;
    ArrayList<NameValuePair> user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarduseracceptpermit);

        SharedPreferences pref =getSharedPreferences("Test",0);
        setTitle(pref.getString("RoomName","")+" 권한 수락/거절");

        roomname=(TextView)findViewById(R.id.roomname);
        interesting=(TextView)findViewById(R.id.interesting);
        keyword=(TextView)findViewById(R.id.keyword);
        master=(TextView)findViewById(R.id.master);
        locate=(TextView)findViewById(R.id.locate);
        memcnt=(TextView)findViewById(R.id.memcnt);

        yes=(Button)findViewById(R.id.leaderchangeok);
        no=(Button)findViewById(R.id.leaderchangeno);

        user = new ArrayList<NameValuePair>();
        user.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        user.add(new BasicNameValuePair("roomName", pref.getString("RoomName","")));

        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"leaderTransReq.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            InputStream stream = resEntity.getContent();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream, "EUC-KR"));
            String line;
            String result = "";
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            jsonParser(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HttpClient http=new DefaultHttpClient();
                    HttpParams params=http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"leaderApplyAccept.do");
                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost= http.execute(httpPost);
                    HttpEntity resEntity=responsePost.getEntity();
                    String result= EntityUtils.toString(resEntity);
                    result=result.trim();
                    if("success".equals(result)){
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HttpClient http=new DefaultHttpClient();
                    HttpParams params=http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"leaderApplyRefusal.do");
                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost= http.execute(httpPost);
                    HttpEntity resEntity=responsePost.getEntity();
                    String result= EntityUtils.toString(resEntity);
                    result=result.trim();
                    if("success".equals(result)){
                        Toast.makeText(getApplicationContext(), "거절 성공", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"거절 실패",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            if(jsoninfo.isNull("roomName"))
                yes.setEnabled(false);
            else {
                yes.setEnabled(true);
                roomname.setText(jsoninfo.getString("roomName"));
                interesting.setText(jsoninfo.getString("roomCategory"));
                keyword.setText(jsoninfo.getString("roomKeyword"));
                master.setText(jsoninfo.getString("leader"));
                locate.setText(jsoninfo.getString("roomLocate"));
                memcnt.setText(jsoninfo.getString("currentMemCnt") + "/" + jsoninfo.getString("roomMemCnt"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
