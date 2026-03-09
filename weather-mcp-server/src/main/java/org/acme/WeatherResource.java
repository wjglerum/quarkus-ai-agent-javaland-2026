package org.acme;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@Authenticated
@Path("/weather")
public class WeatherResource {

    @RestClient
    WeatherClient weatherClient;

    @RestClient
    GeocodingClient geocodingClient;

    @GET
    @Path("/forecast")
    public String forecast(@RestQuery String city) {
        GeocodingResponse geocoding = geocodingClient.search(city, 1);
        List<GeocodingResponse.GeocodingResult> results = geocoding.results();
        if (results == null || results.isEmpty()) {
            throw new WebApplicationException("City not found: " + city, Response.Status.BAD_REQUEST);
        }
        GeocodingResponse.GeocodingResult location = results.getFirst();
        return weatherClient.forecast(
                String.valueOf(location.latitude()),
                String.valueOf(location.longitude()),
                "temperature_2m,wind_speed_10m,precipitation");
    }
}
