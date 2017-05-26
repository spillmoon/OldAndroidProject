package capston.finalproject.uiboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import capston.finalproject.R;
import capston.finalproject.utils.ServerUrl;

public class BoardReplyEditDialog extends Activity {
    EditText editcontent;
    TextView attachfilename;
    Button reset, attach, yes, no;
    String absolutePath, filename, filepath;
    String ansNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardreplyeditdialog);

        Display display= ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        getWindow().getAttributes().width=600;
        getWindow().getAttributes().height=450;

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        ansNo = intent.getStringExtra("ansNo");
        filename=intent.getStringExtra("file");

        editcontent = (EditText)findViewById(R.id.replycontentedit);
        attachfilename=(TextView)findViewById(R.id.attachreplyfilenameedit);
        reset=(Button)findViewById(R.id.resetreplyattach);
        attach=(Button)findViewById(R.id.attachtoreplyedit);
        yes = (Button)findViewById(R.id.yesreplyedit);
        no = (Button)findViewById(R.id.noreplyedit);

        if(filename.equals("null")) {
            attachfilename.setText("");
        }
        else{
            attachfilename.setText(filename);
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachfilename.setText("");
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(BoardReplyEditDialog.this, BoardFileBrowser.class);
                startActivityForResult(act, 1);
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!attachfilename.getText().toString().equals("")) {
                    String fileName = absolutePath;
                    try {
                        String url = new ServerUrl().getServerUrl()+"answerEdit.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansNo", ansNo));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansContent", editcontent.getText().toString()));
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
                        String url = new ServerUrl().getServerUrl()+"answerEdit.do";
                        // 데이터 구분문자. 아무거나 정해도 상관없지만 꼭 나타날 수 없는 형태의 문자열로 정한다.
                        String boundary = "^******^";
                        // 데이터 경계선
                        String delimiter = "\r\n--" + boundary + "\r\n";
                        StringBuffer postDataBuilder = new StringBuffer();
                        // 추가하고 싶은 Key & Value 추가
                        // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansNo", ansNo));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("ansContent", editcontent.getText().toString()));
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
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            absolutePath = data.getStringExtra("absolute");
            filename = absolutePath.substring(absolutePath.lastIndexOf("/"));
            filepath = absolutePath.substring(0, absolutePath.length() - filename.length() + 1);
            filename = filename.substring(1, filename.length() - 1);
            attachfilename.setText(filename);
        }
    }
}
