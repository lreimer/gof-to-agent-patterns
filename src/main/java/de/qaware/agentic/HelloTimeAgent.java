package de.qaware.agentic;


import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.Annotations.Schema;
import com.google.adk.tools.FunctionTool;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class HelloTimeAgent {

    public static BaseAgent ROOT_AGENT = initAgent();

    private static BaseAgent initAgent() {
        return LlmAgent.builder()
                .name("HelloTimeAgent")
                .description("Tells the current time in a specified city")
                .instruction("""
                        You are a helpful assistant that tells the current time in a city.
                        Use the 'getCurrentTime' tool for this purpose.
                        """)
                .model("gemini-flash-latest")
                .tools(FunctionTool.create(HelloTimeAgent.class, "getCurrentTime"))
                .build();
    }

    @Schema(description = "Get the current time for a given city")
    public static Map<String, String> getCurrentTime(
            @Schema(name = "city", description = "Name of the city to get the time for") String city) {
        return Map.of(
                "city", city,
                "time", "The time is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".");
    }
}