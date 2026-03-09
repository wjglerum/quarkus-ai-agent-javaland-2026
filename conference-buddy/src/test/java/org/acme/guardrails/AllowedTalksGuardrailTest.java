package org.acme.guardrails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.langchain4j.data.message.AiMessage.from;
import static dev.langchain4j.test.guardrail.GuardrailAssertions.assertThat;

class AllowedTalksGuardrailTest {

    private AllowedTalksGuardrail rail(String allowedCsv) {
        var r = new AllowedTalksGuardrail();
        r.allowedCsv = allowedCsv;
        return r;
    }

    @Test
    void passesWhenApprovedSpeakerMentioned() {
        var res = rail("Venkat Subramaniam,Josh Long,Willem Jan Glerum")
                .validate(from("Don't miss the talk by Venkat Subramaniam at 10:00 in the Dome!"));
        assertThat(res).isSuccessful();
    }

    @Test
    void passesWhenApprovedVenueMentioned() {
        var res = rail("Blue Fire,Silver Star,Europa-Park")
                .validate(from("Their talk on Blue Fire wraps up at 14:00."));
        assertThat(res).isSuccessful();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Lunch is served from 11:30 to 14:15 in the main hall.",
            "The bathroom is on the left past the registration desk.",
            "Coffee is available all day at the venue.",
            "Wear comfortable shoes — you will be walking a lot!"
    })
    void passesGenericAnswersThatDontReferenceSpecificTalks(String response) {
        // Generic responses should pass even if they don't mention any approved name.
        // The guardrail only activates when the response appears to recommend a talk/speaker.
        var res = rail("Venkat Subramaniam,Josh Long")
                .validate(from(response));
        assertThat(res).isSuccessful();
    }

    @Test
    void repromptsWhenHallucinatedSpeakerRecommended() {
        var res = rail("Venkat Subramaniam,Josh Long")
                .validate(from("Don't miss the talk by Elon Musk on blockchain at 14:00 in the Dome!"));
        assertThat(res).hasFailures();
    }

    @Test
    void repromptsWhenHallucinatedTalkRecommended() {
        var res = rail("Venkat Subramaniam,Josh Long")
                .validate(from("I recommend checking out the session by Dr. Hallucination on quantum computing!"));
        assertThat(res).hasFailures();
    }
}
