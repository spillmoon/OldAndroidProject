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
import capston.finalproject.utils.Answer;
import capston.finalproject.utils.ServerUrl;

public class MyAdapterRereplyList extends BaseAdapter {
    int LayoutId;
    SharedPreferences pref;
    Context context;
    Vector<Answer> ReContent;
    LayoutInflater inflater;
    TextView ansMem,ansContent,date;
    Button delete;
    int SUCCESS=1;
    int FAIL=2;

    public MyAdapterRereplyList(Context context, int LayoutId, Vector<Answer> ReContent){
        this.context=context;
        this.LayoutId=LayoutId;
        this.ReContent=ReContent;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ReContent.size();
    }

    @Override
    public Object getItem(int position) {
        return ReContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(ReContent.get(position).getAnsNo());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView= inflater.inflate(LayoutId,parent,false);

        ansMem=(TextView)convertView.findViewById(R.id.reansMem);
        ansContent=(TextView)convertView.findViewById(R.id.reansContent);
        date=(TextView)convertView.findViewById(R.id.reansDate);
        delete=(Button)convertView.findViewById(R.id.rereplydelete);
        pref=context.getSharedPreferences("Test", 0);

        ansMem.setText(ReContent.get(position).getAnsMem());
        ansContent.setText(ReContent.get(position).getAnsContent());
        date.setText(ReContent.get(position).getAnsDate());

        if(checkAuthor()){
            delete.setEnabled(true);
        }
        else{
            delete.setEnabled(false);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<NameValuePair> info = new ArrayList<NameValuePair>();
                ConnectServer conn = new ConnectServer();
                info.add(new BasicNameValuePair("ansNo", ReContent.get(position).getAnsNo()));
                int flag = conn.getSuccessFail(new ServerUrl().getServerUrl() + "answerDelete.do", info);

                if (flag == SUCCESS) {
                    ReContent.remove(ReContent.get(position));
                    notifyDataSetChanged();
                } else if (flag == FAIL) {
                    Toast.makeText(context, "delete fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    public boolean checkAuthor(){
        if(pref.getString("ID","").equals(ansMem.getText().toString()))
            return true;
        return false;
    }
}
