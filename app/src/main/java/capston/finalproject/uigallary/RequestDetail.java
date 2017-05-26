package capston.finalproject.uigallary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

public class RequestDetail extends Activity {
    String requestinfo, requestImage, requestFoldername, requestID, requestImageNO;
    ImageView imageView;
    Bitmap getBit;
    TextView getTv;
    Button accept,refusal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestdetail);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        imageView = (ImageView)findViewById(R.id.requestimage);
        getTv = (TextView)findViewById(R.id.requestId);
        accept = (Button)findViewById(R.id.requestAccept);
        refusal = (Button)findViewById(R.id.requestRefusal);

        Intent i = getIntent();
        requestinfo = i.getStringExtra("requestinfo");//이미지 정보 받아오기
        String[] str = requestinfo.split(",");
        requestImage = str[0];         //request 이미지 이름
        requestFoldername = str[1];   //request 폴더 이름
        requestID = str[2];            //request id
        requestImageNO = str[3];      //request
        System.out.println(requestImage+requestFoldername+requestID+requestImageNO);
        getTv.setText(requestID);
        ImageSet(new ServerUrl().getServerPath()+ requestImage);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> ImageInfo = new ArrayList<NameValuePair>();
                ImageInfo.add(new BasicNameValuePair("pictureNo", requestImageNO));  //사진 삭제에 사진번호, 사진이름 list에 저장
                ImageInfo.add(new BasicNameValuePair("picName", requestImage));
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
                        Toast.makeText(getApplicationContext(), "삭제 수락", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "거절", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        refusal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> ImageInfo = new ArrayList<NameValuePair>();
                ImageInfo.add(new BasicNameValuePair("pictureNo", requestImageNO));  //사진 삭제에 사진번호, 사진이름 list에 저장
                try {
                    HttpClient http = new DefaultHttpClient();

                    HttpParams params = http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"pictureRequestDeleteRefusal.do");

                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(ImageInfo, "EUC-KR");
                    httpPost.setEntity(entityRequest);

                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntity = responsePost.getEntity();
                    String result = EntityUtils.toString(resEntity);
                    result = result.trim();
                    if ("success".equals(result)) {
                        Toast.makeText(getApplicationContext(), "삭제", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

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
            imageView.setImageBitmap(getBit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
