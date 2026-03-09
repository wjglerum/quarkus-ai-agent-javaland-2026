package org.acme.guardrails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.langchain4j.data.message.AiMessage.from;
import static dev.langchain4j.test.guardrail.GuardrailAssertions.assertThat;

class NoExternalLinksGuardrailTest {

    private final NoExternalLinksGuardrail rail = new NoExternalLinksGuardrail();

    @ParameterizedTest
    @ValueSource(strings = {
            "You can register at https://www.javaland.eu/registration",
            "Check the schedule at http://javaland.eu/schedule",
            "More info at https://meine.doag.org/tickets",
            "Visit https://europa-park.de for park details"
    })
    void repromptsWhenUrlPresent(String response) {
        assertThat(rail.validate(from(response))).hasFailures();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Lunch is served from 11:30 to 14:15 in the main hall.",
            "Venkat Subramaniam gives the keynote at 10:00 in the Dome.",
            "You can find more info at the Infocorner near the main entrance.",
            "Ask at the registration desk — they open at 7:30 on Tuesday."
    })
    void passesResponsesWithoutUrls(String response) {
        assertThat(rail.validate(from(response))).isSuccessful();
    }
}
