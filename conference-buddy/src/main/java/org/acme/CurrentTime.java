package org.acme;

import java.time.LocalDateTime;

import jakarta.enterprise.context.ApplicationScoped;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class CurrentTime {

    @Tool("Get current time and date")
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}
