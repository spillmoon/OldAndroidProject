package capston.finalproject.utils;

public class BoardUtil {
    String folder;
    String title;
    String mem;
    String date;
    String boardno;
    String folderno;
    String foldername;

    public BoardUtil(String no, String folder,String title,String mem,String date){
        this.boardno=no;
        this.folder=folder;
        this.title=title;
        this.mem=mem;
        this.date=date;
    }
    public BoardUtil(String no,String foldername){
        this.folderno=no;
        this.foldername=foldername;
    }

    public String getFoldername() {
        return foldername;
    }

    public String getFolder() {
        return folder;
    }

    public String getTitle() {
        return title;
    }

    public String getMem() {
        return mem;
    }

    public String getDate() {
        return date;
    }

    public String getBoardno() {
        return boardno;
    }

    public String getFolderno() {
        return folderno;
    }
}
