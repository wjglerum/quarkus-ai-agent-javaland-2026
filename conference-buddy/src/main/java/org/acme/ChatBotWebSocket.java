package org.acme;

import dev.langchain4j.guardrail.InputGuardrailException;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;

@Authenticated
@WebSocket(path = "/chat-bot")
public class ChatBotWebSocket {

    @Inject
    SecurityIdentity identity;

    private final ChatBot chatBot;

    public ChatBotWebSocket(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    @OnOpen
    public String onOpen() {
        return "Hi " + identity.getPrincipal().getName() + "! Welcome to the JavaLand 2026 Conference Buddy. Ask me about sessions, speakers, weather, or anything about the conference!";
    }

    @OnTextMessage
    public String onTextMessage(String message) {
        try {
            return chatBot.chat(message);
        } catch (InputGuardrailException e) {
            return "Your message was blocked: " + e.getMessage();
        }
    }
}
