package com.vwo.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.vwo.mobile.VWO;
import com.vwo.mobile.VWOConfig;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.mobile.utils.VWOLog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@AppDeepLink("/sample/{id}")
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Main Activity";
    private static final String API_KEY = "api_key";
    //TODO: Add you VWO api key here
    private String vwoApiKey;
    private SharedPreferences sharedPreferences;
    private static int index = 0;

    private String[] goalList = {
            "landingPage",
            "pixel",
            "zte",
            "samsung",
            "iphone",
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten",
            "eleven",
            "twelve",
            "thirteen",
            "fourteen",
            "fifteen",
            "sixteen",
            "seventeen",
            "eighteen",
            "nineteen",
            "twenty",
            "twenty one",
            "twenty two",
            "twenty three",
            "twenty four",
            "twenty five",
            "twenty six",
            "twenty seven",
            "twenty eight",
            "twenty nine",
            "thirty",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "38",
            "39",
            "40",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "47",
            "48",
            "49",
            "50",
            "51",
            "52",
            "53",
            "54",
            "55",
            "56",
            "57",
            "58",
            "59",
            "60",
            "61",
            "62",
            "63",
            "64",
            "65",
            "66",
            "67",
            "68",
            "69",
            "70",
            "71",
            "72",
            "73",
            "74",
            "75",
            "76",
            "77",
            "78",
            "79",
            "80",
            "81",
            "82",
            "83",
            "84",
            "85",
            "86",
            "87",
            "88",
            "89",
            "90",
            "91",
            "92",
            "93",
            "94",
            "95",
            "96",
            "97",
            "98",
            "99",
            "100",
            "101",
            "102",
            "103",
            "104",
            "105",
            "106",
            "107",
            "108",
            "109",
            "110",
            "111",
            "112",
            "113",
            "114",
            "115",
            "116",
            "117",
            "118",
            "119",
            "120",
            "121",
            "122",
            "123",
            "124",
            "125",
            "126",
            "127",
            "128",
            "129",
            "130",
            "131",
            "132",
            "133",
            "134",
            "135",
            "136",
            "137",
            "138",
            "139",
            "140",
            "141",
            "142",
            "143",
            "144",
            "145",
            "146",
            "147",
            "148",
            "149",
            "150",
            "151",
            "152",
            "153",
            "154",
            "155",
            "156",
            "157",
            "158",
            "159",
            "160",
            "161",
            "162",
            "163",
            "164",
            "165",
            "166",
            "167",
            "168",
            "169",
            "170",
            "171",
            "172",
            "173",
            "174",
            "175",
            "176",
            "177",
            "178",
            "179",
            "180",
            "181",
            "182",
            "183",
            "184",
            "185",
            "186",
            "187",
            "188",
            "189",
            "190",
            "191",
            "192",
            "193",
            "194",
            "195",
            "196",
            "197",
            "198",
            "199",
            "200",
            "201",
            "202",
            "203",
            "204",
            "205",
            "206",
            "207",
            "208",
            "209",
            "210",
            "211",
            "212",
            "213"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        // Config for adding custom parameters before launch.
        Map<String, String> customKeys = new HashMap<>();
        VWOLog.setLogLevel(VWOLog.ALL);
        Log.d(LOG_TAG, "Goal list: \n" + TextUtils.join(", ", goalList));

        Intent intent = getIntent();
        if (intent.getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            Bundle parameters = intent.getExtras();
            vwoApiKey = parameters.getString("id");
            Log.d(LOG_TAG, "API_KEY: " + vwoApiKey);
            if(validateAndSetApiKey(vwoApiKey)) {

                Set<String> list = parameters.keySet();
                for (String key : list) {
                    if(key.startsWith("__vwo__")) {
                        customKeys.put(key.replace("__vwo__", ""), parameters.getString(key));
                        Log.d(LOG_TAG, String.format(Locale.ENGLISH, "KEY: %s, VALUE: %s", key.replace("__vwo__", ""), parameters.getString(key)));
                    }
                }
            }
        } else {
            vwoApiKey = sharedPreferences.getString(API_KEY, BuildConfig.VWO_API_KEY);
            customKeys.put("user_type", "paid");
        }

        initVWO(vwoApiKey, customKeys);
    }

    public void gotoNext(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.exp_text:
                for(String goal : goalList) {
                    VWO.markConversionForGoal(goal);
                }
                break;
            case R.id.exp_image:
                VWO.markConversionForGoal(goalList[index++]);
                break;
            case R.id.exp_var:
                startActivity(new Intent(getApplicationContext(), ExperimentVariable.class));
                VWO.markConversionForGoal("variable", 9.0);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showApiKeyDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.popup_theme);
        builder.setTitle(getString(R.string.title_api_key));

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.input_dialog, null, false);
        // Set up the input
        final TextInputLayout textInputLayout = viewInflated.findViewById(R.id.input_wrapper);
        final AppCompatEditText input = textInputLayout.findViewById(R.id.input);
        final AppCompatTextView currentKeyTextView = viewInflated.findViewById(R.id.current_key);
        if (sharedPreferences.getString(API_KEY, null) != null) {
            currentKeyTextView.setText(getString(R.string.str_current_api_key, sharedPreferences.getString(API_KEY, null)));
        } else {
            currentKeyTextView.setVisibility(View.GONE);
        }
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set up the buttons
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String apiKey = input.getText().toString().trim();

                        if (validateAndSetApiKey(apiKey)) {
                            initVWO(apiKey, null);
                            alertDialog.dismiss();
                        } else {
                            textInputLayout.setErrorEnabled(true);
                            textInputLayout.setError(getString(R.string.error_api_key));
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    @CheckResult
    private boolean validateAndSetApiKey(String apiKey) {
        String regex = "[\\w]{32}-[0-9]*";
        Pattern pattern = Pattern.compile(regex);

        if (!TextUtils.isEmpty(apiKey) && pattern.matcher(apiKey).matches()) {
            Toast.makeText(MainActivity.this, getString(R.string.api_key_set), Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putString(API_KEY, apiKey).apply();
            return true;
        }

        return false;
    }

    private void initVWO(final String key, @Nullable Map<String, String> keys) {
        Log.d("INIT", "Calling initVWO");
        if (!TextUtils.isEmpty(key)) {
            VWOLog.setLogLevel(VWOLog.ALL);
            VWOConfig.Builder vwoConfigBuilder = new VWOConfig.Builder();
            if(keys != null) {
                vwoConfigBuilder.setCustomSegmentationMapping(keys);
            }

            VWO.with(this, key).config(vwoConfigBuilder.build()).launch(new VWOStatusListener() {
                @Override
                public void onVWOLoaded() {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "VWO sdk initialised with key : " + key, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onVWOLoadFailure(String reason) {
                    Toast.makeText(MainActivity.this, "VWO sdk failed to initialize because : " + reason, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            showApiKeyDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
