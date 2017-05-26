package capston.finalproject.uigallary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.utils.ServerUrl;

//사진추가 액티비티
public class ImageAdder extends Activity {
    public ArrayList<String> item = null;
    String[][] parsered;
    SharedPreferences pref;
    String result, path, folderinfo, selfolder1;
    int selima, serverResponseCode = 0;
    ArrayAdapter Adapter1;
    Spinner FolderView;
    Button CanceladdImage, SelectImageButton, SendImage;
    TextView imagename;

    boolean state = false;
    final ArrayList<NameValuePair> user = new ArrayList<NameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageadder);
        FolderView = (Spinner) findViewById(R.id.folderSpiner);
        CanceladdImage = (Button) findViewById(R.id.imageaddcalcel);
        SelectImageButton = (Button) findViewById(R.id.selectImageButton);
        SendImage = (Button) findViewById(R.id.addImageButton);
        imagename = (TextView) findViewById(R.id.fileName);

        pref = getSharedPreferences("Test", 0);
        user.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));


        FolderView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //방의 갤러리에 생성된 폴더 선택
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selfolder1 = FolderView.getSelectedItem().toString();
                for (int i = 0; i < parsered.length; i++) {
                    if (selfolder1.equals(parsered[i][1])) {
                        folderinfo = parsered[i][0];
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //이미지 선택을 위한 액티비티 호출
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, selima);
            }
        });


        CanceladdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });//사진추가 취소


        SendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//업로드

                selfolder1 = FolderView.getSelectedItem().toString();
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                               uploadFile(path); //업로딩 스타트
                               // int response = uploadFile(path); //업로딩 스타트
                                try {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            CheckResultUpLoad();
                                        }
                                    });
                                } catch (Throwable t) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();
                finish();
            }
        });
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //선택한이미지 결과
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == selima) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData(); //이미지 id값
                String[] proj = {MediaStore.Images.Media.DATA};    //커서를 이용해 패스 값 얻어오기
                Cursor cursor = managedQuery(selectedImage, proj, null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();
                path = cursor.getString(index);//이미지 절대경로 저장
                cursor.close();
                imagename.setText(path.substring(path.lastIndexOf("/")+1));//이미지 이름
            }
        }
    }

    public void jsonParser(String fromServer) {
        item = new ArrayList<String>();
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] fileInfo = {"galleryNo", "galleryName", "galleryMem"};
            parsered = new String[jarr.length()][fileInfo.length];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < fileInfo.length; j++)
                        parsered[i][j] = jsonob.getString(fileInfo[j]);
                }
            }
            for (int i = 0; i < parsered.length; i++) {
                item.add(parsered[i][1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, item);
        Adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FolderView.setAdapter(Adapter1);
    }


    public void CheckResultUpLoad(){//업로드 결과 출력
        if(state) {
            Toast.makeText(getApplicationContext(), "file upload success", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"file upload fail",Toast.LENGTH_LONG).show();
        }
    }
    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\"" + key + "\"r\n\r\n"
                + value;
    }

    public static String setFile(String key, String fileName) {
        return "Content-Disposition: form-data; name=\"" + key
                + "\";filename=\"" + fileName + "\"\r\n";
    }


    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;
        HttpURLConnection conn;
        String boundary = "SpecificString";
        int bufferSize;
        byte[] buffer;
        int maxBufferSize;
        String delimiter = "\r\n--" + boundary + "\r\n";

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            return 0;
        }
        try {

            StringBuffer postDataBuilder = new StringBuffer();
            // 추가하고 싶은 Key & Value 추가
            // key & value를 추가한 후 꼭 경계선을 삽입해줘야 데이터를 구분할 수 있다.
            postDataBuilder.append(delimiter);
            postDataBuilder.append(setValue("galleryNo", folderinfo));
            postDataBuilder.append(delimiter);
            postDataBuilder.append(setValue("picMem",pref.getString("ID","")));
            postDataBuilder.append(delimiter);
            // 파일 첨부
            postDataBuilder.append(setFile("uploaded_file", fileName));
            postDataBuilder.append("\r\n");
            // 커넥션 생성 및 설정
            URL url = new URL(new ServerUrl().getServerUrl()+"pictureAdd.do");
            conn = (HttpURLConnection) url.openConnection();
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
            maxBufferSize = 1024;
            bufferSize = Math.min(in.available(), maxBufferSize);
            buffer = new byte[bufferSize];
            int byteRead = in.read(buffer, 0, bufferSize);
            // 전송
            while (byteRead > 0) {
                out.write(buffer);
                bufferSize = Math.min(in.available(), maxBufferSize);
                byteRead = in.read(buffer, 0, bufferSize);
            }
            state=true;
            out.writeBytes(delimiter); // 반드시 작성해야 한다.
            out.flush();
            out.close();
            in.close();
            conn.getInputStream();
            conn.disconnect();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            Toast.makeText(getApplicationContext(), "MalformedURLException", Toast.LENGTH_SHORT).show();
        } catch (ProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception : " + e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return serverResponseCode;
    }
}
