package capston.finalproject.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.BoardUtil;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterFolderList extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<BoardUtil> folderList;
    LayoutInflater inflater;

    public MyAdapterFolderList(Context context, int layoutid, Vector<BoardUtil> folderList){
        this.context=context;
        this.layoutid=layoutid;
        this.folderList=folderList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return folderList.size();
    }

    @Override
    public String getItem(int position) {
        return folderList.get(position).getFolderno();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView folderName=(TextView)convertView.findViewById(R.id.folderNamelist);
        folderName.setText("● "+folderList.get(position).getFoldername());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_addfolder);
                dialog.setTitle("폴더 수정");
                dialog.show();

                final EditText newname = (EditText) dialog.findViewById(R.id.newfolder);
                Button yes = (Button) dialog.findViewById(R.id.yesadd);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                        info.add(new BasicNameValuePair("folderNo",folderList.get(position).getFolderno()));
                        info.add(new BasicNameValuePair("modifiedFolderName", newname.getText().toString()));
                        try {// http 통신준비
                            HttpClient http = new DefaultHttpClient();
                            HttpParams params = http.getParams();
                            HttpConnectionParams.setConnectionTimeout(params, 5000);
                            HttpConnectionParams.setSoTimeout(params, 5000);
                            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl()+"editFolder.do");
                            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
                            httpPost.setEntity(entityRequest);
                            //서버로 전송 후 결과값 받아옴
                            HttpResponse responsePost = http.execute(httpPost);
                            HttpEntity resEntity = responsePost.getEntity();
                            String result = EntityUtils.toString(resEntity);
                            result = result.trim();
                            //결과값(플래그)에 따라 분기(성공, wrong id/pw)
                            if ("success".equals(result)) {
                                Toast.makeText(context, "folder edited", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
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

        return convertView;
    }
}
