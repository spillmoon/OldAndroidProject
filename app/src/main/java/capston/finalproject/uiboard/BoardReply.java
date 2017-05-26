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
import android.widget.TextView;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterReplyList;
import capston.finalproject.utils.Answer;
import capston.finalproject.utils.ServerUrl;

public class BoardReply extends Activity {
    String absolutePath, filename, filepath, contentNo;
    Vector<Answer> answerList;
    Button attach, upload;
    EditText replycontent;
    ListView replyList;
    MyAdapterReplyList mAdapter;
    TextView attachfilename;
    SharedPreferences pref;
    SharedPreferences.Editor pedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardreply);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        pref = getSharedPreferences("Test", 0);
        pedit = pref.edit();
        Intent intent = getIntent();
        contentNo = intent.getStringExtra("boardNo");
        pedit.putString("boardNo", contentNo);
        pedit.apply();

        attach = (Button) findViewById(R.id.attachtoreply);
        upload = (Button) findViewById(R.id.uploadreply);
        replycontent = (EditText) findViewById(R.id.replycontent);
        replyList = (ListView) findViewById(R.id.replyList);
        attachfilename = (TextView) findViewById(R.id.attachreplyfilename);
        answerList = new Vector<Answer>();
    }

    @Override
    public void onResume() {
        super.onResume();

        answerList.clear();
        //댓글 방에서 댓글 리스트를 가져온다
        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        info.add(new BasicNameValuePair("boardNo", contentNo));
        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(new ServerUrl().getServerUrl()+"answerList.do");
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

        mAdapter=new MyAdapterReplyList(BoardReply.this,R.layout.listitem_reply,answerList);
        replyList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        replyList.setAdapter(mAdapter);

        //댓글 파일첨부
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(BoardReply.this, BoardFileBrowser.class);
                startActivityForResult(act, 1);
            }
        });
        //댓글 등록 첨부파일 있을 때 없을 때 구분
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!attachfilename.getText().toString().equals("")) {
                    String fileName = absolutePath;
                    try {
                        String url = new ServerUrl().getServerUrl()+"answerWriting.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansMem", pref.getString("ID", "")));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansContent", replycontent.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardNo", contentNo));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("arcRoute", filepath));
                        postDataBuilder.append(delimiter);
                        // 파일 첨부
                        postDataBuilder.append(setFile("uploaded_file", fileName));
                        postDataBuilder.append("\r\n");
                        // 커넥션 생성 및 설정
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        // 전송 작업 시작
                        FileInputStream in = new FileInputStream(fileName);
                        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
                        // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
                        out.writeUTF(postDataBuilder.toString());
                        // 파일 복사 작업 시작
                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(in.available(), maxBufferSize);
                        byte[] buffer = new byte[bufferSize];
                        // 버퍼 크기만큼 파일로부터 바이트 데이터를 읽는다.
                        int byteRead = in.read(buffer, 0, bufferSize);
                        // 전송
                        while (byteRead > 0) {
                            out.write(buffer);
                            bufferSize = Math.min(in.available(), maxBufferSize);
                            byteRead = in.read(buffer, 0, bufferSize);
                        }
                        out.writeBytes(delimiter); // 반드시 작성해야 한다.
                        out.flush();
                        out.close();
                        in.close();
                        // 결과 반환 (HTTP RES CODE)
                        conn.getInputStream();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                else{
                    String fileName = absolutePath;
                    try {
                        String url = new ServerUrl().getServerUrl()+"answerWriting.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansMem", pref.getString("ID", "")));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansContent", replycontent.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardNo", contentNo));
                        postDataBuilder.append(delimiter);
                        // 파일 첨부
                        postDataBuilder.append(setFile("uploaded_file", fileName));
                        postDataBuilder.append("\r\n");
                        // 커넥션 생성 및 설정
                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        // 전송 작업 시작
                        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
                        // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
                        out.writeUTF(postDataBuilder.toString());
                        // 파일 복사 작업 시작
                        out.writeBytes(delimiter); // 반드시 작성해야 한다.
                        out.flush();
                        out.close();
                        // 결과 반환 (HTTP RES CODE)
                        conn.getInputStream();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                onPause();
                replycontent.setText("");
                attachfilename.setText("");
                onResume();
            }
        });
    }

    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n" + value;
    }

    public static String setFile(String key, String fileName) {
        return "Content-Disposition: form-data; name=\"" + key + "\";filename=\"" + fileName + "\"\r\n";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            absolutePath = data.getStringExtra("absolute");
            filename = absolutePath.substring(absolutePath.lastIndexOf("/"));
            filepath = absolutePath.substring(0, absolutePath.length() - filename.length() + 1);
            filename = filename.substring(1, filename.length() - 1);
            attachfilename.setText(filename);
        }
    }

    //댓글 파싱
    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"ansNo","ansMem", "ansContent", "ansDate","arcName","arcRoute","ansAnswerCount"};
            String[][] parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                answerList.add(new Answer(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3], parsered[i][4],parsered[i][5],parsered[i][6]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
