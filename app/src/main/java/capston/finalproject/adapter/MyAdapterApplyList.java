package capston.finalproject.adapter;

import android.content.Context;
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
import capston.finalproject.utils.Member;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterApplyList extends BaseAdapter{
    Context context;
    int layoutid;
    Vector<Member> memberData;
    LayoutInflater inflater;
    SharedPreferences pref;
    int SUCCESS=1;
    int FAIL=2;

    public MyAdapterApplyList(Context context, int layoutid, Vector<Member> memData){
        this.context=context;
        this.layoutid=layoutid;
        this.memberData=memData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return memberData.size();
    }

    @Override
    public String getItem(int position) {
        return memberData.get(position).getMemID();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        pref=context.getSharedPreferences("Test", 0);
        TextView memID=(TextView)convertView.findViewById(R.id.memid);
        memID.setText(memberData.get(position).getMemID());
        TextView interest=(TextView)convertView.findViewById(R.id.memcategory);
        interest.setText(memberData.get(position).getMemCategory());
        TextView phone=(TextView)convertView.findViewById(R.id.memphone);
        phone.setText(memberData.get(position).getMemPhone());
        TextView email=(TextView)convertView.findViewById(R.id.mememail);
        email.setText(memberData.get(position).getMemEmail());
        Button accept=(Button)convertView.findViewById(R.id.applyaccept);
        Button refusal=(Button)convertView.findViewById(R.id.applyrefusal);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info=new ArrayList<NameValuePair>();
                ConnectServer conn=new ConnectServer();
                info.add(new BasicNameValuePair("memID",memberData.get(position).getMemID()));
                info.add(new BasicNameValuePair("roomName", pref.getString("RoomName", "")));

                int flag=conn.getSuccessFail(new ServerUrl().getServerUrl()+"applyingToMyRoomAccept.do",info);

                if (flag==SUCCESS) {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                } else if(flag==FAIL){
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        refusal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info=new ArrayList<NameValuePair>();
                ConnectServer conn=new ConnectServer();
                info.add(new BasicNameValuePair("memID",memberData.get(position).getMemID()));
                info.add(new BasicNameValuePair("roomName",pref.getString("RoomName","")));

                int flag=conn.getSuccessFail(new ServerUrl().getServerUrl()+"applyingToMyRoomRefusal.do",info);

                if (flag == SUCCESS) {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                } else if (flag == FAIL) {
                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }
}
