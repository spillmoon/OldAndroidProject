package capston.finalproject.uigallary;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.utils.ServerUrl;

//선택 이미지 풀스트린 액티비티
public class ImageFullScreen extends Activity {
    String Save_Path = "/DCIM/memoryShare";
    String ImageNo, ImageName, uploderID, imageinfo,FolderNo, FolderMaster;
    ImageView imageView;
    Bitmap getbmImg;
    TextView gettv;
    SharedPreferences pref;
    DownloadThread dThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagefullscreen);

        pref = getSharedPreferences("Test", 0);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            Save_Path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + Save_Path;
        }

        imageView = (ImageView) findViewById(R.id.fullimageview);
        gettv = (TextView) findViewById(R.id.getImageNmae);

        Intent i = getIntent();
        imageinfo = i.getStringExtra("imageinfo");//이미지 정보 받아오기
        System.out.println(imageinfo);
        String[] str = imageinfo.split(",");
        ImageNo=str[0];  //이미지번호
        ImageName=str[1];  //이미지 이름
        uploderID=str[2];   //uploader id
        FolderNo=str[3];
        FolderMaster=str[4];
        gettv.setText(ImageName);
        ImageSet(new ServerUrl().getServerUrl()+"data/"+ ImageName);  //이미지 이름으로 이미지 뷰어 set
        registerForContextMenu(imageView);    // 이미지 롱 클릭시 ContextMenu 설정
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.fullimageview) {
            if (pref.getString("ID", "").equals(FolderMaster)) {  //자신의 id와 uploader id를 비교하여 다른 메뉴 출력
                menu.add(0, 0, 0, "저장");   //각각 선택 플래그값 지정 0,1,2
                menu.add(0, 1, 0, "삭제");
            } else {
                menu.add(0, 0, 0, "저장");
                menu.add(0, 2, 0, "삭제 요청");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {  //선태한 플래그값 실행
        switch (item.getItemId()) {
            case 0:
                save(item.getItemId());
                break;
            case 1:
                delete(item.getItemId());
                break;
            case 2:
                deleteRequest(item.getItemId());
                break;
        }
        return true;
    }

    private void deleteRequest(int itemId) {   //삭제요청
        ArrayList<NameValuePair> ImageInfo = new ArrayList<NameValuePair>();
        ImageInfo.add(new BasicNameValuePair("pictureNo", ImageNo));   //요청에 이미지 번호, 요청멤버 id list에 저장
        ImageInfo.add(new BasicNameValuePair("requestMem", pref.getString("ID", "")));
        try {
            HttpClient http = new DefaultHttpClient();

            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"pictureRequestDelete.do");

            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(ImageInfo, "EUC-KR");
            httpPost.setEntity(entityRequest);

            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            String result = EntityUtils.toString(resEntity);
            result = result.trim();    //결과 수신
            if ("success".equals(result)) {
                Toast.makeText(getApplicationContext(), "삭제요청", Toast.LENGTH_SHORT).show();
            } else if ("already".equals(result)) {
                Toast.makeText(getApplicationContext(), "삭제요청중인 사진.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "삭제요청 실패", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void delete(int itemId) { //업로더 본인의 사진 삭제
        ArrayList<NameValuePair> ImageInfo = new ArrayList<NameValuePair>();
        ImageInfo.add(new BasicNameValuePair("pictureNo", ImageNo));  //사진 삭제에 사진번호, 사진이름 list에 저장
        ImageInfo.add(new BasicNameValuePair("picName", ImageName));
        if (uploderID.equals(pref.getString("ID", ""))) { // uploader와 현재 로그인 id가 같은지 두번 확인
            try {
                HttpClient http = new DefaultHttpClient();

                HttpParams params = http.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);
                HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"pictureDelete.do");

                UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(ImageInfo, "EUC-KR");
                httpPost.setEntity(entityRequest);

                HttpResponse responsePost = http.execute(httpPost);
                HttpEntity resEntity = responsePost.getEntity();
                String result = EntityUtils.toString(resEntity);
                result = result.trim();
                if ("success".equals(result)) {
                    Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            Toast.makeText(getApplicationContext(), "uploader가 아님", Toast.LENGTH_LONG).show();

    }
    public void save(int i) {  //사진저장
        File dir = new File(Save_Path);

        if (!dir.exists()) { //savepath 폴더가 있는지 확인
            dir.mkdir();    //폴더가 없으면 생성
        }

        File file = new File(Save_Path+"/"+ImageName);
        if (new File(Save_Path + "/" + ImageName).exists() == false) { //폴더에 파일이 있는지 확인
            //다운로드할 스레드 생성  //(서버URL , savepath)
            dThread = new DownloadThread(new ServerUrl().getServerPath() + ImageName, Save_Path + "/" + ImageName);
            dThread.start();   //스래드 시작
            try {
                dThread.join(); //스레드가 끝날때 까지 대기
                getApplication().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(getApplicationContext(), "downloaded", Toast.LENGTH_SHORT).show();  //성공
            } catch (InterruptedException ie) {
                Toast.makeText(getApplicationContext(), "download fail", Toast.LENGTH_SHORT).show();  //실패
                ie.printStackTrace();
            }
        } else {
            //사진이 이미 있기때문에 저장 안함
            Toast.makeText(getApplicationContext(), "이미 다운된 파일입니다.", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadThread extends Thread {  //이미지 파일을 저장 스레드
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
                //통신 설정
                HttpURLConnection conn = (HttpURLConnection) imgurl.openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                //다운로드
                FileOutputStream fos = new FileOutputStream(file);
                for (; ; ) {
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

    void ImageSet(String fileUrl) { //imageview에 이미지 set
        java.net.URL myFileUrl = null;
        try {
            myFileUrl = new URL(fileUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            //설정
            conn.setDoInput(true);
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            //imageview set
            getbmImg = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(getbmImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
