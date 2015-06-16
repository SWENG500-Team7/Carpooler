package com.carpooler.dao;

import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.DroidReapableConnectionManager;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;

import io.searchbox.client.JestClient;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.idle.IdleConnectionReaper;

/**
 * Created by raymond on 6/13/15.
 * Needed to return CustomJestDroidClient
 */
public class CustomJestClientFactory extends JestClientFactory{
    private DroidClientConfig droidClientConfig;
    final static Logger log = LoggerFactory.getLogger(JestClientFactory.class);
    @Override
    public JestClient getObject() {
        JestDroidClient client = new CustomJestDroidClient();


        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", droidClientConfig.getPlainSocketFactory())
                .register("https", droidClientConfig.getSslSocketFactory())
                .build();

        if (droidClientConfig != null) {
            log.debug("Creating HTTP client based on configuration");
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            client.setRequestCompressionEnabled(droidClientConfig.isRequestCompressionEnabled());
            client.setServers(droidClientConfig.getServerList());
            boolean isMultiThreaded = droidClientConfig.isMultiThreaded();
            if (isMultiThreaded) {
                PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

                Integer maxTotal = droidClientConfig.getMaxTotalConnection();
                if (maxTotal != null) {
                    cm.setMaxTotal(maxTotal);
                }

                Integer defaultMaxPerRoute = droidClientConfig.getDefaultMaxTotalConnectionPerRoute();
                if (defaultMaxPerRoute != null) {
                    cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
                }

                Map<HttpRoute, Integer> maxPerRoute = droidClientConfig.getMaxTotalConnectionPerRoute();
                for (Map.Entry<HttpRoute, Integer> entry : maxPerRoute.entrySet()) {
                    cm.setMaxPerRoute(entry.getKey(), entry.getValue());
                }
                httpClientBuilder.setConnectionManager(cm);
                log.debug("Multi Threaded http client created");

                // schedule idle connection reaping if configured
                if (droidClientConfig.getMaxConnectionIdleTime() > 0) {
                    log.info("Idle connection reaping enabled...");

                    IdleConnectionReaper reaper = new IdleConnectionReaper(droidClientConfig, new DroidReapableConnectionManager(cm));
                    client.setIdleConnectionReaper(reaper);
                    reaper.startAsync();
                    reaper.awaitRunning();
                }

            } else {
                log.debug("Default http client is created without multi threaded option");
                httpClientBuilder.setConnectionManager(new BasicHttpClientConnectionManager(registry));
            }

            httpClientBuilder
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(droidClientConfig.getConnTimeout())
                            .setSocketTimeout(droidClientConfig.getReadTimeout())
                            .build())
                    .setDefaultCredentialsProvider(droidClientConfig.getCredentialsProvider())
                    .setProxyAuthenticationStrategy(droidClientConfig.getProxyAuthenticationStrategy())
                    .setRoutePlanner(droidClientConfig.getHttpRoutePlanner());

            // set custom gson instance
            Gson gson = droidClientConfig.getGson();
            if (gson != null) {
                client.setGson(gson);
            }

            client.setHttpClient(httpClientBuilder.build());
            //set discovery (should be set after setting the httpClient on jestClient)
            if (droidClientConfig.isDiscoveryEnabled()) {
                log.info("Node Discovery Enabled...");
                NodeChecker nodeChecker = new NodeChecker(client, droidClientConfig);
                client.setNodeChecker(nodeChecker);
                nodeChecker.startAsync();
                nodeChecker.awaitRunning();
            } else {
                log.info("Node Discovery Disabled...");
            }
        } else {
            log.debug("There is no configuration to create http client. Going to create simple client with default values");
            client.setHttpClient(new DefaultHttpClient());
            LinkedHashSet<String> servers = new LinkedHashSet<String>();
            servers.add("http://localhost:9200");
            client.setServers(servers);
        }

        return client;
    }
    public void setDroidClientConfig(DroidClientConfig droidClientConfig) {
        this.droidClientConfig = droidClientConfig;
    }
}
