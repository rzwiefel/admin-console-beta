package org.codice.ddf.admin.beta;

import static org.ops4j.pax.exam.CoreOptions.bootClasspathLibrary;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.vmOptions;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.debugConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.OptionalCompositeOption;
import org.apache.karaf.tooling.VerifyMojo;

public abstract class AbstractComponentTest {

    public static final MavenArtifactUrlReference BC_PROV = maven().groupId(
            "org.bouncycastle")
            .artifactId("bcprov-jdk15on")
            .version("1.55");

    public static final MavenArtifactUrlReference BC_MAIL = maven().groupId(
            "org.bouncycastle")
            .artifactId("bcmail-jdk15on")
            .version("1.55");

    public static final MavenArtifactUrlReference BC_KIX = maven().groupId(
            "org.bouncycastle")
            .artifactId("bcpkix-jdk15on")
            .version("1.55");

    public static final MavenArtifactUrlReference KARAF_DISTRO_URL = maven().groupId(
            "org.apache.karaf")
            .artifactId("apache-karaf")
            .version("4.1.1")
            .type("tar.gz");

    public abstract List<Option> bootFeatures();

    @Configuration
    public Option[] config() throws Exception {
        return Stream.of(distributionSettings(), bootFeatures(), configurableSettings())
                .flatMap(Collection::stream)
                .toArray(Option[]::new);
    }

    public List<Option> distributionSettings() {
        return Arrays.asList(debugConfiguration("50005", Boolean.getBoolean("isDebugEnabled")),
                karafDistributionConfiguration().frameworkUrl(KARAF_DISTRO_URL)
                        .unpackDirectory(new File("target/exam"))
                        .useDeployFolder(false),
                vmOptions("-Xms2048m", "-Xmx4048m"),
                cleanCaches(),
                bootClasspathLibrary(BC_PROV),
                bootClasspathLibrary(BC_MAIL),
                bootClasspathLibrary(BC_KIX));
    }


    public List<Option> configurableSettings() throws Exception {
        return Arrays.asList(keepRuntimeFolder(),
                logLevel(LogLevelOption.LogLevel.INFO),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                        "rmiRegistryPort",
                        "20001"),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg",
                        "rmiServerPort",
                        "20002"),
                localRepo(),
//                replaceConfigurationFile("etc/log4j2.config.xml",
//                        new File(getClass().getResource("/log4j2.config.xml")
//                                .toURI())),
                editConfigurationFilePut("etc/config.properties", "org.apache.aries.blueprint.synchronous", "false"),
                replaceConfigurationFile("etc/custom.properties",
                        new File(getClass().getResource("/ddf-common/etc/custom.properties")
                                .toURI())),
                    replaceConfigurationFile("bin/setenv",
                        new File(getClass().getResource("/ddf-common/bin/setenv")
                                .toURI())));
    }

    public Option localRepo() {
        // TODO: tbatie - 8/2/17 - FIX ME
//        String localRepo = System.getProperty("maven.repo.local", "");
//        // other stuff...
//        System.out.println("USING LOCAL REPO: " + localRepo);
//        return when(localRepo.length() > 0).useOptions(
//                systemProperty("org.ops4j.pax.url.mvn.localRepository").value(localRepo)
//        );

        return systemProperty("org.ops4j.pax.url.mvn.localRepository").value("/Users/tbatie1/.m2/repository");
    }
}
