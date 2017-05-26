package capston.finalproject.uigallary;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.adapter.MyAdapterGalleryFolder;
import capston.finalproject.utils.GalleryFolder;
import capston.finalproject.utils.ServerUrl;

public class GalleryFolderManagement extends Fragment {
    Vector<GalleryFolder> folder;
    ListView lv;
    MyAdapterGalleryFolder fAdapter;
    String[][] parsered;
    String change;
    ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
    public GalleryFolderManagement() {

    }
    @Override
    public void onResume() {
        super.onResume();
        folder.clear();
        setFolderList(info);
    }

    private void setFolderList(ArrayList<NameValuePair> info){
        try {
            //http클라이언트 생성
            HttpClient http = new DefaultHttpClient();
            //통신 설정
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"galleryListForSet.do");
            //보낼 정보 encoding
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
            //전송
            httpPost.setEntity(entityRequest);
            //결과 수진
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
            jsonParser(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fAdapter = new MyAdapterGalleryFolder(getActivity(), R.layout.gallerymanagementlistviewcustom, folder);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(fAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.galleryfoldermanagement, null);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        lv = (ListView) view.findViewById(R.id.folderList);
        folder = new Vector<GalleryFolder>();
        lv.setOnItemClickListener(folderlistener);

        //sharedpreferences 생성
        SharedPreferences pref = getActivity().getSharedPreferences("Test", 0);

        //list배열에 자신의 아이디와 현재 들어온 방의 데이터 add
        info.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

        return view;
    }

    public AdapterView.OnItemClickListener folderlistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            onPause();
            final String value = lv.getItemAtPosition(position).toString();   //선택한 리스트item 저장
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.foldermodificationanddelete);
            dialog.setTitle("폴더 수정 삭제");
            dialog.show();

            TextView tv = (TextView) dialog.findViewById(R.id.folderDialogTv);
            final EditText et = (EditText) dialog.findViewById(R.id.folderDialogEt);
            Button modificationButton = (Button) dialog.findViewById(R.id.modificationButton);
            Button folderdeleteButton = (Button) dialog.findViewById(R.id.folderdeleteButton);
            Button cancelButton = (Button) dialog.findViewById(R.id.modificationCancelButton);

            tv.setText("기존 폴더 이름 : " + value);
            et.setText(value);

            modificationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    change = et.getText().toString();
                    String selNO = null;
                    for (int i = 0; i < parsered.length; i++) {
                        if (value.equals(parsered[i][0])) {
                            selNO = parsered[i][3];
                        }
                    }
                    System.out.println(selNO);
                    ArrayList<NameValuePair> fileinfo = new ArrayList<NameValuePair>();
                    fileinfo.add(new BasicNameValuePair("galleryNo", selNO));
                    fileinfo.add(new BasicNameValuePair("galleryName", change));
                    try {
                        HttpClient http = new DefaultHttpClient();

                        HttpParams params = http.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 5000);
                        HttpConnectionParams.setSoTimeout(params, 5000);
                        HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"galleryEdit.do");

                        UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(fileinfo, "EUC-KR");
                        httpPost.setEntity(entityRequest);

                        HttpResponse responsePost = http.execute(httpPost);
                        HttpEntity resEntity = responsePost.getEntity();
                        String result = EntityUtils.toString(resEntity);
                        result = result.trim();
                        if ("success".equals(result)) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "수정 완료", Toast.LENGTH_SHORT).show();
                            onDestroy();

                        } else {
                            Toast.makeText(getActivity(), "수정 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    onResume();
                }
            });


            folderdeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selNO = null;
                    for (int i = 0; i < parsered.length; i++) {    //선택한 폴더 폴더번호 검색
                        if (parsered[i][0] == value) {
                            selNO = parsered[i][3];
                        }
                    }
                    ArrayList<NameValuePair> fileinfo = new ArrayList<NameValuePair>();
                    fileinfo.add(new BasicNameValuePair("galleryNo", selNO));

                    try {
                        HttpClient http = new DefaultHttpClient();

                        HttpParams params = http.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 5000);
                        HttpConnectionParams.setSoTimeout(params, 5000);
                        HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"galleryDelete.do");

                        UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(fileinfo, "EUC-KR");
                        httpPost.setEntity(entityRequest);

                        HttpResponse responsePost = http.execute(httpPost);
                        HttpEntity resEntity = responsePost.getEntity();
                        String result = EntityUtils.toString(resEntity);
                        result = result.trim();
                        if ("success".equals(result)) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "삭제", Toast.LENGTH_SHORT).show();
                        } else {
                             Toast.makeText(getActivity(), "삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    onResume();
                }
            });


            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    };

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List"); // 받아올 데이터 오브젝트 키값
            String[] roomInfo = {"galleryName", "galleryMem", "galleryPictures", "galleryNo"}; //오브젝트 내의 데이터 키값
            parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++)
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                }
            }
            //JSON �Ľ� �� �� ������ Vector�� ���� -> MyAdapterGalleryFolder.getView�� ����Ʈ �ѷ���
            for (int i = 0; i < parsered.length; i++) {
                folder.add(new GalleryFolder(parsered[i][0], parsered[i][1], parsered[i][2], parsered[i][3]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
