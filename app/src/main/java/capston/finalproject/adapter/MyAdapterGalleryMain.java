package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.GalleryFolderInfo;

/**
 * Created by MIN on 2015-08-26.
 */
public class MyAdapterGalleryMain extends BaseAdapter {
    private Vector<GalleryFolderInfo> Item;
    private Context Context;
    private int Resource;
    private LayoutInflater inflater;

    public MyAdapterGalleryMain(Context context,int textResource ,Vector<GalleryFolderInfo> item){
        this.Item=item;
        this.Context=context;
        this.Resource=textResource;
        this.inflater = (LayoutInflater) Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Item.size();
    }

    @Override
    public Object getItem(int position) {
        return Item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolde Viewhole;
        if(convertView==null) {
            convertView = inflater.inflate(Resource, null);
            Viewhole = new ViewHolde();
            Viewhole.FolderImage = (ImageView) convertView.findViewById(R.id.GalleryMainGridViewImage);
            Viewhole.FolderName = (TextView) convertView.findViewById(R.id.GalleryMaingridViewText);
            convertView.setTag(Viewhole);


        }else{
            Viewhole = (ViewHolde)convertView.getTag();
        }
        if(Item.get(position).getMemflag()==1){
            Viewhole.FolderImage.setImageResource(R.drawable.aaa);
        }else{
            Viewhole.FolderImage.setImageResource(R.drawable.bbb);
        }
        Viewhole.FolderName.setText(Item.get(position).getGalleryName());

        return convertView;
    }
    public class ViewHolde{
        public ImageView FolderImage;
        public TextView FolderName;
    }
}
