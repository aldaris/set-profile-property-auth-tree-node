/*
 * jon.knight@forgerock.com
 *
 * Sets user profile attributes 
 *
 */

/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2017-2019 ForgeRock AS.
 */
package org.forgerock.openam.auth.nodes;

import static java.util.Collections.singleton;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.openam.core.CoreWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.iplanet.sso.SSOException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;

/**
 * A node which contributes a configurable set of properties to be added to the user's session, if/when it is created.
 */
@Node.Metadata(outcomeProvider = SingleOutcomeNode.OutcomeProvider.class,
        configClass = SetProfilePropertyNode.Config.class)
public class SetProfilePropertyNode extends SingleOutcomeNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetProfilePropertyNode.class);
    private final CoreWrapper coreWrapper;
    static final String USER_PASSWORD = "userPassword";

    /**
     * Configuration for the node.
     */
    public interface Config {
        /**
         * A map of property name to value.
         * @return a map of properties.
         */
        @Attribute(order = 100)
        Map<String, String> properties();
    }

    private final Config config;

    /**
     * Constructs a new SetSessionPropertiesNode instance.
     * @param config Node configuration.
     */
    @Inject
    public SetProfilePropertyNode(@Assisted Config config, CoreWrapper coreWrapper) {
        this.config = config;
        this.coreWrapper = coreWrapper;
    }

    @Override
    public Action process(TreeContext context) throws NodeProcessException {
        String username = context.sharedState.get(USERNAME).asString();
        String realm = context.sharedState.get(REALM).asString();
        AMIdentity userIdentity = coreWrapper.getIdentity(username, realm);

        Map<String, Set<String>> attributes = new HashMap<>();
        for (Map.Entry<String, String> entry : config.properties().entrySet()) {
            String key = entry.getKey();
            String propertyValue = entry.getValue();

            String result = null;
            if (propertyValue.startsWith("\"")) {
                result = propertyValue.substring(1, propertyValue.length() - 1);
            } else if (context.sharedState.isDefined(propertyValue)) {
                result = context.sharedState.get(propertyValue).asString();
            }
            // Special case for password handling
            else if (key.equals(USER_PASSWORD)) {
                result = getUserPassword(context);
            }

            if (StringUtils.isNotEmpty(result)) {
                attributes.put(key, singleton(result));
            }
        }

        try {
            userIdentity.setAttributes(attributes);
            userIdentity.store();
        } catch (IdRepoException | SSOException ex) {
            LOGGER.error("Unable to update user {} in realm {} with attributes {}", username, realm, attributes, ex);
        }

        return goToNext().build();
    }


    private String getUserPassword(TreeContext context) throws NodeProcessException {
        return context.transientState.isDefined(PASSWORD)
                ? context.transientState.get(PASSWORD).asString():"reallyRandomPassword!1232312432423532";
    }
}
