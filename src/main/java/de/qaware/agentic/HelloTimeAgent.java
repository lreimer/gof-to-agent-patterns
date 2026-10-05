package de.qaware.agentic;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.LlmResponse;
import com.google.adk.models.langchain4j.LangChain4j;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import dev.langchain4j.model.openai.OpenAiChatModel;
import io.reactivex.rxjava3.core.Maybe;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class HelloTimeAgent {

    private static final System.Logger LOGGER = System.getLogger(HelloTimeAgent.class.getName());
    private static final Pattern MUNICH = Pattern.compile("\\bMunich\\b", Pattern.CASE_INSENSITIVE);

    public static BaseAgent ROOT_AGENT = createRootAgent();

    private static BaseAgent createRootAgent() {
        return LlmAgent.builder()
                .name("HelloTimeAgent")
                .description("Tells the current time in a specified city")
                .instruction("""
                        You are a helpful assistant that tells the current time in a city.
                        Use the 'getCurrentTime' tool for this purpose.
                        """)

                .model("gemini-flash-latest")
                // .model(createOpenAiCompatibleModel("http://localhost:11434/v1", UUID.randomUUID().toString(), "llama3.1:8b"))

                .tools(FunctionTool.create(HelloTimeAgent.class, "getCurrentTime"))

                .beforeModelCallback((callbackContext, llmRequestBuilder) -> {
                    boolean isMunich = callbackContext.userContent().stream()
                            .flatMap(content -> content.parts().stream())
                            .flatMap(parts -> parts.stream())
                            .flatMap(part -> part.text().stream())
                            .anyMatch(text -> MUNICH.matcher(text).find());
                    if (isMunich) {
                        LOGGER.log(System.Logger.Level.INFO, "Execution cancelled for city: Munich");
                        return Maybe.just(LlmResponse.builder()
                                .content(Content.builder()
                                        .role("model")
                                        .parts(Part.fromText("Execution cancelled for Munich."))
                                        .build())
                                .build());
                    }
                    return Maybe.empty();
                })

                .afterToolCallback((invocationContext, tool, input, toolContext, response) -> {
                    LOGGER.log(System.Logger.Level.INFO, "Tool executed: {0}", tool.name());
                    return Maybe.empty();
                })
                
                .build();
    }

    @Schema(description = "Get the current time for a given city")
    public static Map<String, String> getCurrentTime(
            @Schema(name = "city", description = "Name of the city to get the time for") String city) {
        return Map.of(
                "city", city,
                "time", "The time is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".");
    }

    @SuppressWarnings("unused")
    private static BaseLlm createOpenAiCompatibleModel(String baseUrl, String apiKey, String modelName) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .build();

        return LangChain4j.builder()
                .chatModel(chatModel)
                .modelName(modelName)
                .build();
    }
}