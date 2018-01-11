
package com.github.conanchen.gedit.user.thirdpart.sms;

import com.github.conanchen.gedit.user.thirdpart.common.NeteaseReqHeaders;
import com.github.conanchen.gedit.user.thirdpart.common.SmsResp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * 短信工具类
 */
@Slf4j
@Component
public class MsgSend {
    private static final Gson gson = new GsonBuilder().create();
    @Autowired
    private CloudSms smsApi;
    @Autowired
    private NeteaseReqHeaders headers;
    @Autowired
    private RestTemplate restTemplate;


    public boolean sendCode(String phone) throws IOException {
        String tempId= smsApi.getRegTempId();
        String url = smsApi.getCode_url();
        Map<String,Object> map = new HashMap<>();
        map.put("templateid",tempId);
        map.put("mobile",phone);
        map.put("codeLen",6);
        return post("短信发送",url,map);
    }

    /**
     * 校验指定手机号的验证码是否合法
     * @param phone 电话号码
     * @param code 验证码
     */
    public boolean verify(String phone,String code){
        String url = smsApi.getVerifycode_url();
        Map<String,Object> map = new HashMap<>();
        map.put("mobile",phone);
        map.put("code",code);
       return post("短信验证",url,map);
    }


    private boolean post(String msg,String url,Map<String,Object> map){
        try {
            HttpEntity<Map> entity = new HttpEntity<>(map,headers.create());
            log.info("{}参数：{}", msg,gson.toJson(map));
            SmsResp resp = restTemplate.postForObject(url, entity, SmsResp.class);
            log.info("{}返回值",msg,gson.toJson(resp));
            if (resp.getCode() == HttpStatus.OK.value()){
                return true;
            }
        }catch (RuntimeException e){
            log.info("{}发生异常",msg,e);
        }
        return false;
    }
}
