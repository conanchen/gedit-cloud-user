package com.github.conanchen.gedit.user.grpc;

import com.github.conanchen.gedit.common.grpc.Status;
import com.github.conanchen.gedit.user.fans.grpc.*;
import com.github.conanchen.gedit.user.grpc.interceptor.AuthInterceptor;
import com.github.conanchen.gedit.user.model.FansShip;
import com.github.conanchen.gedit.user.model.User;
import com.github.conanchen.gedit.user.repository.FansShipRepository;
import com.github.conanchen.gedit.user.repository.UserRepository;
import com.github.conanchen.gedit.user.repository.page.OffsetBasedPageRequest;
import com.martiansoftware.validation.Hope;
import com.martiansoftware.validation.UncheckedValidationException;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@GRpcService
public class UserFansService extends UserFansApiGrpc.UserFansApiImplBase {
    @Resource
    private UserRepository userRepository;
    @Resource
    private FansShipRepository fansShipRepository;
    @Override
    public void add(AddFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        FanshipResponse.Builder builder;
        try {
            Claims claims = AuthInterceptor.USER_CLAIMS.get();
            String fanUuid = Hope.that(request.getFanUuid()).named("fanUuid")
                    .isNotNullOrEmpty().value();
            if (fansShipRepository.existsByActiveIsTrueAndFanUuid(fanUuid)){
                builder = FanshipResponse.newBuilder()
                        .setStatus(Status.newBuilder().setCode(Status.Code.PERMISSION_DENIED)
                        .setDetails("被添加的fans已存在推荐人")
                        .build());
            }else{
                User user = (User) userRepository.findOne(fanUuid);
                User current = (User) userRepository.findOne(claims.getSubject());
                Date date = new Date();
                FansShip fansShip = FansShip.builder()
                        .fanName(user.getUsername())
                        .fanUuid(user.getUuid())
                        .parentUuid(current.getUuid())
                        .parentName(current.getUsername())
                        .active(true)
                        .createdDate(date)
                        .updatedDate(date)
                        .build();
                fansShipRepository.save(fansShip);
                builder = modelToResponse(fansShip,0);
            }

        }catch (UncheckedValidationException e){
            builder = FanshipResponse.newBuilder()
                    .setStatus(Status.newBuilder().setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findParent(FindParentFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        FanshipResponse.Builder builder;
        try {
            String fanUuid = Hope.that(request.getFanUuid())
                    .named("fanUuid")
                    .isNotNullOrEmpty()
                    .value();
            FansShip fansShip = fansShipRepository.findByActiveIsTrueAndFanUuid(fanUuid);
            builder = modelToResponse(fansShip,0);
        }catch (UncheckedValidationException e){
            builder = FanshipResponse.newBuilder()
                    .setStatus(Status.newBuilder()
                            .setCode(Status.Code.INVALID_ARGUMENT)
                            .setDetails(e.getMessage())
                            .build());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void listChild(ListChildFanshipRequest request, StreamObserver<FanshipResponse> responseObserver) {
        try {
            Integer from = Hope.that(request.getFrom()).named("from").isNotNull()
                    .isTrue(n -> n >= 0,"from must be greater than or equals：%s",0).value();
            Integer size = Hope.that(request.getSize()).named("size")
                    .isNotNull().isTrue(n -> n > 0,"size must be greater than %s",0).value();
            int tempForm = from == 0 ? 0 : from + 1;
            Pageable pageable = new OffsetBasedPageRequest(tempForm,size,new Sort(Sort.Direction.ASC,"createdDate"));
            String parentUuid = Hope.that(request.getParentUuid())
                    .named("parentUuid")
                    .isNotNullOrEmpty()
                    .value();
            List<FansShip> fansShips = fansShipRepository.findByActiveIsTrueAndParentUuid(parentUuid,pageable);
            for (FansShip fansShip : fansShips){
                responseObserver.onNext(modelToResponse(fansShip, tempForm++).build());
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }catch (UncheckedValidationException e){
            responseObserver.onNext(
                    FanshipResponse.newBuilder()
                            .setStatus(Status.newBuilder()
                                .setCode(Status.Code.INVALID_ARGUMENT)
                                .setDetails(e.getMessage())
                                .build())
                            .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void listMyFan(ListMyFanRequest request, StreamObserver<FanshipResponse> responseObserver) {
        try {
            Claims claims = AuthInterceptor.USER_CLAIMS.get();
            List<FansShip> fansShips = fansShipRepository.findByActiveIsTrueAndParentUuidAndUpdatedDate(claims.getSubject(),new Date(request.getLastUpdated()));
            int tempForm = 0;
            for (FansShip fansShip : fansShips){
                responseObserver.onNext(modelToResponse(fansShip, tempForm++).build());
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }catch (UncheckedValidationException e){
            responseObserver.onNext(
                    FanshipResponse.newBuilder()
                            .setStatus(Status.newBuilder()
                                    .setCode(Status.Code.INVALID_ARGUMENT)
                                    .setDetails(e.getMessage())
                                    .build())
                            .build());
        }
        responseObserver.onCompleted();
    }

    private FanshipResponse.Builder modelToResponse(FansShip ship,int from){
        return FanshipResponse.newBuilder()
                .setStatus(Status.newBuilder()
                        .setCode(Status.Code.OK)
                        .setDetails("success")
                        .build())
                .setFanship(Fanship.newBuilder()
                        .setFanName(ship.getFanName())
                        .setParentName(ship.getParentName())
                        .setParentUuid(ship.getParentUuid())
                        .setFanUuid(ship.getFanUuid())
                        .setCreated(ship.getCreatedDate().getTime())
                        .setFrom(from)
                        .build());
    }
}
