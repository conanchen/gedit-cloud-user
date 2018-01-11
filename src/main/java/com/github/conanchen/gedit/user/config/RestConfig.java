package com.github.conanchen.gedit.user.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 分别设置同步异步连接池
 */
@Order(9)
@Configuration
public class RestConfig {
    @Bean
    @ConfigurationProperties(prefix = "httpclient.pool.sync")
    public CloseableHttpClient syncHttpClient(){
        return HttpClientBuilder.create().build();
    }
    @Bean
    @ConfigurationProperties(prefix = "httpclient.pool.async")
    public CloseableHttpAsyncClient asyncHttpClient(){
        return HttpAsyncClients.createDefault();
    }
    @Bean
    @Qualifier(value = "sync")
    @ConfigurationProperties(prefix = "httpclient.request.sync")
    public ClientHttpRequestFactory sycClientHttpRequestFactory(CloseableHttpClient httpClient){
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    @Qualifier(value = "async")
    @ConfigurationProperties(prefix = "httpclient.request.async")
    public HttpComponentsAsyncClientHttpRequestFactory asycClientHttpRequestFactory(CloseableHttpAsyncClient httpClient){
        return new HttpComponentsAsyncClientHttpRequestFactory(httpClient);
    }
    @Bean
    public RestTemplate restTemplate(@Qualifier(value = "sync")ClientHttpRequestFactory syncRequestFactory,
                                     RestTemplateBuilder builder,
                                     List<HttpMessageConverter<?>> converters) {
        return builder.additionalMessageConverters(converters).requestFactory(syncRequestFactory).build();
    }
    @Bean
    public AsyncRestTemplate asyncRestTemplate(@Qualifier(value = "async")HttpComponentsAsyncClientHttpRequestFactory asyncRequestFactory,
                                     List<HttpMessageConverter<?>> converters) {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(asyncRequestFactory);
        asyncRestTemplate.setMessageConverters(converters);
        return  asyncRestTemplate;
    }

}
