package capston.finalproject.utils;

public class ServerUrl {
    String url;
    String ServerPath;
/*
    public ServerUrl(){
        url="http://192.168.43.23:8080/memoryShare/";
        ServerPath="http://192.168.43.23:8080/memoryShare/data/";
    }*/
    public ServerUrl(){
        //url="http://memoryShare.kangnam.ac.kr:8888/memoryShare/";
        //ServerPath="http://memoryShare.kangnam.ac.kr:8888/memoryShare/data/";
        url = "http://70.12.111.149:8080/memoryShare/";
        ServerPath = "http://70.12.111.149:8080/memoryShare/data";
    }
    public String getServerUrl(){
        return url;
    }

    public String getServerPath(){
        return ServerPath;
    }
}