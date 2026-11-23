package interface_adapter.spellcheck;

import use_case.spellcheck.SpellcheckInputBoundary;
import use_case.spellcheck.SpellcheckInputData;

/**
 * Controller for the spellcheck use case.
 */
public class SpellcheckController {
    private final SpellcheckInputBoundary spellcheckInteractor;

    /**
     * Constructs a SpellcheckController object.
     */
    public SpellcheckController(SpellcheckInputBoundary spellcheckInteractor) {
        this.spellcheckInteractor = spellcheckInteractor;
    }

    /**
     * Executes the spellcheck action.
     *
     * @param text the text to be checked
     */
    public void execute(String text) {
        SpellcheckInputData inputData = new SpellcheckInputData(text);
        this.spellcheckInteractor.execute(inputData);
    }
}
