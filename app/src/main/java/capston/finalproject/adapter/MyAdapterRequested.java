package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.Requested;

/**
 * Created by MIN on 2015-08-11.
 */
public class MyAdapterRequested extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<Requested> RequestedData;
    LayoutInflater inflater;

    public MyAdapterRequested(Context context, int layoutid, Vector<Requested> RequestData){
        this.context=context;
        this.layoutid=layoutid;
        this.RequestedData=RequestData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return RequestedData.size();
    }


    @Override
    public String getItem(int position) {
        return RequestedData.get(position).getPicNO();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView tvFolderName=(TextView)convertView.findViewById(R.id.refolderlistName);
        tvFolderName.setText(RequestedData.get(position).getFolderName());
        tvFolderName.setSingleLine();
        TextView tvImageName=(TextView)convertView.findViewById(R.id.reimageName);
        tvImageName.setText(RequestedData.get(position).getImageName());
        tvImageName.setSingleLine();


        return convertView;
    }

}

