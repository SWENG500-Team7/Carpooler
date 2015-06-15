package com.carpooler.dao;

import com.searchly.jestdroid.JestDroidClient;
import com.searchly.jestdroid.http.HttpDeleteWithEntity;
import com.searchly.jestdroid.http.HttpGetWithEntity;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBaseHC4;
import org.apache.http.client.methods.HttpHeadHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpPutHC4;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by raymond on 6/13/15.
 * Changed to use HttpEntityEnclosingRequestBaseHC4
 */
public class CustomJestDroidClient extends JestDroidClient {
    private final static Logger log = LoggerFactory.getLogger(CustomJestDroidClient.class);

    protected HttpUriRequest constructHttpMethod(String methodName, String url, String payload) {
        HttpUriRequest httpUriRequest = null;

        if (methodName.equalsIgnoreCase("POST")) {
            httpUriRequest = new HttpPostHC4(url);
            log.debug("POST method created based on client request");
        } else if (methodName.equalsIgnoreCase("PUT")) {
            httpUriRequest = new HttpPutHC4(url);
            log.debug("PUT method created based on client request");
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            httpUriRequest = new HttpDeleteWithEntity(url);
            log.debug("DELETE method created based on client request");
        } else if (methodName.equalsIgnoreCase("GET")) {
            httpUriRequest = new HttpGetWithEntity(url);
            log.debug("GET method created based on client request");
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            httpUriRequest = new HttpHeadHC4(url);
            log.debug("HEAD method created based on client request");
        }

        if (httpUriRequest != null && httpUriRequest instanceof HttpEntityEnclosingRequestBaseHC4 && payload != null) {
            EntityBuilder entityBuilder = EntityBuilder.create()
                    .setText(payload)
                    .setContentType(requestContentType);

            if (isRequestCompressionEnabled()) {
                entityBuilder.gzipCompress();
            }

            ((HttpEntityEnclosingRequestBaseHC4) httpUriRequest).setEntity(entityBuilder.build());
        }

        return httpUriRequest;
    }
}
