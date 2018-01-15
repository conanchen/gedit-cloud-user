package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.user.grpc.interceptor.AuthInterceptor;
import com.github.conanchen.gedit.user.logout.grpc.LogoutRequest;
import com.github.conanchen.gedit.user.logout.grpc.LogoutResponse;
import com.github.conanchen.gedit.user.logout.grpc.UserLogoutApiGrpc;
import com.github.conanchen.gedit.user.model.Login;
import com.github.conanchen.gedit.user.repository.LoginRepository;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Slf4j
@GRpcService
public class UserLogoutService extends UserLogoutApiGrpc.UserLogoutApiImplBase{
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
    @Autowired
    private LoginRepository loginRepository;
    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseObserver) {
        Claims claims =  AuthInterceptor.USER_CLAIMS.get();
        //inactive claim id
        Login login = (Login) loginRepository.findOne(claims.getId());
        if (login != null){
            login.setActive(false);
            loginRepository.save(login);
            log.info("user [{}] logout at: [{}]", format.format(Instant.now()));
        }else {
            log.info("unknown claim id (login id):[{}]",claims.getId());
        }
    }
}
