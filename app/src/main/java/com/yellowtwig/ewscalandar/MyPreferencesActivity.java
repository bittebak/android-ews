package com.yellowtwig.ewscalandar;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by marcprive on 06-13-15.
 */
public class MyPreferencesActivity extends PreferenceActivity {

    public static final String USERNAME = "username";
    public static final String DOMAIN = "domain";
    public static final String PASSWORD = "password";
    public static final String URL = "URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
