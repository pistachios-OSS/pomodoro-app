package gis2018.udacity.tametu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import static gis2018.udacity.tametu.utils.Constants.RINGING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.Constants.TICKING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.convertToFloat;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatRingingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatTickingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.initializeSeekBar;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.maxVolume;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private SharedPreferences preferences;

    // 手动声明所有控件（原来用 ButterKnife 的）
    private Spinner workDurationSpinner;
    private Spinner shortBreakDurationSpinner;
    private Spinner longBreakDurationSpinner;
    private Spinner startlongbreakafterSpinner;
    private SeekBar tickingSeekBar;
    private SeekBar ringingSeekBar;
    private TextView aboutUsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 手动绑定控件
        workDurationSpinner = findViewById(R.id.work_duration_spinner);
        shortBreakDurationSpinner = findViewById(R.id.short_break_duration_spinner);
        longBreakDurationSpinner = findViewById(R.id.long_break_duration_spinner);
        startlongbreakafterSpinner = findViewById(R.id.start_long_break_after_spinner);
        tickingSeekBar = findViewById(R.id.ticking_seek_bar);
        ringingSeekBar = findViewById(R.id.ringing_seek_bar);
        aboutUsTextView = findViewById(R.id.about_us_text);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initSpinner();
        seekBarInitialization();

        // 这一行现在能正常运行了！
        aboutUsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void seekBarInitialization() {
        tickingSeekBar = initializeSeekBar(this, tickingSeekBar);
        ringingSeekBar = initializeSeekBar(this, ringingSeekBar);
        tickingSeekBar.setOnSeekBarChangeListener(this);
        ringingSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> workDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.work_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> shortBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.short_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> longBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.long_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> startlongbreakafterAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_long_break_after_array, R.layout.spinner_item);

        workDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        shortBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        longBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        startlongbreakafterAdapter.setDropDownViewResource(R.layout.spinner_item);

        workDurationSpinner.setAdapter(workDurationAdapter);
        shortBreakDurationSpinner.setAdapter(shortBreakDurationAdapter);
        longBreakDurationSpinner.setAdapter(longBreakDurationAdapter);
        startlongbreakafterSpinner.setAdapter(startlongbreakafterAdapter);

        // 恢复上次保存的选择
        workDurationSpinner.setSelection(preferences.getInt(getString(R.string.work_duration_key), 1));
        shortBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.short_break_duration_key), 1));
        longBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.long_break_duration_key), 1));
        startlongbreakafterSpinner.setSelection(preferences.getInt(getString(R.string.start_long_break_after_key), 2));

        workDurationSpinner.setOnItemSelectedListener(this);
        shortBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakDurationSpinner.setOnItemSelectedListener(this);
        startlongbreakafterSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor = preferences.edit();
        switch (parent.getId()) {
            case R.id.work_duration_spinner:
                editor.putInt(getString(R.string.work_duration_key), position);
                break;
            case R.id.short_break_duration_spinner:
                editor.putInt(getString(R.string.short_break_duration_key), position);
                break;
            case R.id.long_break_duration_spinner:
                editor.putInt(getString(R.string.long_break_duration_key), position);
                break;
            case R.id.start_long_break_after_spinner:
                editor.putInt(getString(R.string.start_long_break_after_key), position);
                break;
        }
        editor.apply();
    }

    @Override public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.ticking_seek_bar:
                preferences.edit().putInt(TICKING_VOLUME_LEVEL_KEY, progress).apply();
                floatTickingVolumeLevel = convertToFloat(preferences.getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
                break;
            case R.id.ringing_seek_bar:
                preferences.edit().putInt(RINGING_VOLUME_LEVEL_KEY, progress).apply();
                floatRingingVolumeLevel = convertToFloat(preferences.getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
                break;
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override public void onStopTrackingTouch(SeekBar seekBar) {}
}