package com.github.conanchen.gedit.user.thirdpart.common;

import com.github.conanchen.gedit.user.thirdpart.sms.CheckSumBuilder;
import com.github.conanchen.gedit.user.thirdpart.sms.CloudSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class NeteaseReqHeaders {
    private static final String product = "product";
    @Autowired
    private CloudSms smsApi;
    @Value("${spring.profiles.actice")
    private String profile;
    public HttpHeaders create(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String app_key;
        String app_secret;
        if (product.equals(product)) {
            app_key = smsApi.getNetease_key();
            app_secret = smsApi.getNetease_secret();
        }else{
            app_key = smsApi.getNetease_test_key();
            app_secret = smsApi.getNetease_test_secret();
        }
        String NONCE = smsApi.getNonce();
        String curTime=String.valueOf((System.currentTimeMillis()/1000L));
        String checkSum= CheckSumBuilder.getCheckSum(app_secret,NONCE,curTime);
        //设置请求的header
        headers.add("AppKey",app_key);
        headers.add("Nonce",NONCE);
        headers.add("CurTime",curTime);
        headers.add("CheckSum",checkSum);
        return headers;
    }
}
