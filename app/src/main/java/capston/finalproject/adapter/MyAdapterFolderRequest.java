package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.Request;

public class MyAdapterFolderRequest extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<Request> RequestData;
    LayoutInflater inflater;

    public MyAdapterFolderRequest(Context context, int layoutid, Vector<Request> RequestData){
        this.context=context;
        this.layoutid=layoutid;
        this.RequestData=RequestData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return RequestData.size();
    }


    @Override
    public String getItem(int position) {
        return RequestData.get(position).getPicNO();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView tvImageName=(TextView)convertView.findViewById(R.id.requestlistName);
        tvImageName.setText(RequestData.get(position).getImageName());
        tvImageName.setSingleLine();
        TextView tvfolderName=(TextView)convertView.findViewById(R.id.requestfolderlistId);
        tvfolderName.setText(RequestData.get(position).getFolderName());
        tvfolderName.setSingleLine();
        TextView tvRequestId=(TextView)convertView.findViewById(R.id.deleterequestid);
        tvRequestId.setText(RequestData.get(position).getRequestId());


        return convertView;
    }
}

