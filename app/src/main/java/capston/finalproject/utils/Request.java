package capston.finalproject.utils;

/**
 * Created by MIN on 2015-08-10.
 */
public class Request {
    String imageName;
    String folderName;
    String requestId;
    String picNO;

    public Request(String imageName, String folderName, String requestId, String picNO) {
        this.imageName = imageName;
        this.folderName = folderName;
        this.requestId = requestId;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPicNO() {
        return picNO;
    }

    public void setPicNO(String picNO) {
        this.picNO = picNO;
    }

}
