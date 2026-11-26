package interface_adapter.spellcheck;

import java.util.ArrayList;
import java.util.List;

/**
 * The state for the Spellcheck view model. Represents the data held by the spellcheck view.
 */
public class SpellcheckState {
    private List<String> suggestions = new ArrayList<>();
    private String errorMessage;

    /**
     * Constructs an empty spellcheck state.
     */
     public SpellcheckState() {
     }

    /**
     * Updates the list of spelling suggestions.
     * @param suggestions the new list of suggestions
     */
     public void setSuggestions(List<String> suggestions) {
     this.suggestions = suggestions;
     }

    /**
     * Returns the current spelling suggestions.
     * @return the current list of suggestions
     */
     public List<String> getSuggestions() {
     return this.suggestions;
     }

    /**
     * Updates the error message.
     * @param errorMessage the new error message, or null to clear
     */
     public void setErrorMessage(String errorMessage) {
     this.errorMessage = errorMessage;
     }

    /**
     * Returns the current error message, or null if there's no error.
     * @return the error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
