package com.mrglabs.labelcsv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

/**
 * Talks to a <b>small local</b> vision model through Ollama on this computer.
 *
 * <p><b>Where the AI runs:</b> inside the Ollama process on
 * {@code http://127.0.0.1:11434} (loopback only). Model weights stay on disk;
 * nothing is sent to OpenAI, Google, Azure, or any cloud OCR API.
 *
 * <p><b>Default model:</b> {@code moondream}
 * (~1.8B class / ~1.7&nbsp;GB download / typically 2–4&nbsp;GB RAM) — small
 * enough for a typical laptop.
 *
 * <p>We use Ollama's {@code /api/generate} endpoint with an {@code images}
 * array (that is what moondream expects). JSON is built with plain strings
 * so beginners can read every step — no extra Maven libraries.
 */
public class LocalVisionClient {

    /** Default Ollama HTTP address on the same machine. */
    public static final String DEFAULT_BASE_URL = "http://127.0.0.1:11434";

    /** Tiny vision model name as installed via {@code ollama pull moondream}. */
    public static final String DEFAULT_MODEL = "moondream";

    private final String baseUrl;
    private final String model;
    // Java 11+ built-in HTTP client — no Maven dependency needed
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** Use the default localhost URL and {@code moondream} model. */
    public LocalVisionClient() {
        this(DEFAULT_BASE_URL, DEFAULT_MODEL);
    }

    /**
     * @param baseUrl e.g. http://127.0.0.1:11434
     * @param model   e.g. moondream
     */
    public LocalVisionClient(String baseUrl, String model) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.model = model;
    }

    /** True when Ollama answers on this machine. */
    public boolean isReachable() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/tags"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ask the local model to read a label image file.
     *
     * @param imageFile PNG/JPEG on disk
     * @param size      SMALL or LARGE (changes the prompt)
     * @return model text, or empty if Ollama failed
     */
    public Optional<String> readLabel(java.io.File imageFile, LabelSize size) {
        try {
            // 1) Read the image bytes from disk
            byte[] bytes = Files.readAllBytes(imageFile.toPath());
            // 2) Base64 = text-safe encoding so JSON can carry the image
            String b64 = Base64.getEncoder().encodeToString(bytes);
            // 3) Build JSON for /api/generate (moondream-friendly)
            String prompt = jsonEscape(buildPrompt(size));
            String json = """
                    {
                      "model": "%s",
                      "prompt": "%s",
                      "images": ["%s"],
                      "stream": false,
                      "options": { "temperature": 0, "num_predict": 180 }
                    }
                    """.formatted(jsonEscape(model), prompt, b64);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/generate"))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return Optional.empty();
            }
            // Generate replies use "response":"...." (not chat "message.content")
            String content = extractJsonStringField(res.body(), "response");
            if (content == null || content.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(content.trim());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** Short status string for the UI banner. */
    public String describe() {
        return "ollama:" + model + "@" + baseUrl.replace("http://", "");
    }

    public String getModel() {
        return model;
    }

    /**
     * Tiny models behave better with a short, fixed answer shape.
     * Newlines in the prompt become \\n inside JSON via {@link #jsonEscape}.
     */
    private static String buildPrompt(LabelSize size) {
        if (size == LabelSize.LARGE) {
            return """
                    Read the LARGE sample tube label. Fill every line (blank if missing):
                    BARCODE_OR_NUM:
                    DATE:
                    SN:
                    RN:
                    """;
        }
        return """
                Read the SMALL sample tube label. Fill every line (blank if missing):
                CIRCLED_NUM:
                GS_OR_NGS:
                DATE:
                EQUIP_ID:
                SAMPLE_POINT:
                REF_LUBE:
                """;
    }

    /** Make a string safe to drop inside JSON double-quotes. */
    private static String jsonEscape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    /**
     * Pull a JSON string field value without a full JSON library.
     * Example: extractJsonStringField(body, "response")
     */
    private static String extractJsonStringField(String body, String field) {
        String marker = "\"" + field + "\":\"";
        int idx = body.indexOf(marker);
        if (idx < 0) {
            return null;
        }
        int start = idx + marker.length();
        StringBuilder out = new StringBuilder();
        boolean escape = false;
        for (int i = start; i < body.length(); i++) {
            char c = body.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> out.append('\n');
                    case 't' -> out.append('\t');
                    case '"' -> out.append('"');
                    case '\\' -> out.append('\\');
                    default -> out.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
