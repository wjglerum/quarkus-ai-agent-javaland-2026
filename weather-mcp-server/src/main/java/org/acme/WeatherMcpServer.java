package org.acme;

import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

public class WeatherMcpServer {

    @RestClient
    WeatherClientInternal weatherClient;

    @Tool(name = "current_weather", description = "Get current weather forecast for a location by city name.")
    ToolResponse forecast(String city) {
        String forecast = weatherClient.forecast(city);
        return ToolResponse.success(new TextContent(forecast));
    }
}
