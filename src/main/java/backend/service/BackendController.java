package backend.service;


import backend.service.request.DevicedeActivation;
import backend.service.request.DeviceDeactivationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.net.ssl.SSLException;

public class BackendController {

    @Autowired
    DeviceService service;

    ObjectMapper objectMapper = new ObjectMapper();

    @CrossOrigin
    @PostMapping("/activate")
    public ResponseEntity<String> deactivateDevice(@RequestBody DevicedeActivation request,
                                                                         @RequestParam(value = "userTenantId", required = true) String userTenantId,
                                                                         @RequestParam(value = "activationKey", required = true) String activationKey,
                                                                         @RequestParam(value = "projectId", required = true) String projectId,
                                                                         @RequestParam(value = "lastUserModificationBy", required = true) String lastUserModificationBy,
                                                                         @RequestParam(value = "deviceId", required = true) String deviceId) throws JsonProcessingException, SSLException {

        DeviceDeactivationResponse response = service.devicedeactivationAction(request,
                lastUserModificationBy, deviceId, userTenantId, activationKey, projectId);
        if (response != null){
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(response));
        } else {
            return null;
        }
    }
}
