package capston.finalproject.uigallary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import capston.finalproject.R;
import capston.finalproject.utils.ServerUrl;

public class GalleryRequestedDetail extends Activity {

    ImageView Image;
    TextView fileName;
    Button RequestCancle, Cancle;
    String selPicNo,selPicName,getinfo;
    Bitmap getBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitygalleryrequesteddetail);
        fileName = (TextView)findViewById(R.id.requestIdDetail);
        RequestCancle = (Button)findViewById(R.id.requestAcceptCancle);
        Cancle = (Button)findViewById(R.id.canclecancle);
        Image = (ImageView)findViewById(R.id.myRequestedimage);

        Intent i = getIntent();
        getinfo = i.getStringExtra("imageinfo");//이미지 정보 받아오기
        String[] str = getinfo.split(",");
        selPicName=str[0];
        selPicNo=str[1];
        ImageSet(new ServerUrl().getServerPath() + selPicName);

        fileName.setText(selPicName);

        RequestCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> ImageInfo = new ArrayList<NameValuePair>();
                ImageInfo.add(new BasicNameValuePair("pictureNo", selPicNo));  //사진 삭제에 사진번호, 사진이름 list에 저장

                try {
                    HttpClient http = new DefaultHttpClient();

                    HttpParams params = http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "pictureRequestDeleteRefusal.do");

                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(ImageInfo, "EUC-KR");
                    httpPost.setEntity(entityRequest);

                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntity = responsePost.getEntity();
                    String result = EntityUtils.toString(resEntity);
                    result = result.trim();
                    if ("success".equals(result)) {
                        Toast.makeText(getApplicationContext(), "삭제요청취소", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "취소실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
            getBit = BitmapFactory.decodeStream(is);
            Image.setImageBitmap(getBit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
