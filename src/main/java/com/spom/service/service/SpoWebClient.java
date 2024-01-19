package com.spom.service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class SpoWebClient {

    private final Logger log = LoggerFactory.getLogger(SpoWebClient.class);
    private final HttpClientUtil httpClientUtil;

    @Autowired
    public SpoWebClient(HttpClientUtil httpClientUtil) {
        this.httpClientUtil = httpClientUtil;
    }

    public Mono<String> post(String endpoint,String request, Map<String, String> headers) throws SSLException {
        WebClient webClient;
        webClient = createWebClient();
        return webClient
                .post()
                .uri(endpoint)
                .bodyValue(request)
                .headers(httpHeaders -> {
                    httpHeaders.setAccept(getAcceptedMediaTypes());
                    if (null != headers)
                        headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                })
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.error("HttpStatusCode = {}", clientResponse.statusCode());
                        log.error("HttpHeaders = {}", clientResponse.headers().asHttpHeaders());
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(String.class);
                });
    }

    public Mono<String> post(String endpoint,Object request, Map<String, String> headers) throws Exception {
        WebClient webClient;
        webClient = createWebClient();
        return webClient
                .post()
                .uri(endpoint)
                .bodyValue(request)
                .headers(httpHeaders -> {
                    httpHeaders.setAccept(getAcceptedMediaTypes());
                    if (null != headers)
                        headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                })
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.error("HttpStatusCode = {}", clientResponse.statusCode());
                        log.error("HttpHeaders = {}", clientResponse.headers().asHttpHeaders());
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(String.class);
                });
    }

    public Mono<String> get(String endpoint, Map<String, String> headers) throws SSLException {
    	WebClient webClient;
        webClient = createWebClient();
        return webClient
                .get()
                .uri(endpoint)
                .headers(httpHeaders -> {
                    httpHeaders.setAccept(getAcceptedMediaTypes());
                    if (null != headers)
                        headers.keySet().forEach(key -> httpHeaders.add(key, headers.get(key)));
                })
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().isError()) {
                        log.info("HttpStatusCode = {}", clientResponse.statusCode());
                        log.info("HttpHeaders = {}", clientResponse.headers().asHttpHeaders());
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                    return clientResponse.bodyToMono(String.class);
                });
    }


    private WebClient createWebClient() throws SSLException {
        HttpClient httpClient = this.httpClientUtil.createHttpClient();
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    private List<MediaType> getAcceptedMediaTypes() {
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        return mediaTypes;
    }
}
