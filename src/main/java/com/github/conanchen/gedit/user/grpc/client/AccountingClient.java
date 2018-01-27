package com.github.conanchen.gedit.user.grpc.client;

import com.github.conanchen.gedit.accounting.account.grpc.AccountResponse;
import com.github.conanchen.gedit.accounting.account.grpc.AccountingAccountApiGrpc;
import com.github.conanchen.gedit.accounting.account.grpc.UpsertAccountsRequest;
import com.github.conanchen.gedit.user.model.User;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
@Slf4j
@Component
public class AccountingClient {
    @Value("${accounting.uri}")
    private String uri;
    //channel
    private ManagedChannel channel;
    // search stub
    private AccountingAccountApiGrpc.AccountingAccountApiStub accountingAccountApiStub;
    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(uri,9983)
                .usePlaintext(true)
                .build();
        accountingAccountApiStub = AccountingAccountApiGrpc.newStub(channel);
    }

    public void upsesrtAccounts(User user){
        accountingAccountApiStub.upsertAccounts(UpsertAccountsRequest.newBuilder()
                .setUserUuid(user.getUuid())
                .build()
                ,new StreamObserver<AccountResponse>(){

                    @Override
                    public void onNext(AccountResponse value) {
                        log.info("user account upsert success");
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.info("user account upsert error",t);
                    }

                    @Override
                    public void onCompleted() {
                        log.info("user account upsert complete");
                    }
                }
        );
    }
}
