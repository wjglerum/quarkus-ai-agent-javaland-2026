package org.acme.guardrails;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.regex.Pattern;

/**
 * Prevents the LLM from returning hallucinated URLs.
 * LLMs frequently invent plausible-looking links that are wrong or lead nowhere.
 * This guardrail reprompts the model to give the answer without any links.
 */
@ApplicationScoped
public class NoExternalLinksGuardrail implements OutputGuardrail {

    private static final Pattern URL_PATTERN =
            Pattern.compile("https?://[\\w\\-]+(\\.[\\w\\-]+)+[/\\w\\-.~:?#\\[\\]@!$&'()*+,;=%]*",
                    Pattern.CASE_INSENSITIVE);

    @Override
    public OutputGuardrailResult validate(AiMessage ai) {
        String text = ai.text();
        if (text == null) return success();

        if (URL_PATTERN.matcher(text).find()) {
            return reprompt("Response contains a URL which may be hallucinated", null,
                    "Do not include any URLs or hyperlinks in your response. " +
                    "Direct the user to ask at the Infocorner or check the official conference materials instead.");
        }
        return success();
    }
}
