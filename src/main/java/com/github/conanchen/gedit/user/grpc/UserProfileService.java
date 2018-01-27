package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.common.grpc.ListString;
import com.github.conanchen.gedit.common.grpc.Status;
import com.github.conanchen.gedit.user.grpc.interceptor.AuthInterceptor;
import com.github.conanchen.gedit.user.model.User;
import com.github.conanchen.gedit.user.profile.grpc.*;
import com.github.conanchen.gedit.user.repository.UserRepository;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.martiansoftware.validation.Hope;
import com.martiansoftware.validation.UncheckedValidationException;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import org.lognet.springboot.grpc.GRpcService;

import javax.annotation.Resource;

@GRpcService
public class UserProfileService extends UserProfileApiGrpc.UserProfileApiImplBase {
    private static final String EMPTY_STR = "";
    private final static Gson gson = new Gson();
    @Resource
    private UserRepository userRepository;
    @Override
    public void get(GetRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        UserProfileResponse.Builder builder;
        try {
            String uuid = Hope.that(request.getUuid()).named("uuid").isNotNullOrEmpty().value();
            User user = (User) userRepository.findOne(uuid);
            builder = modelToResponse(user);
        }catch (UncheckedValidationException e){
            builder = UserProfileResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findByMobile(FindByMobileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        UserProfileResponse.Builder builder;
        try {
            String mobile = Hope.that(request.getMobile()).named("mobile")
                    .isNotNullOrEmpty()
                    .matches("^(13|14|15|16|17|18|19)\\d{9}$")
                    .value();
            User user = userRepository.findByMobile(mobile);
            builder = modelToResponse(user);
        }catch (UncheckedValidationException e){
            builder = UserProfileResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void list(ListRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.list(request, responseObserver);
    }

    @Override
    public void updateMyProfile(UpdateMyProfileRequest request, StreamObserver<UpdateMyProfileResponse> responseObserver) {
        try {

        }catch (UncheckedValidationException e){

        }
    }

    @Override
    public void getMyProfile(GetMyProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        UserProfileResponse.Builder builder;
        try {
            Claims claims = AuthInterceptor.USER_CLAIMS.get();
            String uuid = Hope.that(claims.getSubject()).named("uuid").isNotNullOrEmpty().value();
            User user = (User) userRepository.findOne(uuid);
            builder = modelToResponse(user);
        }catch (UncheckedValidationException e){
            builder = UserProfileResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void ban(BanUserRequest request, StreamObserver<BanUserResponse> responseObserver) {
        super.ban(request, responseObserver);
    }

    private UserProfileResponse.Builder modelToResponse(User user){
        return UserProfileResponse.newBuilder()
                .setStatus(Status.newBuilder()
                        .setCode(Status.Code.OK)
                        .setDetails("success")
                        .build())
                .setUserProfile(UserProfile.newBuilder()
                        .setActive(user.getActive())
                        .setDesc(Hope.that(user.getDescr()).orElse(EMPTY_STR).value())
                        .setDistrictId(Hope.that(user.getDistrictUuid()).orElse(EMPTY_STR).value())
                        .setLogo(Hope.that(user.getLogo()).orElse(EMPTY_STR).value())
                        .setPhotos(ListString.newBuilder()
                                .addAllStrs(user.getPhotos() == null ? null : gson.fromJson(user.getPhotos(),new TypeToken<Iterable<String>>() {}.getType()))
                                .build())
                        .setMobile(user.getMobile())
                        .setName(Hope.that(user.getName()).orElse(EMPTY_STR).value())
                        .setUuid(user.getUuid())
                        .build());
    }
}
