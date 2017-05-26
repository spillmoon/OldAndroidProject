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
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class BoardContentEdit extends Activity {
    String no, pullPath, path;
    SharedPreferences pref;
    EditText title, editcontent;
    TextView folder, filename;
    Button editfile, reset, edit, cancel;
    Boolean isfile=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardcontentedit);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        no = intent.getStringExtra("boardNo");

        pref = getSharedPreferences("Test", 0);
        title=(EditText)findViewById(R.id.boardedittitle);
        editcontent=(EditText)findViewById(R.id.boardeditcontent);
        folder=(TextView)findViewById(R.id.boardeditfolder);
        filename=(TextView)findViewById(R.id.boardeditfilename);
        editfile=(Button)findViewById(R.id.boardeditfile);
        reset=(Button)findViewById(R.id.boardeditreset);
        edit=(Button)findViewById(R.id.boardedittoserver);
        cancel=(Button)findViewById(R.id.boardeditcancel);

        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        ConnectServer conn= new ConnectServer();
        info.add(new BasicNameValuePair("boardNo", no));
        setInfo(conn.getJsonArray(new ServerUrl().getServerUrl() + "boardRead.do", info));

        editfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(BoardContentEdit.this, BoardFileBrowser.class);
                startActivityForResult(act, 1);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename.setText("");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!filename.getText().toString().equals("")&&!isfile){
                    String fileName = path+filename.getText().toString();
                    try {
                        String url = new ServerUrl().getServerUrl()+"boardEdit.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardNo", no));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardTitle", title.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardContent", editcontent.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("arcRoute", path));
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
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
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
                        conn.getInputStream();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }else if (!filename.getText().toString().equals("")&&isfile) {
                    String fileName = pullPath;
                    try {
                        String url = new ServerUrl().getServerUrl()+"boardEdit.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardNo", no));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardTitle", title.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardContent", editcontent.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("arcRoute", path));
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
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
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
                } else if(filename.getText().toString().equals("")) {
                    String fileName = pullPath;
                    try {
                        String url = new ServerUrl().getServerUrl()+"boardEdit.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardNo", no));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardTitle", title.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("boardContent", editcontent.getText().toString()));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("arcRoute", path));
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
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                        // 전송 작업 시작
                        //FileInputStream in = new FileInputStream(fileName);
                        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
                        // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
                        out.writeUTF(postDataBuilder.toString());
                        out.writeBytes(delimiter); // 반드시 작성해야 한다.
                        out.flush();
                        out.close();
                        conn.getInputStream();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                Intent intent =new Intent(BoardContentEdit.this, Board.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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
            pullPath = data.getStringExtra("absolute");
            String fileName = pullPath.substring(pullPath.lastIndexOf("/"));
            path = pullPath.substring(0,pullPath.length()-fileName.length()+1);
            fileName = fileName.substring(1, fileName.length() - 1);
            filename.setText(fileName);
            isfile=true;
        }
    }

    public void setInfo(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            folder.setText(jsoninfo.getString("folderName"));
            title.setText(jsoninfo.getString("boardTitle"));
            filename.setText(jsoninfo.getString("arcName"));
            editcontent.setText(jsoninfo.getString("boardContent"));
            no=jsoninfo.getString("boardNo");
            path = jsoninfo.getString("arcRoute");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(filename.getText().toString().equals("null"))
            filename.setText("");
    }
}
