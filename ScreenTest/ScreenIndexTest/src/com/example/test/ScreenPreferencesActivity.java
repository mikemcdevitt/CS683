package com.example.test;

import android.preference.PreferenceActivity;
import android.os.Bundle;

public class ScreenPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.screenoptions);
	}
}