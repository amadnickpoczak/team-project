package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit and integration tests for SpellcheckDataAccess.
 */
class SpellcheckDataAccessTest {

    @Test
    void testEmptyAndBlankTextReturnsEmptyList() throws Exception {
        SpellcheckDataAccess dao = new SpellcheckDataAccess("unused-key");
        assertTrue(dao.getSuggestions("").isEmpty());
        assertTrue(dao.getSuggestions("   \n\t  ").isEmpty());
    }

    @Test
    void testMissingApiKeyThrowsException() {
        SpellcheckDataAccess dao = new SpellcheckDataAccess("");
        assertThrows(RuntimeException.class, () -> dao.getSuggestions("helo world"));
    }

    @Test
    void testInvalidApiKeyThrowsException() {
        SpellcheckDataAccess dao = new SpellcheckDataAccess("invalid-api-key-12345");
        assertThrows(RuntimeException.class, () -> dao.getSuggestions("helo world"));
    }

    @Test
    void testValidSpellingReturnsEmptyList() throws Exception {
        String apiKey = System.getenv("API_NINJAS_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(),
        "Skipping live API test, since API_NINJAS_KEY not set");

        SpellcheckDataAccess dao = new SpellcheckDataAccess(apiKey);
        List<String> result = dao.getSuggestions("This sentence has no spelling errors.");
        assertTrue(result.isEmpty());
    }

    @Test
    void testMisspelledWordAndHistoryAccess() throws Exception {
        String apiKey = System.getenv("API_NINJAS_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(),
        "Skipping live API test, since API_NINJAS_KEY not set");

        SpellcheckDataAccess dao = new SpellcheckDataAccess(apiKey);
        List<String> firstCall = dao.getSuggestions("i am not kiddign");

        assertFalse(firstCall.isEmpty());
        assertTrue(firstCall.get(0).contains("kiddign"));
        assertTrue(firstCall.get(0).contains("kidding"));

        // Second call with identical text should return the same result via history, not API request
        List<String> secondCall = dao.getSuggestions("i am not kiddign");
        assertEquals(firstCall, secondCall);
    }

    @Test
    void testLongTextSplitsAcrossMultipleSegments() throws Exception {
        String apiKey = System.getenv("API_NINJAS_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(),
        "Skipping live API test: API_NINJAS_KEY not set");

        SpellcheckDataAccess dao = new SpellcheckDataAccess(apiKey);
        // Over 50 characters
        String longText = "With Pinions of Disdain The soul can farther fly Than any feather specified in Ornitholigy";
        List<String> result = dao.getSuggestions(longText);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(item -> item.contains("Ornithology")));
    }
}

