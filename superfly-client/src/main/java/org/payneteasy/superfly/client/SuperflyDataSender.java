package org.payneteasy.superfly.client;

import java.util.List;

import org.payneteasy.superfly.client.exception.CollectionException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.payneteasy.superfly.api.ActionDescription;
import com.payneteasy.superfly.api.RoleDescription;
import com.payneteasy.superfly.api.SSOService;

/**
 * Class that, upon instantiation and initialization in a Spring Application
 * Context, automatically sends subsystem data to SSOService.
 * 
 * @author Roman Puchkovskiy
 */
public class SuperflyDataSender implements InitializingBean {
	
	private SSOService ssoService;
	private RoleDescriptionCollector roleDescriptionCollector;
	private ActionDescriptionCollector actionDescriptionCollector;
	private String subsystemIdentifier = null;

	@Required
	public void setSsoService(SSOService ssoService) {
		this.ssoService = ssoService;
	}

	@Required
	public void setRoleDescriptionCollector(
			RoleDescriptionCollector roleDescriptionCollector) {
		this.roleDescriptionCollector = roleDescriptionCollector;
	}

	@Required
	public void setActionDescriptionCollector(
			ActionDescriptionCollector actionDescriptionCollector) {
		this.actionDescriptionCollector = actionDescriptionCollector;
	}

	public String getSubsystemIdentifier() {
		return subsystemIdentifier;
	}

	public void setSubsystemIdentifier(String subsystemIdentifier) {
		this.subsystemIdentifier = subsystemIdentifier;
	}

	public void afterPropertiesSet() throws Exception {
		ssoService.sendSystemData(getSubsystemIdentifier(),
				obtainRoleDescriptions(), obtainActionDescriptions());
	}

	protected RoleDescription[] obtainRoleDescriptions() throws CollectionException {
		List<RoleDescription> roles = roleDescriptionCollector.collect();
		return roles.toArray(new RoleDescription[roles.size()]);
	}
	
	protected ActionDescription[] obtainActionDescriptions() throws CollectionException {
		List<ActionDescription> actions = actionDescriptionCollector.collect();
		return actions.toArray(new ActionDescription[actions.size()]);
	}

}