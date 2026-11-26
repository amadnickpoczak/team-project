package interface_adapter.spellcheck;

import use_case.spellcheck.SpellcheckOutputBoundary;
import use_case.spellcheck.SpellcheckOutputData;

/**
 * Presenter for the spellcheck use case.
 * Provides the spellcheck view model with results from the spellcheck interactor.
 */
public class SpellcheckPresenter implements SpellcheckOutputBoundary {
    private final SpellcheckViewModel viewModel;

    /**
     * Constructs a new spellcheck presenter.
     * @param viewModel the viewModel to communicate with
     */
    public SpellcheckPresenter(SpellcheckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(SpellcheckOutputData outputData) {
        SpellcheckState state = this.viewModel.getState();
        state.setSuggestions(outputData.getSuggestions());
        state.setErrorMessage(null);
        this.viewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        SpellcheckState state = this.viewModel.getState();
        state.setErrorMessage(errorMessage);
        this.viewModel.notifyStateChanged();
    }
}
