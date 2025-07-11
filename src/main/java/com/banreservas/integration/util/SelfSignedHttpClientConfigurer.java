package com.banreservas.integration.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named(value = "selfSignedHttpClientConfigurer")
public class SelfSignedHttpClientConfigurer implements HttpClientConfigurer {
    /** the logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SelfSignedHttpClientConfigurer.class);

    @Override
    public void configureHttpClient(HttpClientBuilder clientBuilder) {

        try {
            LOG.info("Using SelfSignedHttpClientConfigurer...");

            final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509CertChain, authType) -> true).build();

            clientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager(RegistryBuilder
                        .<ConnectionSocketFactory> create()
                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                        .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                        .build()));

            LOG.info("... HttpClient configured!");

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }

    }
}

