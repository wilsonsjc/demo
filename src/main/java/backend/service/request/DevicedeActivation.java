package backend.service.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DevicedeActivation {

	private String actionType;
	private String actionState;
	private String activationKey;
	private String projectId;
	private String targetProjectType;
	private boolean gatewayNotificationEnabled;
	private String userTenantId;

        public String getUserTenantId() {
			return userTenantId;
		}
		public void setUserTenantId(String userTenantId) {
			this.userTenantId = userTenantId;
		}
		public String getActionType() {
			return actionType;
		}
		public void setActionType(String actionType) {
			this.actionType = actionType;
		}
		public String getActionState() {
			return actionState;
		}
		public void setActionState(String actionState) {
			this.actionState = actionState;
		}
		public String getActivationKey() {
			return activationKey;
		}
		public void setActivationKey(String activationKey) {
			this.activationKey = activationKey;
		}
		public String getProjectId() {
			return projectId;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		public String getTargetProjectType() {
			return targetProjectType;
		}
		public void setTargetProjectType(String targetProjectType) {
			this.targetProjectType = targetProjectType;
		}
		public boolean isGatewayNotificationEnabled() {
			return gatewayNotificationEnabled;
		}
		public void setGatewayNotificationEnabled(boolean gatewayNotificationEnabled) {
			this.gatewayNotificationEnabled = gatewayNotificationEnabled;
		}
    
}
