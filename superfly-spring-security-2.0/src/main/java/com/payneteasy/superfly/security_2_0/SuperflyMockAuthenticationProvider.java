package com.payneteasy.superfly.security_2_0;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;

import com.payneteasy.superfly.api.SSOAction;
import com.payneteasy.superfly.api.SSORole;
import com.payneteasy.superfly.api.SSOUser;
import com.payneteasy.superfly.security.mapbuilder.ActionsMapBuilder;
import com.payneteasy.superfly.security_2_0.authentication.SSOUserAndSelectedRoleAuthenticationToken;
import com.payneteasy.superfly.security_2_0.authentication.SSOUserTransportAuthenticationToken;
import com.payneteasy.superfly.security_2_0.authentication.UsernamePasswordAuthRequestInfoAuthenticationToken;

/**
 * Authentication provider which does not use Superfly server at all. It may
 * be useful for fast development.
 * 
 * @author Roman Puchkovskiy
 */
public class SuperflyMockAuthenticationProvider extends SuperflyAuthenticationProvider {

    private String username;
    private String password;
    private ActionsMapBuilder actionsMapBuilder;
    private Map<SSORole, SSOAction[]> cachedActionsMap = null;

    @Required
    public void setUsername(String username) {
        this.username = username;
    }

    @Required
    public void setPassword(String password) {
        this.password = password;
    }

    @Required
    public void setActionsMapBuilder(ActionsMapBuilder actionsMapBuilder) {
        this.actionsMapBuilder = actionsMapBuilder;
    }

    @Override
    protected SSOUser doAuthenticate(
            UsernamePasswordAuthRequestInfoAuthenticationToken authRequest,
            String username, String password) {
        boolean ok = this.username.equals(username) && this.password.equals(password);

        if (!ok) {
            throw new BadCredentialsException("Bad password");
        }

        SSOUser ssoUser;
        try {
            ssoUser = new SSOUser(username, buildActionsMap(username), buildPreferences());
        } catch (Exception e) {
            throw new AuthenticationServiceException("Cannot collect action descriptions", e);
        }
        return ssoUser;
    }

    protected Map<SSORole, SSOAction[]> buildActionsMap(String username) throws Exception {
        if (cachedActionsMap == null) {
            cachedActionsMap = actionsMapBuilder.build();
        }
        return cachedActionsMap;
    }

    protected Map<String, String> buildPreferences() {
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class authentication) {
        return UsernamePasswordAuthRequestInfoAuthenticationToken.class.isAssignableFrom(authentication)
                || SSOUserTransportAuthenticationToken.class.isAssignableFrom(authentication)
                || SSOUserAndSelectedRoleAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
