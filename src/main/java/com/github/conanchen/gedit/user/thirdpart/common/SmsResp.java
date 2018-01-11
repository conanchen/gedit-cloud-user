package com.github.conanchen.gedit.user.thirdpart.common;

import lombok.Data;

@Data
public class SmsResp {
    private Integer code;

    private String msg;

    private Object obj;
}
