package com.vwo.sampleapp.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vwo.mobile.VWO;
import com.vwo.mobile.events.VWOStatusListener;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.fragments.FragmentOnBoardingMain;
import com.vwo.sampleapp.fragments.FragmentSortingMain;
import com.vwo.sampleapp.interfaces.NavigationToggleListener;
import com.vwo.sampleapp.utils.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationToggleListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initVWO(SharedPreferencesHelper.getApiKey(this));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.container_main, new FragmentSortingMain()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_layout_campaign) {
            // Handle the camera action
            getSupportFragmentManager().beginTransaction().replace(R.id.container_main, new FragmentSortingMain()).commit();
        } else if (id == R.id.nav_onboarding_campaign) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container_main, new FragmentOnBoardingMain()).commit();
        } else if (id == R.id.action_clear_data) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.popup_theme);
            builder.setTitle(getString(R.string.confirm));
            builder.setMessage(getString(R.string.clear_data_message));
            builder.setNegativeButton(R.string.clear_data_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton(R.string.clear_data_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferencesHelper.clearData(MainActivity.this);
                    dialogInterface.dismiss();
                    Toast.makeText(MainActivity.this, getString(R.string.data_cleared), Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        } else if (id == R.id.action_enter_api_key) {
            showApiKeyDialog();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
        if(SharedPreferencesHelper.getApiKey(this) != null) {
            currentKeyTextView.setText(getString(R.string.str_current_api_key, SharedPreferencesHelper.getApiKey(this)));
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
                    public void onClick(View v) {
                        String apiKey = input.getText().toString();
                        if (!TextUtils.isEmpty(apiKey) && apiKey.contains("-") && apiKey.length() > 32) {
                            Toast.makeText(MainActivity.this, getString(R.string.api_key_set), Toast.LENGTH_SHORT).show();
                            SharedPreferencesHelper.setApiKey(apiKey, MainActivity.this);
                            initVWO(apiKey);
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

    private void initVWO(String key) {
        if(key == null) {
            return;
        }
        VWO.with(this, key).launch();
    }

    @Override
    public void onToggle() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}
