package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.GalleryFolder;

/**
 * Created by MIN on 2015-08-07.
 */
//갤러리 폴더 리스트 adapter
public class MyAdapterGalleryFolder extends BaseAdapter {
    Context context;
    int layoutid;
    Vector<GalleryFolder> FolderData;
    LayoutInflater inflater;

    public MyAdapterGalleryFolder(Context context, int layoutid, Vector<GalleryFolder> FolderData){
        this.context=context;
        this.layoutid=layoutid;
        this.FolderData=FolderData;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount(){
        return FolderData.size();
    }

    @Override
    public String getItem(int position) {
        return FolderData.get(position).getFolderName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView==null)
            convertView= inflater.inflate(layoutid, parent, false);

        TextView tvFolderName=(TextView)convertView.findViewById(R.id.folderlistName);
        tvFolderName.setText(FolderData.get(position).getFolderName());                //폴더이름
        tvFolderName.setSingleLine();
        TextView tvMakerName=(TextView)convertView.findViewById(R.id.folderlistId);
        tvMakerName.setText(FolderData.get(position).getFoldermaker());                 //만든id
        TextView tvImagecount=(TextView)convertView.findViewById(R.id.folderlistimagecount);
        tvImagecount.setText(FolderData.get(position).getImageCount());                 //폴더내 이미지 갯수

        return convertView;
    }
}

