package capston.finalproject.uiboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterRereplyList;
import capston.finalproject.utils.Answer;
import capston.finalproject.utils.ServerUrl;

public class BoardReReply extends Activity {
    String ansNo,boardNo;
    Vector<Answer> answerList;
    Button upload;
    EditText replycontent;
    ListView replyList;
    MyAdapterRereplyList mAdapter;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardrereply);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        pref = getSharedPreferences("Test", 0);
        Intent intent = getIntent();
        ansNo = intent.getStringExtra("ansNo");
        boardNo = intent.getStringExtra("boardNo");

        upload = (Button) findViewById(R.id.uploadrereply);
        replycontent = (EditText) findViewById(R.id.rereplycontent);
        replyList = (ListView) findViewById(R.id.rereplyList);
        answerList = new Vector<Answer>();
    }

    @Override
    public void onResume() {
        super.onResume();

        answerList.clear();
        //댓글 방에서 댓글 리스트를 가져온다
        ArrayList<BasicNameValuePair> info=new ArrayList<BasicNameValuePair>();
        info.add(new BasicNameValuePair("ansNo", ansNo));
        try{
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"ansAnswerList.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            InputStream stream = resEntity.getContent();
            //JSON string ������
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream,"EUC-KR"));
            String line;
            String result="";
            while((line=bufreader.readLine())!=null){
                result+=line;
            }
            jsonParser(result);//json parsing
        }catch (Exception e){
            e.printStackTrace();
        }

        mAdapter=new MyAdapterRereplyList(BoardReReply.this,R.layout.listitem_rereply,answerList);
        replyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        replyList.setAdapter(mAdapter);

        //댓글 등록 첨부파일 있을 때 없을 때 구분
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BasicNameValuePair> info=new ArrayList<BasicNameValuePair>();
                info.add(new BasicNameValuePair("boardNo",boardNo));
                info.add(new BasicNameValuePair("answerAnsNo", ansNo));
                info.add(new BasicNameValuePair("memID",pref.getString("ID","")));
                info.add(new BasicNameValuePair("ansContent",replycontent.getText().toString()));
                try{
                    HttpClient http=new DefaultHttpClient();
                    HttpParams params=http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"ansAnswerWriting.do");
                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntitiy = responsePost.getEntity();
                    String result = EntityUtils.toString(resEntitiy);
                    result = result.trim();
                    if ("success".equals(result)) {
                        onPause();
                        replycontent.setText("");
                        onResume();
                    } else if ("fail".equals(result)) {
                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    //댓글 파싱
    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"ansNo","ansMem", "ansContent", "ansDate"};
            String[][] rere = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        rere[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < rere.length; i++) {
                answerList.add(new Answer(rere[i][0], rere[i][1], rere[i][2], rere[i][3]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
