package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.user.profile.grpc.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class UserProfileService extends UserProfileApiGrpc.UserProfileApiImplBase {
    @Override
    public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
        super.add(request, responseObserver);
    }

    @Override
    public void get(GetRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.get(request, responseObserver);
    }

    @Override
    public void find(FindRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.find(request, responseObserver);
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
    public void ban(BanRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        super.ban(request, responseObserver);
    }
}
