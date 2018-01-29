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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
            if (user ==  null){
                builder = UserProfileResponse.newBuilder()
                        .setStatus(Status.newBuilder()
                                .setCode(Status.Code.NOT_FOUND)
                                .setDetails("user not found")
                                .build());
            }else {
                builder = modelToResponse(user);
            }
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
        UpdateMyProfileResponse.Builder builder;
        try {
            Claims claims = AuthInterceptor.USER_CLAIMS.get();
            String uuid = Hope.that(claims.getSubject()).named("uuid").isNotNullOrEmpty().value();
            User user = (User) userRepository.findOne(uuid);
            switch (request.getPropertyCase()){
                case DESC:
                    String desc = Hope.that(request.getDesc()).isNotNullOrEmpty()
                            .named("desc")
                            .isTrue(n -> n.length() <= 255,"描述不能大于%s字",255)
                            .value();
                    user.setDescr(desc);
                    break;
                case LOGO:
                    String logo = Hope.that(request.getLogo())
                            .named("logo")
                            .isNotNullOrEmpty()
                            .isTrue(n -> n.length() <= 255,"描述不能大于%s字",255)
                            .value();
                    user.setLogo(logo);
                    break;
                case USERNAME:
                    String username = Hope.that(request.getUsername())
                            .named("username")
                            .isNotNullOrEmpty()
                            .isTrue(n -> n.length() <= 32,"描述不能大于%s字",32)
                            .value();
                    break;
                case ACTIVE:
                    user.setActive(request.getActive());
                    break;
                case MOBILE:
                    String mobile = Hope.that(request.getMobile()).named("mobile")
                            .isNotNullOrEmpty()
                            .matches("^(13|14|15|16|17|18|19)\\d{9}$")
                            .value();
                    user.setMobile(mobile);
                    break;
                case PHOTOS:
                    ListString photos = Hope.that(request.getPhotos()).named("photos")
                            .isNotNullOrEmpty()
                            .value();
                    String strPhotos = Hope.that(gson.toJson(photos))
                            .isTrue(n -> n.length() <=4096,"图片太多了已经装不下了,超过了%s字",4096)
                            .value();
                    user.setPhotos(strPhotos);
                    break;
                case DISTRICTUUID:
                    String districtuuid = Hope.that(request.getDistrictUuid())
                            .named("DISTRICTUUID")
                            .isNotNullOrEmpty()
                            .isTrue(n -> n.length() <= 16)
                            .value();
                    user.setDistrictUuid(districtuuid);
                    break;
                case PROPERTY_NOT_SET:
                    throw new UncheckedValidationException("no properties set");
            }
            user.setUpdatedDate(new Date());
            userRepository.save(user);
            builder = UpdateMyProfileResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.OK)
                            .setDetails("success")
                            .build());
        }catch (UncheckedValidationException e){
            builder = UpdateMyProfileResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
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
                        .setDistrictUuid(Hope.that(user.getDistrictUuid()).orElse(EMPTY_STR).value())
                        .setLogo(Hope.that(user.getLogo()).orElse(EMPTY_STR).value())
                        .setPhotos(ListString.newBuilder()
                                .addAllStrs(user.getPhotos() == null
                                        ? Collections.EMPTY_LIST
                                        : gson.fromJson(user.getPhotos(),new TypeToken<Iterable<String>>() {}.getType()))
                                .build())
                        .setMobile(user.getMobile())
                        .setUsername(Hope.that(user.getUsername()).orElse(EMPTY_STR).value())
                        .setUuid(user.getUuid())
                        .build());
    }
}
