package jp.ikisaki.www;

import android.content.*;
import android.content.SharedPreferences.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * Created by wangyouqi on 2015/06/15.
 */
public class SettingsActivity extends PreferenceActivity implements
        OnPreferenceClickListener, OnSharedPreferenceChangeListener {
    public SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mod default path
        //getPreferenceManager().setSharedPreferencesName("my_preferences");
        //getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);

        sharedpreferences = getSharedPreferences(getPreferenceManager().getSharedPreferencesName(), MODE_PRIVATE);
        try { //システムからディスプレイの情報を読む
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
                sharedpreferences.edit().putBoolean("MANUAL", false).commit();
                //skp.setProgress(111);
                //skp.onSetInitialValue(false, 11);
            else {
                sharedpreferences.edit().putBoolean("MANUAL", true).commit();
                sharedpreferences.edit().putInt("BRIGHTNESS",
                        Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addPreferencesFromResource(R.xml.preference_settings);
        if (Common.CHKFULL) this.getActionBar().hide();

        CheckBoxPreference ckp = (CheckBoxPreference)getPreferenceScreen().findPreference("MANUAL");
        ckp.setOnPreferenceClickListener(this);
        SeekBarPreference skp = (SeekBarPreference)getPreferenceScreen().findPreference("BRIGHTNESS");
        skp.setOnPreferenceChangeListener(onPreferenceChange);

        Preference preference = (Preference)getPreferenceScreen().findPreference("LEGAL");
        preference.setSummary("Version: " + Common.APPVER);

        if (!hasHeaders()) {
            Button button = new Button(this);
            button.setText(getResources().getString(R.string.close));
            button.setOnClickListener(onclick);
            setListFooter(button);
        } else {        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Common.CHKSETTING = true;

        setState();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Common.CHKSETTING = false;
        putState();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /* get preference */
        if (key.equals("START_NAME")) {
            setState();
        } else if (key.equals("RADIUS")) {
            Common.RADIUS = sharedpreferences.getInt(key, 50) * 10;
        } else if (key.equals("FULL")) {
            this.getActionBar().hide();
            Common.CHKFULL = sharedpreferences.getBoolean(key, false);
            if (!Common.CHKFULL) {
                this.getActionBar().show();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else if (Build.VERSION.SDK_INT >= 18) {
                getWindow().getDecorView().setSystemUiVisibility(0x00001000);
            } else if (Build.VERSION.SDK_INT >= 16) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else if (Build.VERSION.SDK_INT >= 14) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } else { // VER<14
                getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
            }
        } else
            System.out.println("logcat:runbuttonstart77777" + key);
    }

    private void setState() {
        //SharedPreferencesから出発地の設定を保存
        Preference preference = (Preference)getPreferenceScreen().findPreference("START_NAME");

        if (sharedpreferences.getInt("START_ID", -1) >= 0) {
            preference.setSummary(sharedpreferences.getString("START_NAME", getResources().getString(R.string.set_start_location)) );
        }
    }

    private void putState() {
        //システムの明るさ設置を書く
        int brightness;
        brightness = sharedpreferences.getInt("BRIGHTNESS", 40);
        if (brightness < 20) brightness = 20;

        try {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getContentResolver().notifyChange(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), null);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("MANUAL")) {
            if (sharedpreferences.getBoolean("MANUAL", false) == false)
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            else
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            return true;
        } else
            return false;
    }

    Preference.OnPreferenceChangeListener onPreferenceChange = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            int intBrightness = Integer.valueOf(newValue.toString()) + 5;
            //get the content resolver
            ContentResolver cResolver = getContentResolver();
            //get the current window
            Window window = getWindow();

            //set the system brightness using the brightness variable value
            //Settings.System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);

            //preview brightness changes at this window
            //get the current window attributes
            WindowManager.LayoutParams layoutpars = window.getAttributes();
            //set the brightness of this window
            layoutpars.screenBrightness = (float)intBrightness / 255;
            //apply attribute changes to this window
            window.setAttributes(layoutpars);

            return false;
        }
    };

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.activity_factory_settings, target);
    }
}
