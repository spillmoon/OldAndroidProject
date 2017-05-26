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
import capston.finalproject.uiboard.BoardContentShow;
import capston.finalproject.utils.BoardUtil;

public class MyAdapterBoardList extends BaseAdapter {
    String boardcontentNo;
    Context context;
    int layoutid;
    Vector<BoardUtil> content;
    LayoutInflater inflater;

    public MyAdapterBoardList(Context context, int layoutid, Vector<BoardUtil> content){
        this.context=context;
        this.layoutid=layoutid;
        this.content=content;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return content.size();
    }

    @Override
    public String getItem(int position) {
        return content.get(position).getBoardno();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView no=(TextView)convertView.findViewById(R.id.boardno);
        no.setText(content.get(position).getBoardno());
        TextView folder=(TextView)convertView.findViewById(R.id.folder);
        folder.setText(content.get(position).getFolder());
        TextView title=(TextView)convertView.findViewById(R.id.title);
        title.setText(content.get(position).getTitle());
        TextView author=(TextView)convertView.findViewById(R.id.author);
        author.setText(content.get(position).getMem());
        TextView date=(TextView)convertView.findViewById(R.id.date);
        date.setText(content.get(position).getDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardcontentNo=getItem(position);
                Intent act=new Intent(context, BoardContentShow.class);
                act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                act.putExtra("boardNo", boardcontentNo);
                context.startActivity(act);
            }
        });

        return convertView;
    }
}
