package com.android.msahakyan.nestedrecycler.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by msahakyan on 08/12/15.
 *
 * Content provider for recent query suggestions
 */
public class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.android.msahakyan.nestedrecycler.provider.SearchSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
