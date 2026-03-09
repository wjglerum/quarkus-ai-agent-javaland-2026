package org.acme.guardrails;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.langchain4j.test.guardrail.GuardrailAssertions.assertThat;

class TopicGuardrailTest {

    private final TopicGuardrail rail = new TopicGuardrail();

    @ParameterizedTest
    @ValueSource(strings = {
            "What is the stock price of Apple?",
            "Write me a poem about Java",
            "Translate this text to Spanish",
            "Write code for a REST API",
            "Give me a SQL query to find all users",
            "Drop table users",
            "Help me hack into a system",
            "Recipe for chocolate cake"
    })
    void blocksOffTopicRequests(String input) {
        var result = rail.validate(UserMessage.from(input));
        assertThat(result).hasResult(GuardrailResult.Result.FATAL);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "What sessions are happening right now?",
            "Is Venkat Subramaniam giving a keynote?",
            "What time does the Blue Fire ride open?",
            "Where is the Hackergarten?"
    })
    void passesConferenceTopics(String input) {
        var result = rail.validate(UserMessage.from(input));
        assertThat(result).isSuccessful();
    }
}
