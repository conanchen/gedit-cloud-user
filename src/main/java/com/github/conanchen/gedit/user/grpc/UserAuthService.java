package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.common.grpc.Status;
import com.github.conanchen.gedit.user.auth.grpc.*;
import com.github.conanchen.gedit.user.grpc.interceptor.AuthInterceptor;
import com.github.conanchen.gedit.user.grpc.interceptor.LogInterceptor;
import com.github.conanchen.gedit.user.model.User;
import com.github.conanchen.gedit.user.repository.UserRepository;
import com.github.conanchen.gedit.user.service.CaptchaService;
import com.github.conanchen.gedit.user.thirdpart.sms.MsgSend;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.*;
import java.util.Date;

import static io.grpc.Status.Code.*;

@GRpcService(applyGlobalInterceptors = false,interceptors = {LogInterceptor.class})
public class UserAuthService extends UserAuthApiGrpc.UserAuthApiImplBase{
    @Value("${jjwt.expire.minutes:5}")
    private Long expiredInMinutes;
    @Value("${jjwt.sigin.key:shuai}")
    private String signinKey;
    @Value("${sms.active:false}")
    private Boolean smsActive;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MsgSend msgSend;
    @Override
    public void signinQQ(SigninQQRequest request, StreamObserver<SigninResponse> responseObserver) {
    }

    @Override
    public void signinWechat(SigninWechatRequest request, StreamObserver<SigninResponse> responseObserver) {

    }

    @Override
    public void signinWeibo(SigninWeiboRequest request, StreamObserver<SigninResponse> responseObserver) {
    }

    @Override
    public void signinWithPassword(SigninWithPasswordRequest request, StreamObserver<SigninResponse> responseObserver) {
        SigninResponse.Builder builder = SigninResponse.newBuilder();
        User user = userRepository.findByMobile(request.getMobile());
        if (!user.getActive()){
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(FAILED_PRECONDITION.value()))
                    .setDetails("账户被禁用")
                    .build();
            builder.setStatus(status);
        }
        if (DigestUtils.sha256Hex(request.getPassword()).equals(user.getPassword())){
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(OK.value()))
                    .setDetails("登录成功")
                    .build();
            builder.setStatus(status);
        }else{
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(INVALID_ARGUMENT.value()))
                    .setDetails("用户名或密码错误")
                    .build();
            builder.setStatus(status);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void signinSmsStep1Question(SmsStep1QuestionRequest request, StreamObserver<SmsStep1QuestionResponse> responseObserver) {
        responseObserver.onNext(captchaService.listImgs());
        responseObserver.onCompleted();
    }

    @Override
    public void signinSmsStep2Answer(SmsStep2AnswerRequest request, StreamObserver<SmsStep2AnswerResponse> responseObserver) {
    }

    @Override
    public void signinSmsStep3Signin(SmsStep3SigninRequest request, StreamObserver<SigninResponse> responseObserver) {
    }

    @Override
    public void registerSmsStep1Question(SmsStep1QuestionRequest request, StreamObserver<SmsStep1QuestionResponse> responseObserver) {
        responseObserver.onNext(captchaService.listImgs());
        responseObserver.onCompleted();
    }

    @Override
    public void registerSmsStep2Answer(SmsStep2AnswerRequest request, StreamObserver<SmsStep2AnswerResponse> responseObserver) {
        try{
            responseObserver.onNext(captchaService.verifyRegister(request));
            responseObserver.onCompleted();
        }catch (StatusRuntimeException e){
            responseObserver.onError(e);
        }
    }

    @Override
    public void registerSmsStep3Signin(SmsStep3RegisterRequest request, StreamObserver<SigninResponse> responseObserver) {
        SigninResponse.Builder builder = SigninResponse.newBuilder();
        try {
            if (!smsActive || msgSend.verify(request.getMobile(),request.getSmscode())){
                Date now = new Date();
                User user = User.builder()
                        .active(true)
                        .createdDate(now)
                        .updatedDate(now)
                        .mobile(request.getMobile())
                        .password(DigestUtils.sha256Hex(request.getPassword()))
                        .build();
                userRepository.save(user);
                //calc expire time
                Date date = expireDate();
                String compactJws = generate(user.getUuid(),now,date);;
                Status status = Status.newBuilder()
                        .setCode(String.valueOf(OK.value()))
                        .setDetails("注册成功")
                        .build();
                builder.setStatus(status)
                        .setExpiresIn(String.valueOf(date.getTime()))
                        .setAccessToken(AuthInterceptor.AUTHENTICATION_SCHEME + compactJws);
            }else{
                Status status = Status.newBuilder()
                        .setCode(String.valueOf(FAILED_PRECONDITION.value()))
                        .setDetails("验证失败，请重试")
                        .build();
                builder.setStatus(status);
            }
        }catch (DuplicateKeyException e){
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(ALREADY_EXISTS.value()))
                    .setDetails("用户已注册,请返回登录")
                    .build();
            builder.setStatus(status);
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    private String generate(String uuid,Date issuedAt,Date expiredDate){
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(issuedAt) // need create login record
                .setSubject(uuid)
                .compressWith(CompressionCodecs.GZIP)
                .signWith(SignatureAlgorithm.HS512, signinKey)
                .setExpiration(expiredDate)
                .compact();
    }
    private Date expireDate(){
        //time calc
        Instant now = Instant.now();
        now.plus(Duration.ofMinutes(expiredInMinutes));
        return  Date.from(now);
    }
}
