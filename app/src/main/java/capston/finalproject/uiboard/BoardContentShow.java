package capston.finalproject.uiboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.utils.ServerUrl;

public class BoardContentShow extends Activity {
    TextView folder, title, author, date, attach, contents;
    Button download,edit,reply,delete;
    String Save_folder = "/MemoryShare";
    String Save_Path;
    String number;
    DownloadThread dThread;
    SharedPreferences pref;
    int SUCCESS=1;
    int FAIL=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardcontentshow);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        String contentNo = intent.getStringExtra("boardNo");
        ArrayList<NameValuePair> info=new ArrayList<NameValuePair>();
        ConnectServer conn=new ConnectServer();
        info.add(new BasicNameValuePair("boardNo", contentNo));

        folder=(TextView)findViewById(R.id.boardshowfolder);
        title=(TextView)findViewById(R.id.boardshowtitle);
        author=(TextView)findViewById(R.id.boardshowauthor);
        date=(TextView)findViewById(R.id.boardshowdate);
        attach=(TextView)findViewById(R.id.boardshowattachfile);
        contents=(TextView)findViewById(R.id.boardshowcontent);
        download=(Button)findViewById(R.id.boardshowdown);
        edit=(Button)findViewById(R.id.boardcontentedit);
        reply=(Button)findViewById(R.id.boardreply);
        delete=(Button)findViewById(R.id.boardcontentdelete);

        jsonParser(conn.getJsonArray(new ServerUrl().getServerUrl()+"boardRead.do",info));

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BoardContentShow.this, BoardReply.class);
                intent.putExtra("boardNo",number);
                startActivity(intent);
            }
        });

        if(attach.getText().toString().equals("null")) {
            attach.setText("");
            download.setEnabled(false);
        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDownload(attach.getText().toString());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAuthor()) {
                    ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                    ConnectServer conn=new ConnectServer();
                    info.add(new BasicNameValuePair("boardNo", number));
                    int flag=conn.getSuccessFail(new ServerUrl().getServerUrl()+"boardDelete.do",info);

                    if (flag==SUCCESS) {
                        Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if(flag==FAIL){
                        Toast.makeText(getApplicationContext(), "delete fail", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"삭제권한없음",Toast.LENGTH_SHORT).show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAuthor()) {
                    Intent intent = new Intent(BoardContentShow.this, BoardContentEdit.class);
                    intent.putExtra("boardNo", number);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(),"수정권한없음",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkAuthor(){
        pref=getSharedPreferences("Test",0);
        if(pref.getString("ID","").equals(author.getText().toString()))
            return true;
        return false;
    }

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONObject jsoninfo = jsonob.getJSONObject("info");
            folder.setText(jsoninfo.getString("folderName"));
            title.setText(jsoninfo.getString("boardTitle"));
            author.setText(jsoninfo.getString("boardMem"));
            date.setText(jsoninfo.getString("boardDate"));
            attach.setText(jsoninfo.getString("arcName"));
            contents.setText(jsoninfo.getString("boardContent"));
            number=jsoninfo.getString("boardNo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void executeDownload(String filename){
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            Save_Path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + Save_folder;
        }
        File dir = new File(Save_Path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서
        // 없으면 다운받고 있으면 해당 파일 실행시킴.
        if (new File(Save_Path + "/" + filename).exists() == false) {
            dThread = new DownloadThread(new ServerUrl().getServerUrl()+"data/"+filename, Save_Path + "/" + filename);
            dThread.start();
            try {
                dThread.join();
                Toast.makeText(getApplicationContext(), "완료", Toast.LENGTH_SHORT).show();
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
        }

        @Override
        public void run() {
            URL imgurl;
            int Read;
            try {
                imgurl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();
            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
