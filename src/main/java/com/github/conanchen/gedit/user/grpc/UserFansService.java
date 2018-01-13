package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.user.fans.grpc.*;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class UserFansService extends UserFansApiGrpc.UserFansApiImplBase {

    @Override
    public void add(AddFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        super.add(request, responseObserver);
    }

    @Override
    public void findParent(FindParentFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        super.findParent(request, responseObserver);
    }

    @Override
    public void listChild(ListChildFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        super.listChild(request, responseObserver);
    }
}
