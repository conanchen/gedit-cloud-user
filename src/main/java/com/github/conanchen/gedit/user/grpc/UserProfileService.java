package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.user.profile.grpc.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class UserProfileService extends UserProfileApiGrpc.UserProfileApiImplBase {
    @Override
    public void get(GetRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.get(request, responseObserver);
    }

    @Override
    public void findByMobile(FindByMobileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.findByMobile(request, responseObserver);
    }

    @Override
    public void list(ListRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.list(request, responseObserver);
    }

    @Override
    public void updateMyProfile(UpdateMyProfileRequest request, StreamObserver<UpdateMyProfileResponse> responseObserver) {
        super.updateMyProfile(request, responseObserver);
    }

    @Override
    public void getMyProfile(GetMyProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.getMyProfile(request, responseObserver);
    }

    @Override
    public void ban(BanUserRequest request, StreamObserver<BanUserResponse> responseObserver) {
        super.ban(request, responseObserver);
    }
}
