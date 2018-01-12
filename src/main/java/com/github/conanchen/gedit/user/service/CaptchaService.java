package com.github.conanchen.gedit.user.service;

import com.github.conanchen.gedit.user.auth.grpc.Question;
import com.github.conanchen.gedit.user.auth.grpc.SmsStep1QuestionResponse;
import com.github.conanchen.gedit.user.auth.grpc.SmsStep2AnswerRequest;
import com.github.conanchen.gedit.user.auth.grpc.SmsStep2AnswerResponse;
import com.github.conanchen.gedit.user.model.CaptchaImg;
import com.github.conanchen.gedit.user.model.CaptchaType;
import com.github.conanchen.gedit.user.repository.CaptchaImgRepository;
import com.github.conanchen.gedit.user.repository.CaptchaTypeRepository;
import com.github.conanchen.gedit.user.repository.UserRepository;
import com.github.conanchen.gedit.user.repository.page.OffsetBasedPageRequest;
import com.github.conanchen.gedit.user.thirdpart.sms.MsgSend;
import com.github.conanchen.gedit.user.utils.EntityUtils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CaptchaService {
    private static final String product = "product";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaImgRepository captchaImgRepository;
    @Autowired
    private CaptchaTypeRepository captchaTypeRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MsgSend msgSend;
    @Value("${spring.profiles.active}")
    private String profile;
    @Value("${captcha.expire.minute}")
    private Long expireMinutes;

    private boolean isProduct;

    @PostConstruct
    public void init(){
        isProduct = product.equals(profile);
    }

    private CaptchaResult userDB(){
        long count = captchaTypeRepository.countByActive(true);
        if (count < 1){
            return null;
        }
        Random random = new Random();
        long rand = random.longs(1L,count + 1).limit(1).findFirst().getAsLong();
        CaptchaType type = captchaTypeRepository.findByActive(true,new OffsetBasedPageRequest((int)rand -1,1));
        while (type == null){
            type = captchaTypeRepository.findByActive(true,new OffsetBasedPageRequest(0,1));
        }
        long countImgs = captchaImgRepository.countByTypeUuid(type.getUuid());
        long randImg = random.longs(1,countImgs + 1).limit(1).findFirst().getAsLong();
        int randCount = random.ints(1,7).limit(1).findFirst().getAsInt();
        List<CaptchaImg> captchaImgList = captchaImgRepository.findByTypeUuid(type.getUuid(),new OffsetBasedPageRequest((int)randImg - 1,randCount));
        List<String> ids = EntityUtils.createFieldList(captchaImgList, "uuid");
        String verifyId = UUID.randomUUID().toString();
        //redis operation
        BoundValueOperations<String,List<String>> boundValueOperations =  redisTemplate.boundValueOps("captcha_img_ids_" + verifyId);
        boundValueOperations.set(ids,expireMinutes, TimeUnit.MINUTES);

        if (captchaImgList.size() < 6){
           long countAllImgs =  captchaImgRepository.countByTypeUuidNot(type.getUuid());
           long ranImgAll =  random.longs(1,countAllImgs + 1).limit(1).findFirst().getAsLong();
           int limit = 6 - captchaImgList.size();
           long left = countAllImgs - ranImgAll;
           int offset = Long.compare(left,limit) < 0 ? (int)(left - 1): (int)(countAllImgs - 7L);
           List<CaptchaImg> others = captchaImgRepository.findByTypeUuidNot(type.getUuid(),new OffsetBasedPageRequest(offset ,limit));
           captchaImgList.addAll(others);
        }
        Collections.shuffle(captchaImgList);
        return new CaptchaResult(type.getName(),verifyId,captchaImgList);
    }
    public SmsStep1QuestionResponse listImgs(){
        CaptchaResult result = userDB();
        if (result == null){
            return null;
        }
        List<Question> questions = new ArrayList<>(result.getImgs().size());
        for (CaptchaImg captchaImg : result.getImgs()){
            Question  question = Question.newBuilder().setUuid(captchaImg.getUuid()).setImage(captchaImg.getUrl()).build();
            questions.add(question);
        }
        return SmsStep1QuestionResponse.newBuilder().setQuestionTip(result.getTip()).setToken(result.getToken()).addAllQuestion(questions).build();
    }
    public SmsStep2AnswerResponse verifyResetPassword(SmsStep2AnswerRequest request){
        boolean exists = userRepository.existsByMobile(request.getMobile());
        if ( exists){
            throw new StatusRuntimeException(Status.ALREADY_EXISTS.withDescription("用户已注册,如忘记密码请重置"));
        }
        return verify(request);
    }

    public SmsStep2AnswerResponse verifyRegister(SmsStep2AnswerRequest request){
        boolean exists = userRepository.existsByMobile(request.getMobile());
        if ( !exists){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription("用户未注册,请返回注册"));
        }
        return verify(request);
    }

    private SmsStep2AnswerResponse verify(SmsStep2AnswerRequest request){
        com.google.protobuf.ProtocolStringList requestQuestionUuidList = request.getQuestionUuidList();
        BoundValueOperations<String,List<String>> boundValueOperations =  redisTemplate.boundValueOps("captcha_img_ids_" + request.getToken());
        List<String> listIds = boundValueOperations.get();
        if (equals(listIds,requestQuestionUuidList)) {
            if (isProduct) {
                if (msgSend.sendCode(request.getMobile())) {
                    log.info("验证码发送成功：{}", request.getMobile());
                }else{
                    log.warn("验证码发送失败");
                    throw new StatusRuntimeException(Status.UNAVAILABLE.withDescription("短信验证暂不可用"));
                }
            }
        }
        return SmsStep2AnswerResponse.newBuilder().setStatus(com.github.conanchen.gedit.common.grpc.Status.newBuilder().setCode(String.valueOf(Status.OK.getCode().value()))).build();
    }

    private boolean equals(List<String> list, com.google.protobuf.ProtocolStringList requestQuestionUuidList){
        try {
            if (!CollectionUtils.isEmpty(list)){
                if (list.size() == requestQuestionUuidList.size()) {
                    int right = 0;
                    for (String id : list) {
                        for (int index = 0; index < requestQuestionUuidList.size();index++) {
                            if (id.equals(requestQuestionUuidList.get(index))) {
                                right ++;
                                break;
                            }
                        }
                    }
                    if (right == list.size()){
                        return true;
                    }
                }
            }
        }catch (NumberFormatException e){
            //ignore
        }
        return false;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class CaptchaResult{
        private String tip;
        private String token;
        private List<CaptchaImg> imgs;
    }
}
