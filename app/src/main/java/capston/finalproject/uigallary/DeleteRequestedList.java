package capston.finalproject.uigallary;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
import capston.finalproject.adapter.MyAdapterRequested;
import capston.finalproject.utils.Requested;
import capston.finalproject.utils.ServerUrl;

public class DeleteRequestedList extends Fragment {
    SharedPreferences pref;
    String[][] parsered;
    MyAdapterRequested IRAdapter;
    Vector<Requested> RequestedList;
    ListView lv;
    ArrayList<NameValuePair> requestinfo = new ArrayList<NameValuePair>();

    public DeleteRequestedList() {

    }

    @Override
    public void onResume() {
        super.onResume();
        RequestedList.clear();
        setDeleteRequestedList(requestinfo);
    }

    private void setDeleteRequestedList(ArrayList<NameValuePair> requestinfo) {
        try {
            //http클라이언트 생성
            HttpClient http = new DefaultHttpClient();
            //통신 설정
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "pictureDeleteRequestingList.do");
            //보낼 정보 encoding
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(requestinfo, "EUC-KR");
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
        IRAdapter = new MyAdapterRequested(getActivity(), R.layout.deleterequestedlistcustom, RequestedList);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(IRAdapter);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deleterequestedlist, container, false);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        lv = (ListView) view.findViewById(R.id.RequestedList);
        RequestedList = new Vector<Requested>();
        pref = getActivity().getSharedPreferences("Test", 0);

        requestinfo.add(new BasicNameValuePair("memID", pref.getString("ID", "")));
        requestinfo.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selPicName = RequestedList.get(position).getImageName();
                String selpicNo = RequestedList.get(position).getPicNO();
                String imageinfo=selPicName+","+selpicNo;
                Intent i = new Intent(getActivity(), GalleryRequestedDetail.class);
                i.putExtra("imageinfo", imageinfo);
                startActivity(i);
            }
        });
        return view;
    }

    public void jsonParser(String fromServer) {
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List"); // 받아올 데이터 오브젝트 키값
            String[] roomInfo = {"pictureNo", "galleryName", "picName"}; //오브젝트 내의 데이터 키값
            parsered = new String[jarr.length()][roomInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < roomInfo.length; j++) {
                        parsered[i][j] = jsonob.getString(roomInfo[j]);
                    }
                }
            }
            //JSON �Ľ� �� �� ������ Vector�� ���� -> MyAdapterGalleryFolder.getView�� ����Ʈ �ѷ���
            for (int i = 0; i < parsered.length; i++) {
                RequestedList.add(new Requested(parsered[i][0], parsered[i][1], parsered[i][2]));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
