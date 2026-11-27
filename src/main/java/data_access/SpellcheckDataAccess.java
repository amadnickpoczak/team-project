package data_access;

import use_case.spellcheck.SpellcheckDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access implementation for the spellcheck use case. Directly retrieves and reformats suggestions
 * from API Ninjas' Spell Check API. API requires a key and limits requests to 50 characters.
 */
public class SpellcheckDataAccess implements SpellcheckDataAccessInterface {
    private static final String API_URL = "https://api.api-ninjas.com/v1/spellcheck?text=";
    private static final int MAX_REQUEST_LENGTH = 50;
    private static final int HTTP_SUCCESS = 200;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String apiKey;
    private final Map<String, List<String>> history = new HashMap<>();

    /**
     * Constructs a spellcheck data access object using the API_NINJAS_KEY environment variable.
     */
    public SpellcheckDataAccess() {
        this(System.getenv("API_NINJAS_KEY"));
    }

    /**
     * Constructs a spellcheck data access object using an explicit API key.
     */
    public SpellcheckDataAccess(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<String> getSuggestions(String text) throws Exception {
        List<String> suggestions = new ArrayList<>();
        if (text.isBlank()) {
            return suggestions;
        }
        for (String seg : splitUp(text)) {
            if (this.history.containsKey(seg)) {
                suggestions.addAll(this.history.get(seg));
            } else {
                List<String> segSuggestions = apiRequestSuggestions(seg);
                this.history.put(seg, segSuggestions);
                suggestions.addAll(segSuggestions);
            }
        }
        return suggestions;
    }
    /**
     * Requests suggestions directly from Spell Check API.
     * @param seg the segment to be checked
     */
     public List<String> apiRequestSuggestions(String seg) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Missing API_NINJAS_KEY environment variable.");
        }
         try {
             String encodedSeg = URLEncoder.encode(seg, StandardCharsets.UTF_8);
             HttpRequest request = HttpRequest.newBuilder()
                     .uri(URI.create(API_URL + encodedSeg))
                     .header("X-Api-Key", apiKey)
                     .GET()
                     .build();
             HttpResponse<String> response = this.httpClient.send(
                     request, HttpResponse.BodyHandlers.ofString());
             if (response.statusCode() != HTTP_SUCCESS) {
                 throw new RuntimeException("Spellcheck API error (HTTP " + response.statusCode() + ")");
             }
             return parseResponse(seg, response.body());
         }
         catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Reaching Spellcheck API failed: " + ex.getMessage(), ex);
         }
     }

    /**
     * Parses JSON responses from Spell Check API and returns UI-friendly suggestions.
     * Sample API response: {"original": "i am not kiddign", "corrected": "i am not kidding", "corrections": [{
     * "word": "kiddign", "index": 3, "correction": "kidding", "candidates": ["kidding"]}]}
     * Sample parsed suggestions: ["kiddign -> kidding"]
     * @param response the JSON API response
     * @param originalSeg the original segment, required to restore case
     * @return the list of suggestions
     */
    private List<String> parseResponse(String originalSeg, String response) {
        List<String> results = new ArrayList<>();
        String[] entries = response.split("\\{\"word\":");

        // Start at index 1 to skip the opening "{\"corrections\": [" prefix
        for (int i = 1; i < entries.length; i++) {
            String entry = entries[i];

            // Extract the misspelled word between the first pair of quotes
            int firstQuote = entry.indexOf('"');
            int secondQuote = entry.indexOf('"', firstQuote + 1);
            String lowerWord = entry.substring(firstQuote + 1, secondQuote);

            // Extract every suggestion inside "candidates": [ ... ]
            int bracketStart = entry.indexOf('[');
            int bracketEnd = entry.indexOf(']');
            String rawCandidates = entry.substring(bracketStart + 1, bracketEnd)
                    .replace("\"", "")
                    .trim();

            String originalWord = findOriginalCaps(originalSeg, lowerWord);
            String casedCandidates = matchCaps(originalWord, rawCandidates);
            results.add(originalWord + " -> " + casedCandidates);
        }
        return results;
    }
    private String findOriginalCaps(String chunk, String lowerWord) {
        for (String token : chunk.split("\\s+")) {
            if (token.equalsIgnoreCase(lowerWord)) {
                return token;
            }
        }
        return lowerWord;
    }

    /**
     * Matches spelling suggestion capitalisation to original misspelled word capitalisation.
     * @param original the original word
     * @param candidates the spelling suggestion(s) for original
     * @return the re-cased suggestion(s)
     */
    private String matchCaps(String original, String candidates) {
        if (candidates.isEmpty() || original.isEmpty()) {
            return candidates;
        }
        if (original.equals(original.toUpperCase()) && original.length() > 1) {
            return candidates.toUpperCase();
        }
        if (Character.isUpperCase(original.charAt(0))) {
            String[] items = candidates.split(", ");
            for (int i = 0; i < items.length; i++) {
                items[i] = Character.toUpperCase(items[i].charAt(0)) + items[i].substring(1);
            }
            return String.join(", ", items);
        }
        return candidates;
    }

    /**
     * Cleans, then splits up text into requestable chunks of at most MAX_REQUEST_LENGTH.
     * @param text the text to split up
     * @return the list of segments
     */
    private List<String> splitUp(String text) {
        List<String> segs = new ArrayList<>();
        String cleanedText = text.replaceAll("[^a-zA-Z'\\s]", "");
        String[] words = cleanedText.trim().split("\\s+");
        StringBuilder currSeg = new StringBuilder();

        for (String word : words) {
            if (currSeg.isEmpty()) {
                currSeg.append(word);
            } else if (currSeg.length() + 1 + word.length() <= MAX_REQUEST_LENGTH) {
                currSeg.append(" ").append(word);
            } else {
                segs.add(currSeg.toString());
                currSeg = new StringBuilder(word);
            }
        }
        if (!currSeg.isEmpty()) {
            segs.add(currSeg.toString());
        }
        return segs;
    }
}