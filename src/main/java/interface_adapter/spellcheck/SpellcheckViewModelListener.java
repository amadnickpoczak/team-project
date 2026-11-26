package interface_adapter.spellcheck;

/**
 * Listener for the spellcheck view model.
 */
public interface SpellcheckViewModelListener {

    /**
     * Called when the spellcheck state of the spellcheck view model is updated.
     */
    void stateChanged();
}
