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
package org.codice.ddf.admin.beta.mocks;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.codice.ddf.admin.configurator.ConfiguratorException;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class MockServiceReader implements ServiceReader {

    private MultiKeyMap mockServices;

    public MockServiceReader() {
        this.mockServices = new MultiKeyMap();
    }

    @Override
    public <S> S getServiceReference(Class<S> serviceClass) throws ConfiguratorException {
        throw new UnsupportedOperationException("MockServiceReader does not implement getServiceReference. Write me!");
    }

    @Override
    public <S> Set<S> getServices(Class<S> serviceClass, String filter) {
        return (Set<S>) mockServices.get(serviceClass, filter);
    }

    public <S> MockServiceReader addMockService(Class<S> requestedServiceClass, S mockService) {
        addMockService(requestedServiceClass, null, mockService);
        return this;
    }

    public <S> MockServiceReader addMockService(Class<S> requestedServiceClass, String filter, S mockService) {
        if(mockServices.containsKey(requestedServiceClass, filter)) {
            ((Set<S>)mockServices.get(requestedServiceClass, filter)).add(mockService);
        } else {
            Set<S> newSet = new HashSet<>();
            newSet.add(mockService);
            mockServices.put(requestedServiceClass, filter, newSet);
        }
        return this;
    }

    public <S> MockServiceReader addMockServices(Class<S> requestedServiceClass, Set<S> mockServices) {
        mockServices.forEach(s -> addMockService(requestedServiceClass, null, s));
        return this;
    }

    public <S> MockServiceReader addMockServices(Class<S> requestedServiceClass, String filter, Set<S> mockServices) {
        mockServices.forEach(s -> addMockService(requestedServiceClass, filter, s));
        return this;
    }
}