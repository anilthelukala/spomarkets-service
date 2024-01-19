package com.spom.service.service;


import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.handler.logging.LogLevel;
import io.netty.resolver.DefaultAddressResolverGroup;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import reactor.netty.http.client.HttpClient;

@Component
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public HttpClient createHttpClient() throws SSLException {
      HttpClient httpClient;
      // ConnectionProvider connectionProvider = ConnectionProvider.newConnection();
      httpClient = ((HttpClient)((HttpClient)HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)).wiretap("reactor.netty.http.client.HttpClient", LogLevel.TRACE, AdvancedByteBufFormat.TEXTUAL)).followRedirect(true);
      return httpClient;
   }
}
