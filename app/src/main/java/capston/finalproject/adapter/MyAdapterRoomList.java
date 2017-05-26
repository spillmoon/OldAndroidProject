package capston.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.uiroomlist.RoomApply;
import capston.finalproject.uiroomlist.RoomApplyCancel;
import capston.finalproject.utils.Room;

public class MyAdapterRoomList extends BaseAdapter {
    String inforoomname;
    Context context;
    int layoutid;
    Vector<Room> roomData;
    LayoutInflater inflater;
    public MyAdapterRoomList(Context context, int layoutid, Vector<Room> roomData){
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

        TextView tvRoomCategory=(TextView)convertView.findViewById(R.id.listitemCategory);
        tvRoomCategory.setText(roomData.get(position).getRoomCategory());
        TextView tvRoomName=(TextView)convertView.findViewById(R.id.listitemName);
        tvRoomName.setText(roomData.get(position).getRoomName());
        TextView tvRoomMember=(TextView)convertView.findViewById(R.id.listitemMember);
        tvRoomMember.setText(roomData.get(position).getCurrentMemCnt()+"/"+roomData.get(position).getRoomMemCnt());
        TextView tvRoomLocate=(TextView)convertView.findViewById(R.id.listitemLocate);
        tvRoomLocate.setText(roomData.get(position).getRoomLocate());
        TextView tvRoomKeyword=(TextView)convertView.findViewById(R.id.listitemKeyword);
        tvRoomKeyword.setText(roomData.get(position).getRoomKeyword());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomData.get(position).getReqStatus() ==1) {
                    inforoomname = getItem(position);
                    Intent act = new Intent(context, RoomApplyCancel.class);
                    act.putExtra("name", inforoomname);
                    context.startActivity(act);
                }
                else{
                    inforoomname = getItem(position);
                    Intent act = new Intent(context, RoomApply.class);
                    act.putExtra("name", inforoomname);
                    context.startActivity(act);
                }
            }
        });
        TextView reqStatus=(TextView)convertView.findViewById(R.id.reqStatus);
        if(roomData.get(position).getReqStatus() == 0)//already mamber
            reqStatus.setText("가입 됨");
        else if(roomData.get(position).getReqStatus() == 1)//applying
            reqStatus.setText("신청 중");
        else//abailbe apply
            reqStatus.setText("가입 가능");
        return convertView;
    }
}
