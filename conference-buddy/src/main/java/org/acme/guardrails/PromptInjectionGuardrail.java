package org.acme.guardrails;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Detects common prompt injection attack patterns and blocks them immediately.
 * Prevents attackers from overriding the system prompt or extracting internal instructions.
 */
@ApplicationScoped
public class PromptInjectionGuardrail implements InputGuardrail {

    private static final List<String> PATTERNS = List.of(
            "ignore previous instructions",
            "ignore all previous",
            "disregard your instructions",
            "disregard all instructions",
            "you are now",
            "act as",
            "new persona",
            "reveal your system prompt",
            "show me your instructions",
            "what is your system prompt",
            "repeat the above",
            "output your initial prompt",
            "forget your instructions",
            "override your instructions"
    );

    @Override
    public InputGuardrailResult validate(UserMessage um) {
        String lower = um.singleText().toLowerCase();
        for (String pattern : PATTERNS) {
            if (lower.contains(pattern)) {
                return fatal("Prompt injection attempt detected");
            }
        }
        return success();
    }
}
