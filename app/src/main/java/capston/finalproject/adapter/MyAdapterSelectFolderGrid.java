package capston.finalproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Vector;

import capston.finalproject.R;
import capston.finalproject.utils.GalleryFolderInfo;
import capston.finalproject.utils.RoundedTransform;
import capston.finalproject.utils.ServerUrl;

/**
 * Created by M on 2015-10-10.
 */
public class MyAdapterSelectFolderGrid extends BaseAdapter {
    private Vector<GalleryFolderInfo> Item;
    private Context Context;
    private int Resource;
    private LayoutInflater inflater;
    public ImageView FolderImage;
    public TextView FolderName;
    ServerUrl Serverurl = new ServerUrl();
    public MyAdapterSelectFolderGrid(Context context, int textResource, Vector<GalleryFolderInfo> item) {
        this.Item = item;
        this.Context = context;
        this.Resource = textResource;
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

        if (convertView == null) {
            convertView = inflater.inflate(Resource, null);
            FolderImage = (ImageView) convertView.findViewById(R.id.SelectFolderGridViewItemImage);
         //   FolderName = (TextView) convertView.findViewById(R.id.SelectFolderGridViewItemTxet);
        } else {
        }
        RoundedTransform transForm = new RoundedTransform(10, 0);//이미지 라운딩처리 클래스

        Picasso.with(Context)//Context
                .load(Serverurl.getServerPath()+Item.get(position).getGalleryName())//URL
                .placeholder(R.drawable.aaa)//Default Image
                .resize(100,100)
                .into(FolderImage);//View
        //FolderName.setText(Item.get(position).getGalleryName());

        return convertView;
    }

}