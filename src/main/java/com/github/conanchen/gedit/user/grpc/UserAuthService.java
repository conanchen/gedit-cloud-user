package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.common.grpc.Status;
import com.github.conanchen.gedit.user.auth.grpc.*;
import com.github.conanchen.gedit.user.model.User;
import com.github.conanchen.gedit.user.repository.UserRepository;
import com.github.conanchen.gedit.user.service.CaptchaService;
import com.github.conanchen.gedit.user.thirdpart.sms.MsgSend;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Date;

import static io.grpc.Status.Code.ALREADY_EXISTS;
import static io.grpc.Status.Code.FAILED_PRECONDITION;

@GRpcService
public class UserAuthService extends UserAuthApiGrpc.UserAuthApiImplBase{
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
        responseObserver.onNext(captchaService.verifyRegister(request));
        responseObserver.onCompleted();
    }

    @Override
    public void registerSmsStep3Signin(SmsStep3SigninRequest request, StreamObserver<SigninResponse> responseObserver) {
        Status status = null;
        try {
            if (msgSend.verify(request.getMobile(),request.getSmscode())){
                Date now = new Date();
                User user = User.builder()
                        .active(true)
                        .createdDate(now)
                        .updatedDate(now)
                        .mobile(request.getMobile())
                        // TODO need a password in param
                        .build();
                userRepository.save(user);
            }else{
                status = Status.newBuilder()
                        .setCode(String.valueOf(FAILED_PRECONDITION.value()))
                        .setDetails("验证失败，请重试")
                        .build();
            }
        }catch (DuplicateKeyException e){
            status = Status.newBuilder()
                    .setCode(String.valueOf(ALREADY_EXISTS.value()))
                    .setDetails("用户已注册,请返回登录")
                    .build();
        }
        SigninResponse response =  SigninResponse.newBuilder().setStatus(status).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
