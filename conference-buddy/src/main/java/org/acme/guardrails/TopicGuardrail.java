package org.acme.guardrails;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Ensures the conference buddy stays on-topic and cannot be abused
 * as a general-purpose AI assistant.
 */
@ApplicationScoped
public class TopicGuardrail implements InputGuardrail {

    private static final List<String> OFF_TOPIC_PATTERNS = List.of(
            "stock price",
            "write me a poem",
            "write a poem",
            "translate this",
            "translate the following",
            "write code for",
            "write a function",
            "sql query",
            "sql select",
            "drop table",
            "help me hack",
            "recipe for",
            "how to make a bomb",
            "write an essay",
            "homework help"
    );

    @Override
    public InputGuardrailResult validate(UserMessage um) {
        String lower = um.singleText().toLowerCase();
        for (String pattern : OFF_TOPIC_PATTERNS) {
            if (lower.contains(pattern)) {
                return fatal("I can only help with JavaLand 2026 conference questions — sessions, speakers, schedule, venue, and weather");
            }
        }
        return success();
    }
}
