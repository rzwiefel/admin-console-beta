package org.codice.ddf.admin.comp.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.KarafFeaturesOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class AdminAppFeatures {

    public static final String GRAPHQL_FEATURE = "admin-beta-graphql";

    public static final String ADMIN_UTILS_FEATURE = "admin-beta-utils";

    public static final String ADMIN_WCPM_FEATURE = "admin-beta-wcpm";

    // TODO: tbatie - 7/31/17 - fix version
    public static final MavenArtifactUrlReference ADMIN_APP_FEATURE_URL = maven().groupId(
            "org.codice.ddf.admin.beta")
            .artifactId("admin-query-app")
            .type("xml")
            .classifier("features")
            .version("0.1.3-SNAPSHOT");

    private List<String> featuresToAdd;

    private AdminAppFeatures() {
        featuresToAdd = new ArrayList<>();
    }

    public static AdminAppFeatures addFeatures() {
        return new AdminAppFeatures();
    }

    public AdminAppFeatures graphQLFeature() {
        featuresToAdd.add(GRAPHQL_FEATURE);
        return this;
    }

    public AdminAppFeatures adminUtilsFeature() {
        featuresToAdd.add(ADMIN_UTILS_FEATURE);
        return this;
    }

    public AdminAppFeatures adminWcpmFeature() {
        featuresToAdd.add(ADMIN_WCPM_FEATURE);
        return this;
    }

    public KarafFeaturesOption build() {
        return KarafDistributionOption.features(ADMIN_APP_FEATURE_URL,
                featuresToAdd.toArray(new String[0]));
    }
}
