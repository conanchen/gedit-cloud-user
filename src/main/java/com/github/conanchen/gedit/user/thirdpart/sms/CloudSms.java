package com.github.conanchen.gedit.user.thirdpart.sms;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * @author hai
 * @desc 网易云信常量工具类
 * @date 2017/2/6 10:37
 */
@Slf4j
@Data
@PropertySources(
        {
                @PropertySource(value = "classpath:/config/netease.properties"),
                @PropertySource(value = "classpath:/config/security.properties")
        }
)
@Component
public class CloudSms {
   /* @PostConstruct
    private void init(){
        String product = "product";
        String profile = environment.getProperty("spring.profiles.active", product);
        boolean enable = product.equals(profile);
        if (enable){
            app_key = properties.getProperty("app_key_product");
            app_secret = properties.getProperty("app_secret_product");
            log.info("the product key use in neteasecloud ");
        }else{
            app_key = properties.getProperty("app_key_test");
            app_secret = properties.getProperty("app_secret_test");
            log.info("the test key use in neteasecloud ");
        }
    }*/
    //private static final Properties properties = new Properties();
    /**
     * 模版API
     */
    @Value("${sms.template_url}")
    private String template_url;

    /**
     * 验证码API
     */
    @Value("${sms.code_url}")
    private String code_url;
    /**
     * 校验指定手机号的验证码是否合法api
     */
    @Value("${sms.verifycode_url}")
    private String verifycode_url;
    /**
     * 查询信息是否发送成功
     */
    @Value("${sms.status_url}")
    private String status_url;

    /**
     * APP_SECRET
     */
    @Value("${netease_test_secret}")
    private String netease_test_secret;
    /**
     * APP_KEY
     */
    @Value("${netease_test_key}")
    private String netease_test_key;

    /**
     * APP_KEY
     */
    @Value("${netease_key}")
    private String netease_key;
    /**
     * APP_SECRET
     */
    @Value("${netease_secret}")
    private String netease_secret;

    @Value("${nonce}")
    private String nonce;
    /**
     * 非验证码短信模版id
     */
    @Value("${sms.regTempId}")
    private String regTempId;
    /**
     * 短信验证码模版id
     */
    @Value("${sms.vcodeTempId}")
    private String vcodeTempId;
}
