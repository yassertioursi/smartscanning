package com.ois.scanning;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity {


	private AppParamsFragment mAppParams;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);


		final Bundle args = getIntent().getExtras();


		if (savedInstanceState == null) {
			mAppParams = new AppParamsFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.settings_app_params_holder,
					mAppParams).commit();
			mAppParams.setArguments(args);
		} else {
			mAppParams = (AppParamsFragment) getSupportFragmentManager().findFragmentById(
					R.id.settings_app_params_holder);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		} else if (id == R.id.action_apply) {

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = preferences.edit();


			boolean succeeded = true;

			// Save general params
			if (succeeded && mAppParams != null) {
				succeeded = mAppParams.save(editor);
			}

			editor.apply();

			if (succeeded) {
				setResult(RESULT_OK);
				finish();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public static void initCheckBox(CheckBox checkBox, boolean value) {
		if (checkBox.isChecked() == value) {
			checkBox.setChecked(!value);
			checkBox.toggle();
		} else {
			checkBox.setChecked(value);
		}
	}
}
