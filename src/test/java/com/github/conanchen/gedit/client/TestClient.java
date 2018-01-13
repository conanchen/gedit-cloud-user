package com.github.conanchen.gedit.client;

import com.github.conanchen.gedit.user.CloudUserApplication;
import com.github.conanchen.gedit.user.auth.grpc.SigninResponse;
import com.github.conanchen.gedit.user.auth.grpc.SmsStep3RegisterRequest;
import com.github.conanchen.gedit.user.auth.grpc.UserAuthApiGrpc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CloudUserApplication.class)
public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);
    private static final Gson gson = new GsonBuilder().setDateFormat(DateFormat.MILLISECOND_FIELD).setPrettyPrinting().create();
    private UserAuthApiGrpc.UserAuthApiBlockingStub blockingStub;

    @Before
    public void init(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1",9980)
                .usePlaintext(true)
                .build();
        blockingStub = UserAuthApiGrpc.newBlockingStub(channel);
    }
    @Test
    public void testRegister(){
        SigninResponse response = blockingStub.registerSmsStep3Signin(SmsStep3RegisterRequest.newBuilder()
                .setMobile("15281718792")
                .setPassword("123456")
                .setSmscode("123456")
                .build()
        );
        log.info(gson.toJson(response));
    }
}
