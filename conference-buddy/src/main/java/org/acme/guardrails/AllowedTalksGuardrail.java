package org.acme.guardrails;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

/**
 * Prevents the LLM from hallucinating talks or speakers that are not on the
 * official JavaLand 2026 schedule.
 *
 * Only activates when the response appears to recommend a specific talk or speaker.
 * Generic answers (e.g. "the bathroom is on the left") pass through untouched.
 */
@ApplicationScoped
public class AllowedTalksGuardrail implements OutputGuardrail {

    @ConfigProperty(name = "guardrails.talks.allowed") // check application.properties for the list
    String allowedCsv;

    // Phrases that signal the LLM is referencing a specific talk or speaker
    private static final List<String> RECOMMENDATION_SIGNALS = List.of(
            "talk by", "session by", "presented by",
            "give a talk", "gives a talk", "giving a talk",
            "their talk", "his talk", "her talk",
            "recommend the talk", "check out the talk",
            "don't miss", "dont miss",
            "speaking about", "is speaking", "will speak"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage ai) {
        String text = ai.text();
        if (text == null || text.isBlank()) return success();

        String lower = text.toLowerCase();

        // Only validate responses that appear to reference specific talks/speakers
        boolean isRecommending = RECOMMENDATION_SIGNALS.stream().anyMatch(lower::contains);
        if (!isRecommending) return success();

        // At least one approved name must appear
        for (String raw : allowedCsv.split(",")) {
            String name = raw.trim();
            if (!name.isEmpty() && lower.contains(name.toLowerCase())) {
                return success();
            }
        }

        return reprompt("Response references talks or speakers not in the approved JavaLand 2026 schedule", null,
                "Only recommend talks and speakers from the official JavaLand 2026 schedule. " +
                "Do not mention any speakers or talks that were not provided to you.");
    }
}
