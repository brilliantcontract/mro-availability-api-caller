package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(bc.mro.mrosupply_com_api_caller.GenerateJsonResource.class);
        resources.add(bc.mro.mrosupply_com_api_caller.ProductAvailabilityAndPriceOnBackEndResource.class);
        resources.add(bc.mro.mrosupply_com_api_caller.ProductAvailabilityAndPriceOnFrontEndLoggedOutResource.class);
        resources.add(bc.mro.mrosupply_com_api_caller.ProductAvailabilityAndPriceOnFrontEndResource.class);
        resources.add(org.glassfish.jersey.jsonb.internal.JsonBindingProvider.class);
        resources.add(org.glassfish.jersey.server.wadl.internal.WadlResource.class);
    }

}
