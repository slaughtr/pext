package net.a40two.pext.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.a40two.pext.Constants;
import net.a40two.pext.R;
import net.a40two.pext.Settings;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner mExpireSpinner;
    Spinner mPrivacySpinner;
    Spinner mSyntaxSpinner;
    Spinner mTextSizeSpinner;
    Spinner mResultLimitSpinner;
    Button mSaveSettingsButton;
    Switch mLineNumberSwitch;
    Switch mWrapSwitch;
    Switch mFlingScrollSwitch;

    //ads
    private AdView mAdView;

    //for saving to shared prefs
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    //for saving to firebase
    private DatabaseReference mSettingsReference;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //initialize test ads
        MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-3940256099942544~3347511713");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //set all the spinners and adapters
        mExpireSpinner = (Spinner) this.findViewById(R.id.default_expiration_spinner);
        ArrayAdapter<CharSequence> expireAdapter = ArrayAdapter.createFromResource(this,
                R.array.expire_date_select, android.R.layout.simple_spinner_item);
        expireAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mExpireSpinner.setAdapter(expireAdapter);

        mPrivacySpinner = (Spinner) this.findViewById(R.id.default_privacy_spinner);
        ArrayAdapter<CharSequence> privacyAdapter;
        if (!Constants.LOGGED_IN) {
            //if not logged in, don't show "Private" option
            privacyAdapter = ArrayAdapter.createFromResource(this,
                    R.array.private_select_not_logged_in, android.R.layout.simple_spinner_item);
        } else {
            privacyAdapter = ArrayAdapter.createFromResource(this,
                    R.array.private_select, android.R.layout.simple_spinner_item);
        }
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPrivacySpinner.setAdapter(privacyAdapter);

        mSyntaxSpinner = (Spinner) this.findViewById(R.id.default_syntax_spinner);
        ArrayAdapter<CharSequence> syntaxAdapter = ArrayAdapter.createFromResource(this,
                R.array.syntax_highlight_select, android.R.layout.simple_spinner_item);
        syntaxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSyntaxSpinner.setAdapter(syntaxAdapter);

        mTextSizeSpinner = (Spinner) this.findViewById(R.id.text_size_spinner);
        ArrayAdapter<CharSequence> textSizeAdapter = ArrayAdapter.createFromResource(this, R.array.editor_text_size_select, android.R.layout.simple_spinner_dropdown_item);
        mTextSizeSpinner.setAdapter(textSizeAdapter);

        mResultLimitSpinner = (Spinner) this.findViewById(R.id.results_limit_spinner);
        ArrayAdapter<CharSequence> resultLimitAdapter = ArrayAdapter.createFromResource(this, R.array.result_limit_select, android.R.layout.simple_spinner_dropdown_item);
        mResultLimitSpinner.setAdapter(resultLimitAdapter);

        //set spinner click listeners
        mExpireSpinner.setOnItemSelectedListener(this);
        mPrivacySpinner.setOnItemSelectedListener(this);
        mSyntaxSpinner.setOnItemSelectedListener(this);
        mTextSizeSpinner.setOnItemSelectedListener(this);
        mResultLimitSpinner.setOnItemSelectedListener(this);

        //set spinners to values loaded from settings
        mExpireSpinner.setSelection(Settings.EXPIRE);
        mPrivacySpinner.setSelection(Settings.PRIVACY);
        mSyntaxSpinner.setSelection(Settings.SYNTAX);
        mTextSizeSpinner.setSelection(getIndex(mTextSizeSpinner, Settings.TEXT_SIZE));
        mResultLimitSpinner.setSelection(getIndex(mResultLimitSpinner, Settings.RESULT_LIMIT));

        //button
        mSaveSettingsButton = (Button) this.findViewById(R.id.save_settings_button);
        mSaveSettingsButton.setOnClickListener(this);

        //switches
        mLineNumberSwitch = (Switch) this.findViewById(R.id.show_line_numbers_switch);
        mLineNumberSwitch.setChecked(Settings.SHOW_LINE_NUMBERS);

        mWrapSwitch = (Switch) this.findViewById(R.id.wrap_switch);
        mWrapSwitch.setChecked(Settings.WORDWRAP);

        mFlingScrollSwitch = (Switch) this.findViewById(R.id.fling_to_scroll_switch);
        mFlingScrollSwitch.setChecked(Settings.FLING_TO_SCROLL);

        //no need to get firebase if not logged in
        if (Constants.LOGGED_IN) {
            mSettingsReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(Constants.USER_NAME)
                    .child(Constants.FIREBASE_CHILD_SETTINGS);
        }
    }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override public void onNothingSelected(AdapterView<?> parent) { }

    @Override public void onClick(View v) {
        if (v == mSaveSettingsButton) { saveSettings(); }
    }

    private void saveSettings() {
        //set values in Settings
        Settings.EXPIRE = mExpireSpinner.getSelectedItemPosition();
        Settings.PRIVACY = mPrivacySpinner.getSelectedItemPosition();
        Settings.SYNTAX = mSyntaxSpinner.getSelectedItemPosition();
        Settings.TEXT_SIZE = Integer.parseInt(mTextSizeSpinner.getSelectedItem().toString());
        Settings.RESULT_LIMIT = Integer.parseInt(mResultLimitSpinner.getSelectedItem().toString());
        Settings.SHOW_LINE_NUMBERS = mLineNumberSwitch.isChecked();
        Settings.WORDWRAP = mWrapSwitch.isChecked();
        Settings.FLING_TO_SCROLL = mFlingScrollSwitch.isChecked();

        //set values in shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(Constants.PREFERENCES_EXPIRATION_KEY, Settings.EXPIRE).apply();
        mEditor.putInt(Constants.PREFERENCES_PRIVACY_KEY, Settings.PRIVACY).apply();
        mEditor.putInt(Constants.PREFERENCES_SYNTAX_KEY, Settings.SYNTAX).apply();
        mEditor.putInt(Constants.PREFERENCES_TEXT_SIZE_KEY, Settings.TEXT_SIZE).apply();
        mEditor.putInt(Constants.PREFERENCES_RESULT_LIMIT_KEY, Settings.RESULT_LIMIT).apply();
        mEditor.putBoolean(Constants.PREFERENCES_LINE_NUMBER_KEY, Settings.SHOW_LINE_NUMBERS).apply();
        mEditor.putBoolean(Constants.PREFERENCES_WORDWRAP_KEY, Settings.WORDWRAP).apply();
        mEditor.putBoolean(Constants.PREFERENCE_FLING_SCROLL_KEY, Settings.FLING_TO_SCROLL).apply();

        //set values in firebase if logged in
        if (Constants.LOGGED_IN) {
            mSettingsReference.child("EXPIRE").setValue(Settings.EXPIRE);
            mSettingsReference.child("PRIVACY").setValue(Settings.PRIVACY);
            mSettingsReference.child("SYNTAX").setValue(Settings.SYNTAX);
            mSettingsReference.child("TEXT_SIZE").setValue(Settings.TEXT_SIZE);
            mSettingsReference.child("RESULT_LIMIT").setValue(Settings.RESULT_LIMIT);
            mSettingsReference.child("SHOW_LINE_NUMBERS").setValue(Settings.SHOW_LINE_NUMBERS);
            mSettingsReference.child("WORDWRAP").setValue(Settings.WORDWRAP);
            mSettingsReference.child("FLING_TO_SCROLL").setValue(Settings.FLING_TO_SCROLL);
        }
        //show a confirmation toast and exit
        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    //used to get position of spinner by provided value
    //useful for text size and result limit
    private int getIndex(Spinner spinner, int value){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (Integer.parseInt(spinner.getItemAtPosition(i).toString()) == value) {
                index = i;
            }
        }
        return index;
    }
}
