package interface_adapter.spellcheck;

import java.util.List;
import java.util.ArrayList;

/**
 * The view model for the spellcheck use case.
 * Holds the spelling suggestions and error state for the UI to observe.
 */
public class SpellcheckViewModel {
    private final List<SpellcheckViewModelListener> listeners = new ArrayList<>();
    private SpellcheckState state = new SpellcheckState();

    /**
     * Returns the current spellcheck state.
     * @return the state
     */
    public SpellcheckState getState() {
        return this.state;
    }

    /**
     * Updates the spellcheck state.
     * @param state the new state
     */
    public void setState(SpellcheckState state) {
        this.state = state;
    }

    /**
     * Adds a new spellcheck view model listener.
     * @param listener the listener object to be added
     */
    public void addListener(SpellcheckViewModelListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Notifies each listener of a change in state.
     */
    public void notifyStateChanged() {
        for (SpellcheckViewModelListener listener : listeners) {
            listener.stateChanged();
        }
    }
}
