package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-07.
*/

//폴더 리스트 정보
public class GalleryFolder {
    String folderName;
    String foldermaker;
    String imageCount;
    String folderNo;
    public GalleryFolder(String folderName,String foldermaker,String imageCount,String folderNo){
        this.folderName=folderName;
        this.foldermaker=foldermaker;
        this.imageCount=imageCount;
        this.folderNo=folderNo;
    }
    public String getFolderName(){
        return folderName;
    }
    public void setFolderName(String foldername){
        this.folderName=foldername;
    }
    public String getFoldermaker(){
        return foldermaker;
    }
    public void setFoldermaker(String foldermaker){
        this.foldermaker=foldermaker;
    }
    public String getImageCount(){
        return imageCount;
    }
    public void setImageCount(String imageCount){
        this.imageCount=imageCount;
    }
    public String getFolderNo(){
        return folderNo;
    }
    public void setFolderNo(String folderNo){
        this.folderNo=folderNo;
    }
}
