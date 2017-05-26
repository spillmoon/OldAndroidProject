package capston.finalproject.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import capston.finalproject.uiothers.FRoomMain;
import capston.finalproject.utils.Room;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterMyCreate extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<Room> roomData;
    LayoutInflater inflater;
    TextView tvRoomName;
    String[][] parsered;
    SharedPreferences pref;

    public MyAdapterMyCreate(Context context, int layoutid, Vector<Room> roomData) {
        this.context = context;
        this.layoutid = layoutid;
        this.roomData = roomData;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return roomData.size();
    }

    @Override
    public String getItem(int position) {
        return roomData.get(position).getRoomName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(layoutid, parent, false);

        pref = context.getSharedPreferences("Test", 0);
        TextView tvRoomCategory = (TextView) convertView.findViewById(R.id.mycreateCategory);
        tvRoomName = (TextView) convertView.findViewById(R.id.mycreateName);
        TextView tvRoomMember = (TextView) convertView.findViewById(R.id.mycreateMember);
        TextView tvRoomLocate = (TextView) convertView.findViewById(R.id.mycreateLocate);
        TextView tvRoomKeyword = (TextView) convertView.findViewById(R.id.mycreateKeyword);

        Button btnPermition = (Button) convertView.findViewById(R.id.permitionGiveBtn);
        Button btnRoomDelete = (Button) convertView.findViewById(R.id.roomDeleteBtn);

        tvRoomCategory.setText(roomData.get(position).getRoomCategory());
        tvRoomName.setText(roomData.get(position).getRoomName());
        tvRoomMember.setText(roomData.get(position).getCurrentMemCnt() + "/" + roomData.get(position).getRoomMemCnt());
        tvRoomLocate.setText(roomData.get(position).getRoomLocate());
        tvRoomKeyword.setText(roomData.get(position).getRoomKeyword());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor pedit = pref.edit();
                pedit.putString("RoomName", roomData.get(position).getRoomName());
                pedit.apply();
                Intent act = new Intent(context, FRoomMain.class);
                context.startActivity(act);
            }
        });

        btnPermition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                info.add(new BasicNameValuePair("roomName", roomData.get(position).getRoomName()));
                String result = "";
                String line;
                try {
                    HttpClient http = new DefaultHttpClient();
                    HttpParams params = http.getParams();
                    HttpConnectionParams.setConnectionTimeout(params, 5000);
                    HttpConnectionParams.setSoTimeout(params, 5000);
                    HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "leaderTransferList.do");
                    UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
                    httpPost.setEntity(entityRequest);
                    HttpResponse responsePost = http.execute(httpPost);
                    HttpEntity resEntity = responsePost.getEntity();
                    InputStream stream = resEntity.getContent();
                    //JSON string ������
                    BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream, "EUC-KR"));
                    while ((line = bufreader.readLine()) != null) {
                        result += line;
                    }
                    jsonParser(result, position);//json parsing
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnRoomDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_myroomchkdialog1);
                dialog.setTitle("방 파기");
                dialog.show();

                Button yes = (Button) dialog.findViewById(R.id.myroomdeleteY);
                Button no = (Button) dialog.findViewById(R.id.myroomdeleteN);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                        info.add(new BasicNameValuePair("roomName", roomData.get(position).getRoomName()));
                        try {
                            HttpClient http = new DefaultHttpClient();
                            HttpParams params = http.getParams();
                            HttpConnectionParams.setConnectionTimeout(params, 5000);
                            HttpConnectionParams.setSoTimeout(params, 5000);
                            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "roomDestroy.do");
                            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(info, "EUC-KR");
                            httpPost.setEntity(entityRequest);
                            HttpResponse responsePost = http.execute(httpPost);
                            HttpEntity resEntity = responsePost.getEntity();
                            String result = EntityUtils.toString(resEntity);
                            result = result.trim();
                            if ("success".equals(result)) {
                                roomData.remove(roomData.get(position));
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "파기 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
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

    public void jsonParser(String fromServer, int position) {
        String[] memberName = {};
        String flag = "2";
        try {
            JSONObject jsonob = new JSONObject(fromServer);
            JSONArray jarr = jsonob.getJSONArray("List");
            String[] listinfo = {"memID", "memName", "leaderChk"};
            parsered = new String[jarr.length()][listinfo.length];
            memberName = new String[jarr.length()];
            for (int i = 0; i < jarr.length(); i++) {
                jsonob = jarr.getJSONObject(i);
                if (jsonob != null) {
                    for (int j = 0; j < listinfo.length; j++)
                        parsered[i][j] = jsonob.getString(listinfo[j]);
                }
                memberName[i] = parsered[i][0] + parsered[i][1];
                flag = parsered[i][2];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showMemberList(memberName, flag, position);
    }

    private void showMemberList(String[] memberlist, String flag, final int position) {
        final ArrayList<NameValuePair> name = new ArrayList<NameValuePair>();
        if (flag.equals("0")) {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setTitle("양도 받을 사람");
            adb.setSingleChoiceItems(memberlist, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    name.add(new BasicNameValuePair("memID", parsered[which][0]));
                    name.add(new BasicNameValuePair("roomName", roomData.get(position).getRoomName()));
                }
            });
            adb.setPositiveButton("양도", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        HttpClient http = new DefaultHttpClient();
                        HttpParams params = http.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 5000);
                        HttpConnectionParams.setSoTimeout(params, 5000);
                        HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "leaderTransferApply.do");
                        UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(name, "EUC-KR");
                        httpPost.setEntity(entityRequest);
                        HttpResponse responsePost = http.execute(httpPost);
                        HttpEntity resEntity = responsePost.getEntity();
                        String result = EntityUtils.toString(resEntity);
                        result = result.trim();
                        if ("success".equals(result)) {
                            Toast.makeText(context, "양도 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "양도 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.show();
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setTitle("양도 받을 사람");
            adb.setMessage("양도중이거나 멤버가 없습니다");
            adb.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.show();
        }
    }
}
