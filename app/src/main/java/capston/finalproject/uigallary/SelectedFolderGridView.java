package capston.finalproject.uigallary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterSelectFolderGrid;
import capston.finalproject.utils.GalleryFolderInfo;
import capston.finalproject.utils.ServerUrl;

//선택한 폴더 내부 이미지 뷰
public class SelectedFolderGridView extends Activity {
    public Vector<GalleryFolderInfo> item;
    String fileinfo1="";   //선택 이미지 데이터
    String[][] parsered;    //파싱받아온 데이터
    GridView gridView;
    String GetFolderNo,FolderMaster,GetFolderInfo;    //선택한 폴더 번호
    SharedPreferences pref;
    MyAdapterSelectFolderGrid FAdapter;
    final ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();
    @Override
    public void onResume(){
        super.onResume();
        item.clear();
        setSelectedFolderGridView(user);
    }
    private void setSelectedFolderGridView(ArrayList<NameValuePair> user){
        try {
            HttpClient http = new DefaultHttpClient();

            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"pictureFromGallery.do");

            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(user, "EUC-KR");
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
        FAdapter = new MyAdapterSelectFolderGrid(this,R.layout.selectgridviewitem, item);
        gridView.setAdapter(FAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectedfoldergridview);

        item = new Vector<GalleryFolderInfo>();
        Intent i = getIntent();
        GetFolderInfo = i.getStringExtra("folderno");
        String[] str = GetFolderInfo.split(",");  //인텐트로 값 받기
        GetFolderNo = str[0];
        FolderMaster = str[1];
        pref = getSharedPreferences("Test", 0);
        gridView = (GridView) findViewById(R.id.selectedfoldergrid);


        user.add(new BasicNameValuePair("galleryNo", GetFolderNo));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {    //그리드뷰에서 아이템 선택시
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String SelectName = item.get(position).getGalleryName();  //선택 image
                for (int f = 0; f < parsered.length; f++) {   //선택한 이미지 data저장
                    if (SelectName.equals(parsered[f][1])) {        //intent로 3개중 2개만 값이 넘어가서 이렇게 보냄
                        fileinfo1 = parsered[f][0] + ",";
                        fileinfo1 += parsered[f][1] + ",";
                        fileinfo1 += parsered[f][2] + ",";
                        fileinfo1 += GetFolderInfo;
                        System.out.println(fileinfo1);
                    }
                }
                Intent i = new Intent(getApplicationContext(), ImageFullScreen.class);
                i.putExtra("imageinfo", fileinfo1);
                startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public void jsonParser(String fromServer) {

        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] fileInfo = {"pictureNo", "picName", "picMem"};   //사진 정보
            parsered = new String[jarr.length()][fileInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < fileInfo.length; j++)
                        parsered[i][j] = jsonob.getString(fileInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                item.add(new GalleryFolderInfo(parsered[i][0], parsered[i][1], 1));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
