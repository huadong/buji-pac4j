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
package io.buji.pac4j.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.CachingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsFullyAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.IsRememberedAuthorizer;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.buji.pac4j.token.Pac4jToken;

/**
 * Helper for Shiro.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class ShiroHelper {

    private final static Authorizer<CommonProfile> IS_REMEMBERED_AUTHORIZER = new IsRememberedAuthorizer<>();

    private final static Authorizer<CommonProfile> IS_FULLY_AUTHENTICATED_AUTHORIZER = new IsFullyAuthenticatedAuthorizer<>();
    
    private final static Logger logger = LoggerFactory.getLogger(ShiroHelper.class);

    /**
     * Populate the authenticated user profiles in the Shiro subject.
     *
     * @param profiles the linked hashmap of profiles
     */
    public static void populateSubject(final LinkedHashMap<String, CommonProfile> profiles) {
        if (profiles != null && profiles.size() > 0) {
            final List<CommonProfile> listProfiles = ProfileHelper.flatIntoAProfileList(profiles);
            try {
                if (IS_FULLY_AUTHENTICATED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityUtils.getSubject().login(new Pac4jToken(profiles, false));
                } else if (IS_REMEMBERED_AUTHORIZER.isAuthorized(null, listProfiles)) {
                    SecurityUtils.getSubject().login(new Pac4jToken(profiles, true));
                }
                
	            // clear authorization cache
	    		org.apache.shiro.mgt.SecurityManager sm = SecurityUtils.getSecurityManager();
	    		if (sm instanceof RealmSecurityManager) {
	    			Collection<Realm>  realms= ((RealmSecurityManager)sm).getRealms();
	    			for(Realm rm :realms) {
		    			if(rm instanceof CachingRealm) {
		    				PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
		    				if (logger.isDebugEnabled()) {
		    					logger.debug(String.format("clear realm[%s] cache for: %s", 
		    							rm.getName(), profiles));
		    				}
		    				((CachingRealm)rm).onLogout(principals);
		    			}
	    			}
	    		}
            } catch (final HttpAction e) {
                throw new TechnicalException(e);
            }
        }
    }
}
