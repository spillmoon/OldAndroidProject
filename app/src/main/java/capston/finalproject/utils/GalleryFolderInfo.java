package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-26.
 */
public class GalleryFolderInfo {
    String galleryNo,galleryName;
    int memflag;

    public GalleryFolderInfo(String NO,String Name,int flag){
        this.galleryNo=NO;
        this.galleryName=Name;
        this.memflag=flag;
    }

    public int getMemflag() {
        return memflag;
    }

    public String getGalleryName() {
        return galleryName;
    }

    public String getGalleryNo() {
        return galleryNo;
    }

    public void setGalleryName(String galleryName) {
        this.galleryName = galleryName;
    }

    public void setGalleryNo(String galleryNo) {
        this.galleryNo = galleryNo;
    }

    public void setMemflag(int memflag) {
        this.memflag = memflag;
    }
}
