package capston.finalproject.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.service.ConnectServer;
import capston.finalproject.uiothers.FRoomMain;
import capston.finalproject.utils.Room;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterMyJoin extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<Room> roomData;
    LayoutInflater inflater;
    SharedPreferences pref;
    Button yes,no;
    int SUCCESS=1;
    int FAIL=2;
    public MyAdapterMyJoin(Context context, int layoutid, Vector<Room> roomData){
        this.context=context;
        this.layoutid=layoutid;
        this.roomData=roomData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
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
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);
        pref =context.getSharedPreferences("Test",0);

        TextView tvRoomCategory=(TextView)convertView.findViewById(R.id.myjoinCategory);
        tvRoomCategory.setText(roomData.get(position).getRoomCategory());
        TextView tvRoomName=(TextView)convertView.findViewById(R.id.myjoinName);
        tvRoomName.setText(roomData.get(position).getRoomName());
        TextView tvRoomMember=(TextView)convertView.findViewById(R.id.myjoinMember);
        tvRoomMember.setText(roomData.get(position).getCurrentMemCnt()+"/"+roomData.get(position).getRoomMemCnt());
        TextView tvRoomLocate=(TextView)convertView.findViewById(R.id.myjoinLocate);
        tvRoomLocate.setText(roomData.get(position).getRoomLocate());
        TextView tvRoomKeyword=(TextView)convertView.findViewById(R.id.myjoinKeyword);
        tvRoomKeyword.setText(roomData.get(position).getRoomKeyword());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor pedit=pref.edit();
                pedit.putString("RoomName",roomData.get(position).getRoomName());
                pedit.apply();
                Intent act = new Intent(context, FRoomMain.class);
                context.startActivity(act);
            }
        });
        Button btnRoomOut=(Button)convertView.findViewById(R.id.roomOutBtn);
        btnRoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_myroomchkdialog2);
                dialog.setTitle("방 탈퇴");
                dialog.show();
                yes=(Button)dialog.findViewById(R.id.getoutroomY);
                no=(Button)dialog.findViewById(R.id.getoutroomN);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                        info.add(new BasicNameValuePair("memID",pref.getString("ID", "")));
                        info.add(new BasicNameValuePair("roomName",roomData.get(position).getRoomName()));
                        ConnectServer conn = new ConnectServer();
                        int flag=conn.getSuccessFail(new ServerUrl().getServerUrl() +"withdrawFromRoom.do", info);
                        if (flag==SUCCESS) {
                            roomData.remove(roomData.get(position));
                            notifyDataSetChanged();
                        } else if(flag==FAIL){
                            Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show();
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
}
