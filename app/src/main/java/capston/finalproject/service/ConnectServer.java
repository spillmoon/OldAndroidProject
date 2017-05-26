package capston.finalproject.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import capston.finalproject.utils.ServerUrl;

public class ConnectServer {
    int SUCCESS=1;
    int FAIL=2;
    int VALID=3;
    int INVALID=4;
    int NOID=5;
    int NOPW=6;
    int APPLY_SUCCESS=7;
    int APPLY_FAIL=8;
    int ALREADY_APPLY=9;
    int TRANSFERED=10;
    int LEADER=11;
    int NOMAL=12;

    public ConnectServer(){
    }
    public int ScheduleDel(String url, ArrayList<NameValuePair> param) {
        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "calendarDelete.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(param, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            String result = EntityUtils.toString(resEntity);
            result = result.trim();
            if ("success".equals(result)) {// 사용가능하면 등록 활성화
                return 0;
            } else if ("fail".equals(result)) {// 불가능하면 비활성화
                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public int ScheduleModif(String url, ArrayList<NameValuePair> param) {
        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(new ServerUrl().getServerUrl() + "calendarEdit.do");
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(param, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            String result = EntityUtils.toString(resEntity);
            result = result.trim();
            if ("success".equals(result)) {// 사용가능하면 등록 활성화
                return 0;
            } else if ("fail".equals(result)) {// 불가능하면 비활성화
                return 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public int getSuccessFail(String url, ArrayList<NameValuePair> param){
        try {// http 통신준비
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(url);
            UrlEncodedFormEntity entityRequest=new UrlEncodedFormEntity(param,"EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            String result= EntityUtils.toString(resEntity);
            result=result.trim();
            if("success".equals(result)){
                return SUCCESS;
            }
            else if("fail".equals(result)) {
                return FAIL;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public int sendGet(String url, ArrayList<NameValuePair> param){
        try {// http 통신준비
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(url);
            UrlEncodedFormEntity entityRequest=new UrlEncodedFormEntity(param,"EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            String result= EntityUtils.toString(resEntity);
            result=result.trim();
            if ("transfered".equals(result)) {
                return TRANSFERED;
            } else if ("leader".equals(result)) {
                return LEADER;
            } else if ("normal".equals(result)) {
                return NOMAL;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public String getJsonArray(String url, ArrayList<NameValuePair> param){
        try {
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(url);
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(param, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            InputStream stream = resEntity.getContent();
            //JSON string ������
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream,"EUC-KR"));
            String line;
            String result="";
            while((line=bufreader.readLine())!=null){
                result+=line;
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public int doRoomApply(String url, ArrayList<NameValuePair> param){
        try {
            HttpClient http = new DefaultHttpClient();
            HttpParams params = http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity entityRequest = new UrlEncodedFormEntity(param, "EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost = http.execute(httpPost);
            HttpEntity resEntity = responsePost.getEntity();
            String message = EntityUtils.toString(resEntity);
            message = message.trim();
            if("applySuccess".equals(message)){
                return APPLY_SUCCESS;
            }
            else if("applyFail".equals(message)) {
                return APPLY_FAIL;
            }
            else if("alreadyApply".equals(message)){
                return ALREADY_APPLY;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int doCheck(String url, ArrayList<NameValuePair> param){
        try {// http 통신준비
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(url);
            UrlEncodedFormEntity entityRequest=new UrlEncodedFormEntity(param,"EUC-KR");
            httpPost.setEntity(entityRequest);
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            String result= EntityUtils.toString(resEntity);
            result=result.trim();
            if("valid".equals(result)){
                return VALID;
            }
            else if("invalid".equals(result)) {
                return INVALID;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    public int login(String url, ArrayList<NameValuePair> param){
        try {// http 통신준비
            HttpClient http=new DefaultHttpClient();
            HttpParams params=http.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            HttpPost httpPost=new HttpPost(url);
            UrlEncodedFormEntity entityRequest=new UrlEncodedFormEntity(param,"EUC-KR");
            httpPost.setEntity(entityRequest);
            //서버로 전송 후 결과값 받아옴
            HttpResponse responsePost= http.execute(httpPost);
            HttpEntity resEntity=responsePost.getEntity();
            String result= EntityUtils.toString(resEntity);
            result=result.trim();
            //결과값(플래그)에 따라 분기(성공, wrong id/pw)
            if("success".equals(result)){
                return SUCCESS;
            }
            else if("noid".equals(result)) {
                return NOID;
            }
            else if("nopw".equals(result)){
                return NOPW;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }
}
