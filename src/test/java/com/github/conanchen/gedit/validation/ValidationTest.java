package com.github.conanchen.gedit.validation;

import com.martiansoftware.validation.Hope;
import com.martiansoftware.validation.UncheckedValidationException;
import org.junit.Assert;
import org.junit.Test;

public class ValidationTest {
    @Test
    public void basicTest(){
        try {
            Hope.that("")
                    .isNotNullOrEmpty()
                    .matches("^(13|14|15|16|17|18|19)\\d{9}$");

        }catch (UncheckedValidationException e){
            Assert.assertEquals("value must not be empty",e.getMessage());
        }
    }
    @Test
    public void namedTest(){
        try {
            Hope.that("1528171").named("mobile")
                    .isNotNullOrEmpty()
                    .matches("^(13|14|15|16|17|18|19)\\d{9}$");

        }catch (UncheckedValidationException e){
            Assert.assertEquals("mobile must match at least one of the following regular expressions: \"^(13|14|15|16|17|18|19)\\d{9}$\"",e.getMessage());
        }
    }
    @Test
    public void predicateTest(){
        String warnTips = "密码长度为6～32位";
        try {
            Hope.that("fwesf").named("password")
                    .isNotNullOrEmpty()
                    .isTrue(n -> n.length() > 6 && n.length() <= 32,warnTips);
        }catch (UncheckedValidationException e){
            Assert.assertEquals(warnTips,e.getMessage());
        }

    }
}
