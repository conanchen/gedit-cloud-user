package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.user.location.grpc.UpdateMyLocationRequest;
import com.github.conanchen.gedit.user.location.grpc.UpdateMyLocationResponse;
import com.github.conanchen.gedit.user.location.grpc.UserLocationApiGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class UserLocationService extends UserLocationApiGrpc.UserLocationApiImplBase{
    @Override
    public void updateMyLocation(UpdateMyLocationRequest request, StreamObserver<UpdateMyLocationResponse> responseObserver) {
        super.updateMyLocation(request, responseObserver);
    }
}
