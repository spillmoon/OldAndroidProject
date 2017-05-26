package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-11.
 */
public class Requested {
    String imageName;
    String folderName;
    String picNO;

    public Requested(String picNO, String folderName, String imageName) {
        this.imageName = imageName;
        this.folderName = folderName;
        this.picNO = picNO;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getPicNO() {
        return picNO;
    }

    public void setPicNO(String picNO) {
        this.picNO = picNO;
    }

}

