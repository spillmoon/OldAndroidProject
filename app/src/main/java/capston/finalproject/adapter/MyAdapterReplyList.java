package capston.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.uiboard.BoardReReply;
import capston.finalproject.uiboard.BoardReplyEditDialog;
import capston.finalproject.utils.Answer;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterReplyList extends BaseAdapter {
    String Save_folder = "/MemoryShare";
    String Save_Path;
    int LayoutId;
    SharedPreferences pref;
    DownloadThread dThread;
    Context context;
    Vector<Answer> ReContent;
    LayoutInflater inflater;
    TextView ansMem,ansContent,date,file;
    public Button down,edit,delete,rere;
    int SUCCESS=1;
    int FAIL=2;

    public MyAdapterReplyList(Context context, int LayoutId, Vector<Answer> ReContent){
        this.context=context;
        this.LayoutId=LayoutId;
        this.ReContent=ReContent;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ReContent.size();
    }

    @Override
    public Object getItem(int position) {
        return ReContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(ReContent.get(position).getAnsNo());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView= inflater.inflate(LayoutId,parent,false);

        ansMem=(TextView)convertView.findViewById(R.id.ansMem);
        ansContent=(TextView)convertView.findViewById(R.id.ansContent);
        date=(TextView)convertView.findViewById(R.id.ansDate);
        file=(TextView)convertView.findViewById(R.id.arcName);
        down=(Button)convertView.findViewById(R.id.replyattachdown);
        edit=(Button)convertView.findViewById(R.id.replyedit);
        delete=(Button)convertView.findViewById(R.id.replydelete);
        rere=(Button)convertView.findViewById(R.id.rereply);
        pref=context.getSharedPreferences("Test", 0);

        ansMem.setText(ReContent.get(position).getAnsMem());
        ansContent.setText(ReContent.get(position).getAnsContent());
        date.setText(ReContent.get(position).getAnsDate());

        if(ReContent.get(position).getArcName().equals("null")) {
            file.setText("");
            down.setEnabled(false);
        }
        else {
            file.setText(ReContent.get(position).getArcName());
            down.setEnabled(true);
        }

        if(checkAuthor()){
            edit.setEnabled(true);
            delete.setEnabled(true);
        }
        else{
            edit.setEnabled(false);
            delete.setEnabled(false);
        }

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDownload(ReContent.get(position).getArcName());
            }
        });
        rere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoardReReply.class);
                intent.putExtra("ansNo",ReContent.get(position).getAnsNo());
                intent.putExtra("boardNo",pref.getString("boardNo", ""));
                context.startActivity(intent);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BoardReplyEditDialog.class);
                intent.putExtra("ansNo", ReContent.get(position).getAnsNo());
                intent.putExtra("file", ReContent.get(position).getArcName());
                context.startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> delinfo = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();
                delinfo.add(new BasicNameValuePair("ansNo", ReContent.get(position).getAnsNo()));
                int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "answerDelete.do", delinfo);
                if (flag == SUCCESS) {
                    ReContent.remove(ReContent.get(position));
                    notifyDataSetChanged();
                } else if (flag == FAIL) {
                    Toast.makeText(context, "delete fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    public boolean checkAuthor(){
        if(pref.getString("ID","").equals(ansMem.getText().toString()))
            return true;
        return false;
    }
    public void executeDownload(String filename){
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            Save_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + Save_folder;
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
                Toast.makeText(context, "완료", Toast.LENGTH_SHORT).show();
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
