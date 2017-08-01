package org.codice.ddf.admin.beta.mocks;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;

public class MockConfiguratorSuite implements ConfiguratorSuite {

    private MockBundleActions bundleActions;
    private MockConfiguratorFactory configuratorFactory;
    private MockServiceActions serviceActions;
    private MockServiceReader serviceReader;

    public MockConfiguratorSuite() {
        this.configuratorFactory = new MockConfiguratorFactory();
        this.bundleActions = new MockBundleActions();
        this.serviceActions = new MockServiceActions();
        this.serviceReader = new MockServiceReader();
    }

    @Override
    public MockConfiguratorFactory getConfiguratorFactory() {
        return configuratorFactory;
    }

    @Override
    public MockBundleActions getBundleActions() {
        return bundleActions;
    }

    @Override
    public FeatureActions getFeatureActions() {
        throw new UnsupportedOperationException("MockedConfigurationSuite does not support getFeatureActions. Write me!");
    }

    @Override
    public ManagedServiceActions getManagedServiceActions() {
        throw new UnsupportedOperationException("MockedConfigurationSuite does not support getManagedServiceActions! Write me!");

    }

    @Override
    public PropertyActions getPropertyActions() {
        throw new UnsupportedOperationException("MockedConfigurationSuite does not support getPropertyActions! Write me!");
    }

    @Override
    public MockServiceActions getServiceActions() {
        return serviceActions;
    }

    @Override
    public MockServiceReader getServiceReader() {
        return serviceReader;
    }

}
