package org.codice.ddf.admin.beta;

import static junit.framework.TestCase.fail;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class ServiceUtils {

    public static <T> void registerServices(Class<T> clazz, Set<T> instances) {
        instances.forEach(instance -> registerService(clazz, instance));
    }

    public static <T> void registerService(Class<T> clazz, T instance) {
        registerService(clazz, instance, new Hashtable<>());
    }

    public static <T> void registerService(Class<T> clazz, T instance, Dictionary props) {
        getBundleContext().registerService(clazz, instance, props);
    }

    public static <T> Set<T> getServices(Class<T> clazz) {
        try {
            return getBundleContext().getServiceReferences(clazz, null)
                    .stream()
                    .map(ref -> getBundleContext().getService(ref))
                    .collect(Collectors.toSet());

        } catch (InvalidSyntaxException e) {
            fail(e.getMessage());
        }

        return new HashSet<>();
    }

    public static <T> T getService(Class<T> clazz) {
        return getBundleContext().getService(getBundleContext().getServiceReference(clazz));
    }

    public static void updateService(Class clazz, Map<String, Object> newConfig) {

    }
    private static BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(ServiceUtils.class)
                .getBundleContext();
    }

    public static ConfigurationBuilder createConfig() {
        return new ConfigurationBuilder();
    }


    public static class ConfigurationBuilder {

        private Map<String, Object> config;

        public ConfigurationBuilder() {
            config = new HashMap<>();
        }

        public ConfigurationBuilder put(String key, Object val) {
            config.put(key, val);
            return this;
        }

        public ConfigurationBuilder put(String key, Object... vals) {
            config.put(key, Arrays.asList(vals));
            return this;
        }

        public Map<String, Object> create() {
            return config;
        }
    }
}
