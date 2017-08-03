/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.beta;

import static org.codice.ddf.admin.beta.ServiceUtils.getService;
import static org.codice.ddf.admin.beta.ServiceUtils.registerServices;
import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.WHITE_LIST_CONTEXT;
import static org.codice.ddf.admin.security.common.services.StsServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.osgi.framework.Constants.SERVICE_PID;
import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AppConfigurationEntry;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.karaf.jaas.config.JaasRealm;
import org.codice.ddf.admin.comp.test.AdminAppFeatures;
import org.codice.ddf.admin.comp.test.ComponentTestFeatures;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.codice.ddf.security.handler.api.AuthenticationHandler;
import org.codice.ddf.security.handler.api.HandlerResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class WebContextPolicyManagerIT extends AbstractComponentTest {

    public static final String GRAPHQL_ENDPOINT = "http://localhost:8080/admin/beta/graphql";

    public static final String VARIABLE_RESOURCE_PATH = "/query/wcpm/";

    public static final String VARIABLE_FILE_NAME = "RequestVariables.json";

    public static final String QUERY_RESOURCE_PATH = "/query/wcpm/query/";

    public static final String MUTATION_RESOURCE_PATH = "/query/wcpm/mutation/";

    public static final GraphQLHelper REQUEST_FACTORY = new GraphQLHelper(
            WebContextPolicyManagerIT.class,
            QUERY_RESOURCE_PATH,
            MUTATION_RESOURCE_PATH,
            VARIABLE_RESOURCE_PATH,
            GRAPHQL_ENDPOINT);

    public static final String TEST_AUTH_1 = "TEST_AUTH_1";

    public static final String TEST_AUTH_2 = "TEST_AUTH_2";

    public static final String TEST_AUTH_3 = "TEST_AUTH_3";

    public static final Set<AuthenticationHandler> MOCK_AUTH_TYPES =
            ImmutableSet.of(new MockAuthenticationHandler(TEST_AUTH_1),
                    new MockAuthenticationHandler(TEST_AUTH_2),
                    new MockAuthenticationHandler(TEST_AUTH_3));

    public static final String TEST_REALM_1 = "TEST_REALM_1";

    public static final String TEST_REALM_2 = "TEST_REALM_2";

    public static final String TEST_REALM_3 = "TEST_REALM_3";

    public static final Set<JaasRealm> MOCK_REALMS = ImmutableSet.of(new MockJaasRealm(
            TEST_REALM_1), new MockJaasRealm(TEST_REALM_2), new MockJaasRealm(TEST_REALM_3));

    public static final String TEST_CLAIM_1 = "TEST_CLAIM_1";

    public static final String TEST_CLAIM_2 = "TEST_CLAIM_2";

    public static final String TEST_CLAIM_3 = "TEST_CLAIM_3";

    public static final String[] MOCK_CLAIMS =
            new String[] {TEST_CLAIM_1, TEST_CLAIM_2, TEST_CLAIM_3};

    public static final Map<String, Object> MOCK_STS_CONFIGURATION = ImmutableMap.of(STS_CLAIMS_PROPS_KEY_CLAIMS, MOCK_CLAIMS);

    @Override
    public List<Option> bootFeatures() {
        return Arrays.asList(
//                ComponentTestFeatures.addFeatures()
//                        .thirdPartyFeature()
//                        .testDependenciesFeature()
//                        .configuratorFixFeature()
//                        .basicHandlerFeature()
//                        .securityHandlerApiFeature()
//                        .securityPolicyContextFeature()
//                        .build(),

                ComponentTestFeatures.addFeatures().all().build(),

                AdminAppFeatures.addFeatures()
                        .adminUtilsFeature()
                        .adminWcpmFeature()
                        .graphQLFeature()
                        .build());
    }

    @BeforeExam
    public static void beforeClass() {
        // TODO: tbatie - 7/31/17 - If we setup up the configurator before the schema generates, do we need to make these services?
        // TODO: tbatie - 7/31/17 - we could also force a schema update instead
        registerServices(AuthenticationHandler.class, MOCK_AUTH_TYPES);
        registerServices(JaasRealm.class, MOCK_REALMS);
        MockSts.register(MOCK_CLAIMS);
    }

    @Before
    public void setup() {
        REQUEST_FACTORY.waitForGraphQLSchema("GetWhiteListed.graphql");
    }

    // TODO: tbatie - 7/6/17 - Write test for ensuring:
    //  policies bin collapse
    /*
    @Test
    public void getAuthTypes() throws IOException {
        ExtractableResponse getResponse = sendGraphQlQuery("GetAuthTypes.graphql");
        System.out.println("\nResponse:\n" + getResponse.body().asString());
    }

    @Test
    public void getRealms() throws IOException {
        ExtractableResponse getResponse = sendGraphQlQuery("GetRealms.graphql");
        System.out.println("\nResponse:\n" + getResponse.body().asString());
        assertThat(getResponse.jsonPath()
                .get("data.wcpm.realms"), is(MOCK_REALMS));
    }

    @Test
    public void getPoliciesQuery() throws IOException {
        ExtractableResponse getResponse = sendGraphQlQuery("GetPolicies.graphql");
        System.out.println("\nResponse:\n" + getResponse.body().asString());
    }



    @Test
    public void savePolicies() throws IOException {
        ExtractableResponse getResponse = sendGraphQlMutation("SavePolicies.graphql");
        System.out.println("\nResponse:\n" + getResponse.body().asString());
    }*/


    @Test
    public void getWhiteListed() throws IOException {

        List<String> newWhitelistValues = ImmutableList.of("a", "b", "c");

        Map<String, Object> newConfig = ServiceUtils.createConfig()
                .put(WHITE_LIST_CONTEXT, newWhitelistValues)
                .create();

        ServiceUtils.getService(ServiceActions.class)
                .build(POLICY_MANAGER_PID, newConfig, true)
                .commit();

        List<String> retrievedWhiteList = REQUEST_FACTORY.createRequest()
                .usingQuery("GetWhiteListed.graphql")
                .usingVariables("RequestVariables.json")
                .send()
                .getResponse()
                .jsonPath()
                .get("data.wcpm.whitelisted");

        assertThat(retrievedWhiteList, is(newWhitelistValues));
    }

    @Test
    public void saveWhiteListed() throws IOException {

        List<String> expectedWhiteListValues = REQUEST_FACTORY.getVariableValue(VARIABLE_FILE_NAME,
                "whitelistContexts");

        REQUEST_FACTORY.createRequest()
                .usingMutation("SaveWhiteListed.graphql")
                .usingVariables("RequestVariables.json")
                .send()
                .getResponse()
                .jsonPath()
                .get("data");

        assertThat(getService(ServiceActions.class).read(POLICY_MANAGER_PID).get(WHITE_LIST_CONTEXT),
                is(expectedWhiteListValues));
    }

    public static class MockSts {

        private Dictionary<String, String> serviceProps;

        private Dictionary<String, Object> configProps;

        public MockSts(String[] stsClaims) {
            serviceProps = new Hashtable<>();
            serviceProps.put(SERVICE_PID, StsServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID);

            configProps = new Hashtable<>();
            configProps.put(STS_CLAIMS_PROPS_KEY_CLAIMS, stsClaims);
        }

        public Dictionary<String, String> getServiceProps() {
            return serviceProps;
        }

        public Dictionary<String, Object> getConfigProps() {
            return configProps;
        }

        public static void register(String[] stsClaims) {
            MockSts mock = new MockSts(stsClaims);
            ServiceUtils.registerService(MockSts.class,
                    new MockSts(stsClaims),
                    mock.getServiceProps());
            try {
                ServiceUtils.getService(ConfigurationAdmin.class)
                        .getConfiguration(StsServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID)
                        .update(mock.getConfigProps());

            } catch (IOException e) {
                fail("Failed to retrieve configuration for mock sts.");
            }

        }
    }

    public static class MockAuthenticationHandler implements AuthenticationHandler {

        private String authType;

        public MockAuthenticationHandler(String authType) {
            this.authType = authType;
        }

        @Override
        public String getAuthenticationType() {
            return authType;
        }

        @Override
        public HandlerResult getNormalizedToken(ServletRequest servletRequest,
                ServletResponse servletResponse, FilterChain filterChain, boolean b)
                throws ServletException {
            return null;
        }

        @Override
        public HandlerResult handleError(ServletRequest servletRequest,
                ServletResponse servletResponse, FilterChain filterChain) throws ServletException {
            return null;
        }
    }

    public static class MockJaasRealm implements JaasRealm {

        private String realm;

        public MockJaasRealm(String realm) {
            this.realm = realm;
        }

        @Override
        public String getName() {
            return realm;
        }

        @Override
        public int getRank() {
            return 0;
        }

        @Override
        public AppConfigurationEntry[] getEntries() {
            return new AppConfigurationEntry[0];
        }
    }
}
