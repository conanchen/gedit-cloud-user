package com.github.conanchen.gedit.user.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Order(8)
@Configuration
public class JSONConfig{
    @Bean
    public List<HttpMessageConverter<?>> converters(List<HttpMessageConverter<?>> converters){
        GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
        // use millisecond as a json Serializer
        Gson gson = new GsonBuilder().setDateFormat(DateFormat.MILLISECOND_FIELD).create();
        msgConverter.setGson(gson);
        converters.add(msgConverter);
        log.info("json config initial success");
        return converters;
    }
}
