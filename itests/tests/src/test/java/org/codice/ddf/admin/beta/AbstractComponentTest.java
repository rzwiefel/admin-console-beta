package org.codice.ddf.admin.beta;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static junit.framework.TestCase.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public abstract class AbstractComponentTest {

    public static final MavenArtifactUrlReference KARAF_DISTRO_URL = maven().groupId(
            "org.apache.karaf")
            .artifactId("apache-karaf")
            .version("4.1.1")
            .type("tar.gz");

    public abstract List<Option> bootFeatures();

    @Configuration
    public Option[] config() {
        return Stream.of(distributionSettings(), bootFeatures(), configurableSettings())
                .flatMap(Collection::stream)
                .toArray(Option[]::new);
    }

    public List<Option> distributionSettings() {
        return Arrays.asList(debugConfiguration("50005", Boolean.getBoolean("isDebugEnabled")),
                karafDistributionConfiguration().frameworkUrl(KARAF_DISTRO_URL)
                        .unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false));
    }


    public List<Option> configurableSettings() {
        return Arrays.asList(keepRuntimeFolder(),
                logLevel(LogLevelOption.LogLevel.INFO),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                        "rmiRegistryPort",
                        "20001"),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                        "rmiServerPort",
                        "20002"));
    }
}
