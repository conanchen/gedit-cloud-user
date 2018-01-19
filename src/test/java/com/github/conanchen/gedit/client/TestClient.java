package com.github.conanchen.gedit.client;

import com.github.conanchen.gedit.hello.grpc.HelloGrpc;
import com.github.conanchen.gedit.hello.grpc.HelloRequest;
import com.github.conanchen.gedit.user.CloudUserApplication;
import com.github.conanchen.gedit.user.auth.grpc.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Key;
import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudUserApplication.class)
public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);
    private static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.MILLISECOND_FIELD).setPrettyPrinting().create();
    private UserAuthApiGrpc.UserAuthApiBlockingStub blockingStub;
    private HelloGrpc.HelloBlockingStub helloBlockingStub;
    @Value("${jjwt.expire.minutes:5}")
    private Long expiredInMinutes;
    @Before
    public void init(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1",9980)
                .usePlaintext(true)
                .build();
        blockingStub = UserAuthApiGrpc.newBlockingStub(channel);
        helloBlockingStub = HelloGrpc.newBlockingStub(channel);
    }

    @Test
    public void testRegister1(){
        SmsStep1QuestionResponse response = blockingStub.registerSmsStep1Question(SmsStep1QuestionRequest.newBuilder()
                .build()
        );
        log.info(gson.toJson(response));
    }
    @Test
    public void testRegister2(){
        SmsStep1QuestionResponse response1 = blockingStub.registerSmsStep1Question(SmsStep1QuestionRequest.newBuilder()
                .build()
        );
        log.info(gson.toJson(response1));
        Assert.assertNotEquals(response1,null);
        List<Question> questions = response1.getQuestionList();
        String[] uuidsArray = new String[3];
        int i = 0;
        for (Question question : questions){
            uuidsArray[i++] = question.getUuid();
            if (i == 3){
                break;
            }
        }
        Iterable<String> uuids = new ArrayList<>();
        SmsStep2AnswerResponse response2 = blockingStub.registerSmsStep2Answer(SmsStep2AnswerRequest.newBuilder()
                .setMobile("15281718791")
                .setToken(response1.getToken())
                .addAllQuestionUuid(uuids)
                .build()
        );
        log.info(gson.toJson(response2));
    }
    @Test
    public void testRegisterStep3(){
        RegisterResponse response = blockingStub.registerSmsStep3Register(SmsStep3RegisterRequest.newBuilder()
                .setMobile("15281718792")
                .setPassword("123456")
                .setSmscode("123456")
                .build()
        );
        log.info(gson.toJson(response));
    }

    @Test
    public void testSignIn(){
        SigninResponse response = blockingStub.signinWithPassword(SigninWithPasswordRequest.newBuilder()
                .setMobile("15281718791")
                .setPassword("123456")
                .build()
        );
        log.info(gson.toJson(response));
    }
    @Test
    public void testJJwt(){
        String compactJws =  Jwts.builder()
                .setIssuedAt(new Date()) //need create  login record
                .setSubject("1")
                .compressWith(CompressionCodecs.GZIP)
                .signWith(SignatureAlgorithm.HS512, "fwefwefew")
                .setExpiration(expireDate())
                .compact();
        Jws<Claims> claimsJwt = Jwts.parser().setSigningKey("fwefwefew").parseClaimsJws(compactJws);
        Assert.assertEquals(claimsJwt.getBody().getSubject(),"1");
    }
    @Test
    public void testJJwtSample(){
        Key key = MacProvider.generateKey();

        String compactJws = Jwts.builder()
                .setSubject("Joe")
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        assert Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject().equals("Joe");
    }
    private Date expireDate(){
        //time calc
        Instant now = Instant.now();
        Instant future = now.plus(Duration.ofMinutes(expiredInMinutes));
        return  Date.from(future);
    }
    @Test
    public void hello(){
        helloBlockingStub.sayHello(HelloRequest.newBuilder().build());
    }
}
