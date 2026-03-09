package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/v1")
@RegisterRestClient(baseUri = "https://geocoding-api.open-meteo.com")
public interface GeocodingClient {

    @GET
    @Path("/search")
    GeocodingResponse search(@RestQuery String name, @RestQuery int count);
}
