/*
 * Licensed to the bujiio organization of the Shiro project under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.buji.pac4j.filter;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.http.adapter.J2ENopHttpActionAdapter;

import io.buji.pac4j.context.ShiroSessionStore;
import io.buji.pac4j.engine.ShiroCallbackLogic;

/**
 * <p>This filter finishes the login process for an indirect client, based on the {@link #callbackLogic}.</p>
 *
 * <p>The configuration can be provided via setter methods: {@link #setConfig(Config)} (security configuration),
 * {@link #setDefaultUrl(String)} (default url after login if none was requested) and {@link #setMultiProfile(Boolean)} (whether multiple profiles should be kept).</p>
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class CallbackFilter implements Filter {

    private CallbackLogic<Object, J2EContext> callbackLogic;

    private Config config;

    private String defaultUrl;

    private Boolean multiProfile;

    private Boolean saveInSession;
    
    private Boolean renewSession;
    
    private String client;
    

    private HttpActionAdapter<Object, J2EContext> httpActionAdapter;

    public CallbackFilter() {
        callbackLogic = new ShiroCallbackLogic<>();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {

        assertNotNull("callbackLogic", callbackLogic);
        assertNotNull("config", config);

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final SessionStore<J2EContext> sessionStore = config.getSessionStore();
        final J2EContext context = new J2EContext(request, response, sessionStore != null ? sessionStore : ShiroSessionStore.INSTANCE);
        final HttpActionAdapter<Object, J2EContext> adapter = httpActionAdapter != null ? httpActionAdapter : J2ENopHttpActionAdapter.INSTANCE;

        callbackLogic.perform(context, config, adapter, this.defaultUrl, this.saveInSession, this.multiProfile, this.renewSession, this.client);
    }

    @Override
    public void destroy() {}

    public CallbackLogic<Object, J2EContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(final CallbackLogic<Object, J2EContext> callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(final Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }
    
    public Boolean getRenewSession() {
		return renewSession;
	}
    
    public void setRenewSession(Boolean renewSession) {
		this.renewSession = renewSession;
	}
    
    public Boolean getSaveInSession() {
		return saveInSession;
	}
    
    public void setSaveInSession(Boolean saveInSession) {
		this.saveInSession = saveInSession;
	}

    public HttpActionAdapter<Object, J2EContext> getHttpActionAdapter() {
        return httpActionAdapter;
    }

    public void setHttpActionAdapter(final HttpActionAdapter<Object, J2EContext> httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }
    
    public String getClient() {
		return client;
	}
    
    public void setClient(String client) {
		this.client = client;
	}
}
