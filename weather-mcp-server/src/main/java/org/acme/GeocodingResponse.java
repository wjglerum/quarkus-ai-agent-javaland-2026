package org.acme;

import java.util.List;

public record GeocodingResponse(List<GeocodingResult> results) {

    public record GeocodingResult(String name, double latitude, double longitude) {
    }
}
