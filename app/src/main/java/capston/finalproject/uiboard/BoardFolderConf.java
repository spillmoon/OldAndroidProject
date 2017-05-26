package capston.finalproject.uiboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import org.apache.http.NameValuePair;
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
import capston.finalproject.adapter.MyAdapterFolderList;
import capston.finalproject.utils.BoardUtil;
import capston.finalproject.utils.ServerUrl;

public class BoardFolderConf extends Activity {
    MyAdapterFolderList adapter;
    ListView showFolder;
    Vector<BoardUtil> folderList;
    Button addfolder, deletefolder;
    SharedPreferences pref;
    String[][] parsered;
    String[] delfolder;
    ArrayList<NameValuePair> name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardfolderconf);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        showFolder = (ListView) findViewById(R.id.boardfolderList);
        addfolder = (Button) findViewById(R.id.boardaddfolder);
        deletefolder = (Button) findViewById(R.id.boarddeletefolder);
        pref = getSharedPreferences("Test", 0);
        folderList = new Vector<BoardUtil>();
    }

    @Override
    public void onResume() {
        super.onResume();

        folderList.clear();
        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "showFolderList.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            InputStream stream = resEntity.getContent();
            //JSON string ������
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream, "EUC-KR"));
            String line;
            String result = "";
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            jsonParser(result);//json parsing
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        adapter = new MyAdapterFolderList(BoardFolderConf.this, R.layout.listitem_folderlist, folderList);
        showFolder.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showFolder.setAdapter(adapter);

        addfolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(BoardFolderConf.this);
                dialog.setContentView(R.layout.dialog_addfolder);
                dialog.setTitle("폴더 추가");
                dialog.show();

                final EditText newname = (EditText) dialog.findViewById(R.id.newfolder);
                Button yes = (Button) dialog.findViewById(R.id.yesadd);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
                        info.add(new BasicNameValuePair("folderName", newname.getText().toString()));
                        try {// http 통신준비
                            HttpClient http = new DefaultHttpClient();
                            HttpParams params = http.getParams();
                            HttpConnectionParams.setConnectionTimeout(params, 5000);
                            HttpConnectionParams.setSoTimeout(params, 5000);
                            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "addFolder.do");
                            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
                            httpPost.setEntity(entityRequest);
                            //서버로 전송 후 결과값 받아옴
                            HttpResponse responsePost = http.execute(httpPost);
                            HttpEntity resEntity = responsePost.getEntity();
                            String result = EntityUtils.toString(resEntity);
                            result = result.trim();
                            //결과값(플래그)에 따라 분기(성공, wrong id/pw)
                            if ("success".equals(result)) {
                                onResume();
                            } else {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                Button no = (Button) dialog.findViewById(R.id.noadd);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        deletefolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFolderList(delfolder);
            }
        });
    }

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] roomInfo = {"folderNo", "folderName"};
            parsered = new String[jarr.length()][roomInfo.length];
            delfolder = new String[jarr.length()];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
                delfolder[i] = parsered[i][1];
            }
            for (int i = 0; i < parsered.length; i++) {
                folderList.add(new BoardUtil(parsered[i][0], parsered[i][1]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showFolderList(String[] folderlist) {
        AlertDialog.Builder adb = new AlertDialog.Builder(BoardFolderConf.this);
        adb.setTitle("폴더 삭제");
        adb.setSingleChoiceItems(folderlist, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = new ArrayList<NameValuePair>();
                name.add(new BasicNameValuePair("folderNo", parsered[which][0]));
            }
        });
        adb.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    HttpClient http = new DefaultHttpClient();
                    HttpParams params = http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "deleteFolder.do");
                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name, "EUC-KR");
                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntity = responsePost.getEntity();
                    String result = EntityUtils.toString(resEntity);
                    result = result.trim();
                    if ("success".equals(result)) {
                        onResume();
                    } else {
                        Toast.makeText(getApplicationContext(), "폴더삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }
}
