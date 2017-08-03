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
package org.codice.ddf.admin.comp.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.KarafFeaturesOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class ComponentTestFeatures {
    public static final String THIRDPARTY_FEATURE = "thirdparty";

    public static final String COMMON_TEST_DEPENDENCIES_FEATURE = "common-test-dependencies";

//    public static final String MOCK_CONFIGURATOR_FEATURE = "mock-configurator";

    // TODO: tbatie - 7/31/17 - Fix in ddf
    public static final String SECURITY_POLICY_CONTEXT_FEATURE = "security-policy-context";

    public static final String SECURITY_HANDLER_API_FEATURE = "security-handler-api";

    public static final String CONFIGURAT_FIX_FEATURE = "configurator-fix";

    public static final String BASIC_HANDLER_FEATURE = "basic-handler";

    public static final String ALL = "all";

    // TODO: tbatie - 7/31/17 - fix version
    public static final MavenArtifactUrlReference COMPONENT_TEST_FEATURE = maven().groupId(
            "org.codice.ddf.admin.beta")
            .artifactId("commons")
            .version("0.1.3-SNAPSHOT")
            .type("xml")
            .classifier("features");

    private List<String> featuresToAdd;

    private ComponentTestFeatures() {
        featuresToAdd = new ArrayList<>();
    }

    public static ComponentTestFeatures addFeatures() {
        return new ComponentTestFeatures();
    }

    public ComponentTestFeatures thirdPartyFeature() {
        featuresToAdd.add(THIRDPARTY_FEATURE);
        return this;
    }

    public ComponentTestFeatures testDependenciesFeature() {
        featuresToAdd.add(COMMON_TEST_DEPENDENCIES_FEATURE);
        return this;
    }

    public ComponentTestFeatures securityPolicyContextFeature() {
        featuresToAdd.add(SECURITY_POLICY_CONTEXT_FEATURE);
        return this;
    }

    public ComponentTestFeatures securityHandlerApiFeature() {
        featuresToAdd.add(SECURITY_HANDLER_API_FEATURE);
        return this;
    }

    public ComponentTestFeatures configuratorFixFeature() {
        featuresToAdd.add(CONFIGURAT_FIX_FEATURE);
        return this;
    }

    public ComponentTestFeatures basicHandlerFeature() {
        featuresToAdd.add(BASIC_HANDLER_FEATURE);
        return this;
    }

    public ComponentTestFeatures all() {
        featuresToAdd.add(ALL);
        return this;
    }

    public KarafFeaturesOption build() {
        return KarafDistributionOption.features(COMPONENT_TEST_FEATURE,
                featuresToAdd.toArray(new String[0]));
    }
}
