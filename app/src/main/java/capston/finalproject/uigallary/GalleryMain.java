package capston.finalproject.uigallary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import capston.finalproject.adapter.MyAdapterGalleryMain;
import capston.finalproject.utils.GalleryFolderInfo;
import capston.finalproject.utils.ServerUrl;

public class GalleryMain extends Activity {
    Vector<GalleryFolderInfo> item;
    String  FolderNo, SelectedFolderName, result;
    String InputFolderName;
    String[][] parsered;
    GridView gridview;
    Button ImageAddButton, FolderAddButton, GalleryManagement;
    SharedPreferences pref;
    MyAdapterGalleryMain FAdapter;
    final ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();
    @Override
    public void onResume(){
        super.onResume();
        item.clear();
        setGalleryMainGridView(user);
    }
    private void setGalleryMainGridView(final ArrayList<NameValuePair> user){

        try {
            HttpClient http = new DefaultHttpClient();

            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"galleryList.do");

            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            InputStream stream = resEntity.getContent();
            //JSON string ������
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream, "EUC-KR"));
            String line;
            result = "";
            while ((line = bufreader.readLine()) != null) {
                result += line;
            }
            jsonParser(result);//json parsing
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FAdapter = new MyAdapterGalleryMain(this, R.layout.gallerymaingridviewitem, item);
        gridview.setAdapter(FAdapter);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerymain);

        item = new Vector<GalleryFolderInfo>();
        pref = getSharedPreferences("Test", 0);
        setTitle(pref.getString("RoomName","")+" 갤러리");
        user.add(new BasicNameValuePair("roomName", pref.getString("RoomName", ""))); //서버로 보낼 방이름 add

        gridview = (GridView) findViewById(R.id.gridView);
        ImageAddButton = (Button) findViewById(R.id.addimagebut);
        FolderAddButton = (Button) findViewById(R.id.addfolder);
        GalleryManagement = (Button) findViewById(R.id.gallerysetting);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // add listener to button
        GalleryManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //갤러리 설정 액티비티로 이동
                Intent act = new Intent(GalleryMain.this, GalleryManagement.class);
                startActivity(act);
            }
        });

        FolderAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {// 갤러리 폴더 추가버튼

                onPause();
                final Dialog dialog = new Dialog(GalleryMain.this);
                dialog.setContentView(R.layout.galleryfolderadddialog);
                dialog.setTitle("폴더 추가");
                dialog.show();

                final EditText et = (EditText) dialog.findViewById(R.id.textDialog);
                Button addButton = (Button) dialog.findViewById(R.id.declineButton);
                Button cancelButton = (Button) dialog.findViewById(R.id.declineButton1);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        user.clear();
                        InputFolderName = et.getText().toString();
                        user.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));
                        user.add(new BasicNameValuePair("memID", pref.getString("ID", ""))); //폴더 생성시 만드는 아이디 add
                        user.add(new BasicNameValuePair("folderName", InputFolderName));  //
                        try {
                            HttpClient http = new DefaultHttpClient();

                            HttpParams params = http.getParams();
                            HttpConnectionParams.setConnectionTimeout(params, 10000);
                            HttpConnectionParams.setSoTimeout(params, 10000);
                            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"galleryAdd.do");

                            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
                            httpPost.setEntity(entityRequest);

                            HttpResponse responsePost = http.execute(httpPost);
                            HttpEntity resEntity = responsePost.getEntity();
                            result = EntityUtils.toString(resEntity);
                            result = result.trim();
                            if ("success".equals(result)) {
                                Toast.makeText(getApplicationContext(),"폴더 생성",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(),"폴더 생성 실패",Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        InputFolderName="";
                        et.setText("");
                        dialog.dismiss();
                        onResume();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() { //폳더 생성 취소
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        ImageAddButton.setOnClickListener(new View.OnClickListener() { //이미지 추가버튼
            @Override
            public void onClick(View v) {
                Intent act = new Intent(GalleryMain.this, ImageAdder.class);
                startActivity(act);
            }
        });



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SelectedFolderName = item.get(position).getGalleryName();//선택 폴더 저장
                for (int i = 0; i < parsered.length; i++) { //선택한 폴더 이름으로 폴더번호 저장
                    if (parsered[i][1].equals(SelectedFolderName)) {
                        FolderNo = parsered[i][0]+","+parsered[i][2];
                    }
                }
                Intent i = new Intent(getApplicationContext(),SelectedFolderGridView.class);
                i.putExtra("folderno",FolderNo);//인텐트로 선책한 폴더번호 전달
                startActivity(i);
            }
        });
    }

    //�ҷ��� �κ� ó��(���ϸ�ϵ��� ����Ʈ�� �����ֱ�
    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] fileInfo = {"galleryNo", "galleryName", "galleryMem"}; //폴더리스트를 받아올때 폴더 번호,이름,만든사람 같이 받아옴
            parsered = new String[jarr.length()][fileInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < fileInfo.length; j++)
                        parsered[i][j] = jsonob.getString(fileInfo[j]);
                }
            }

            for (int i = 0; i < parsered.length; i++) {
                if(parsered[i][2].equals(pref.getString("ID",""))) {
                    item.add(new GalleryFolderInfo(parsered[i][0], parsered[i][1], 1));

                }
                else{
                    item.add(new GalleryFolderInfo(parsered[i][0], parsered[i][1],0));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}