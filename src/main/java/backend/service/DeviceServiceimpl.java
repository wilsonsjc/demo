package backend.service;

import backend.service.request.DeviceDeactivationResponse;
import backend.service.request.DevicedeActivation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceServiceimpl implements  DeviceService{

    @Autowired
    ErrorResponseUtil errorResponseUtil;
    public String FIXED = "fixed";
    public int maxConnections=100;
    public int maxIdleTimeInSeconds=10;
    public int maxLifeTimeInSeconds=10;
    public int pendingAcquireTimeoutInSeconds=10;
    public int evictBackgroundInSeconds=10;
    public String bufferLimit="1000000";
    private String inventoryApiBaseUrl ="https://bla.bla.com/api/v1/inventory";

    @Override
    public DeviceDeactivationResponse devicedeactivationAction(DevicedeActivation request, String lastUserModificationBy, String deviceId, String userTenantId, String activationKey, String projectId) throws SSLException, JsonProcessingException {

        String response = bhnoinventoryWebClient().get().retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), errorResponseUtil::handleErrorResponse)
                .bodyToMono(String.class).block();
        return getDeviceDeactivationResponse(response);
    }

    public WebClient bhnoinventoryWebClient() throws SSLException {

        ConnectionProvider provider = ConnectionProvider.builder(FIXED).maxConnections(maxConnections)
                .maxIdleTime(Duration.ofSeconds(maxIdleTimeInSeconds))
                .maxLifeTime(Duration.ofSeconds(maxLifeTimeInSeconds))
                .pendingAcquireTimeout(Duration.ofSeconds(pendingAcquireTimeoutInSeconds))
                .evictInBackground(Duration.ofSeconds(evictBackgroundInSeconds)).build();

        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        final HttpClient httpClient = HttpClient.create(provider).secure(t -> t.sslContext(sslContext));
        return WebClient.builder().baseUrl(inventoryApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(Integer.parseInt(bufferLimit)))
                .build();
    }

    public DeviceDeactivationResponse getDeviceDeactivationResponse(String response) throws JsonProcessingException {
        DeviceDeactivationResponse deviceDeactivationResponse = new DeviceDeactivationResponse();
        Map<String, String> map = new ObjectMapper().readValue(response, new TypeReference<HashMap<String, String>>() {});
        deviceDeactivationResponse.setChangeId(map.get("changeId"));
        deviceDeactivationResponse.setSequenceId(map.get("sequenceId"));
        deviceDeactivationResponse.setTenantId(map.get("tenantId"));
        return deviceDeactivationResponse;
    }
}
