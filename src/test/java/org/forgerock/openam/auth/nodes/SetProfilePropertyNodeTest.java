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
 * Copyright 2019 ForgeRock AS.
 */
package org.forgerock.openam.auth.nodes;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.forgerock.json.JsonValue.field;
import static org.forgerock.json.JsonValue.json;
import static org.forgerock.json.JsonValue.object;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.REALM;
import static org.forgerock.openam.auth.node.api.SharedStateConstants.USERNAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.forgerock.openam.auth.node.api.ExternalRequestContext;
import org.forgerock.openam.auth.node.api.TreeContext;
import org.forgerock.openam.auth.nodes.SetProfilePropertyNode.Config;
import org.forgerock.openam.core.CoreWrapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.sun.identity.idm.AMIdentity;

public class SetProfilePropertyNodeTest {

    private static final String USER = "user.1";
    private static final String REALM_NAME = "hello-world";
    @Mock
    private Config config;
    @Mock
    private CoreWrapper coreWrapper;
    @Mock
    private AMIdentity identity;
    @Mock
    private SetProfilePropertyNode node;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        node = new SetProfilePropertyNode(config, coreWrapper);
    }

    @Test
    public void shouldUpdateUserOnlyOnceWhenSettingMultipleAttributes() throws Exception {
        given(config.properties()).willReturn(ImmutableMap.of("hello", "\"world\"", "foo", "\"bar\""));
        given(coreWrapper.getIdentity(USER, REALM_NAME)).willReturn(identity);

        node.process(setupTreeContext());

        verify(identity).store();
    }

    @Test
    public void shouldSetStaticValues() throws Exception {
        given(config.properties()).willReturn(ImmutableMap.of("hello", "\"world\""));
        given(coreWrapper.getIdentity(USER, REALM_NAME)).willReturn(identity);

        node.process(setupTreeContext());

        verify(identity).setAttributes(singletonMap("hello", singleton("world")));
        verify(identity).store();
    }

    private TreeContext setupTreeContext() {
        return new TreeContext(json(object(field(USERNAME, USER), field(REALM, REALM_NAME))),
                new ExternalRequestContext.Builder().build(), emptyList());
    }

}