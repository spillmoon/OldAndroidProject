package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.DaySchedule;

/**
 * Created by MIN on 2015-08-20.
 */
public class MyAdapterScheduleList extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<DaySchedule> SelDayData;
    LayoutInflater inflater;

    public MyAdapterScheduleList(Context context, int layoutid, Vector<DaySchedule> SelDayData){
        this.context=context;
        this.layoutid=layoutid;
        this.SelDayData=SelDayData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return SelDayData.size();
    }


    @Override
    public String getItem(int position) {
        return SelDayData.get(position).getScheduleNo();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView tvScheduleName=(TextView)convertView.findViewById(R.id.ListScheduleName);
        tvScheduleName.setText(SelDayData.get(position).getScheduleName());
        TextView tvSchedulePlace=(TextView)convertView.findViewById(R.id.ListSchedulePlace);
        tvSchedulePlace.setText(SelDayData.get(position).getSchedulePlace());
        TextView tvScheduleDate=(TextView)convertView.findViewById(R.id.ListScheduleDate);
        tvScheduleDate.setText(SelDayData.get(position).getScheduleStartDate());
        TextView tvScheduleExplain=(TextView)convertView.findViewById(R.id.ListExplain);
        tvScheduleExplain.setText(SelDayData.get(position).getScheduleExplain());
        tvScheduleExplain.setSingleLine();

        return convertView;
    }
}

