package org.codice.ddf.admin.beta;

import static junit.framework.TestCase.fail;

import java.util.HashSet;
import java.util.Hashtable;
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
        getBundleContext().registerService(clazz, instance, new Hashtable<>());
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

    public static BundleContext getBundleContext() {
        return FrameworkUtil.getBundle(ServiceUtils.class)
                .getBundleContext();
    }
}
