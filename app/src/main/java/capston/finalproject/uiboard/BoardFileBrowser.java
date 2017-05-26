package capston.finalproject.uiboard;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import capston.finalproject.R;

public class BoardFileBrowser extends ListActivity {
    private List<String> item = null;
    private List<String> path = null;
    private String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boardfilebrowser);
        mPath = (TextView) findViewById(R.id.filepath);
        getDir(root);
    }

    private void getDir(String dirPath) {
        mPath.setText(dirPath);
        String Getname;
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Getname = file.getName();
            //디렉토리이거나 원하는 확장자 파일만 리스트 뷰에 출력
            if (file.isDirectory() || Getname.endsWith(".jpg") || Getname.endsWith(".gif") || Getname.endsWith(".png") || //사진
                    Getname.endsWith(".mp3") || Getname.endsWith(".wmv") ||     //음원
                    Getname.endsWith(".avi") || Getname.endsWith(".mp4") || Getname.endsWith(".mpeg") ||    //동영상
                    Getname.endsWith(".hwp") || Getname.endsWith(".pptx") || Getname.endsWith(".ppt") || Getname.endsWith(".docx") || Getname.endsWith(".pdf") || Getname.endsWith(".txt")) {//문서
                path.add(file.getPath());
                if (file.isDirectory())
                    item.add(file.getName() + "/");
                else
                    item.add(file.getName());
            }
        }
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.listitem_boardfilebrowser, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position));
            else {
                new AlertDialog.Builder(this)
                        .setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        } else {
            Intent result = new Intent();
            String str = mPath.getText().toString() + "/" + file.getName();
            result.putExtra("absolute", str);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}