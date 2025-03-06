package backend.service;


import backend.service.request.DevicedeActivation;
import backend.service.request.DeviceDeactivationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.net.ssl.SSLException;

public interface DeviceService {
	DeviceDeactivationResponse devicedeactivationAction(DevicedeActivation request, String lastUserModificationBy,
														String deviceId, String userTenantId, String activationKey, String projectId) throws SSLException, JsonProcessingException;

}
