package com.vwo.sampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.models.Success;
import com.vwo.sampleapp.utils.Constants;

/**
 * Created by aman on 08/08/17.
 */

public class FragmentOnBoarding extends Fragment implements View.OnClickListener {
    private static final String ARG_PAGE_TYPE = "page_type";
    public static final String ARG_SUCCESS = "success";

    private int pageType;
    private AppCompatEditText email;
    private AppCompatEditText password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_boarding, container, false);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                pageType = getArguments().getInt(ARG_PAGE_TYPE, FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL);
            } else {
                pageType = FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL;
            }
        } else {
            pageType = savedInstanceState.getInt(ARG_PAGE_TYPE, FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL);
        }

        email = view.findViewById(R.id.edit_text_email);
        password = view.findViewById(R.id.edit_text_password);
        AppCompatButton login = view.findViewById(R.id.button_login);
        AppCompatButton facebookLogin = view.findViewById(R.id.button_facebook_login);
        AppCompatTextView textViewOr = view.findViewById(R.id.text_or);
        AppCompatButton skipLogin = view.findViewById(R.id.button_skip);


        switch (pageType) {
            case FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL:
                facebookLogin.setVisibility(View.GONE);
                textViewOr.setVisibility(View.GONE);
                skipLogin.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                break;
            case FragmentOnBoardingMain.VARIATION_LOGIN_TYPE_NORMAL:
                boolean socialMedia = (Boolean) VWO.getVariationForKey(Constants.VWOKeys.KEY_SOCIAL_MEDIA, false);
                boolean skip = (Boolean) VWO.getVariationForKey(Constants.VWOKeys.KEY_SKIP, false);
                if (socialMedia) {
                    textViewOr.setVisibility(View.VISIBLE);
                    facebookLogin.setVisibility(View.VISIBLE);
                } else {
                    textViewOr.setVisibility(View.GONE);
                    facebookLogin.setVisibility(View.GONE);
                }

                if (skip) {
                    skipLogin.setVisibility(View.VISIBLE);
                } else {
                    skipLogin.setVisibility(View.GONE);
                }

                login.setVisibility(View.VISIBLE);
                break;

            case FragmentOnBoardingMain.VARIATION_LOGIN_TYPE_SOCIAL:
                facebookLogin.setVisibility(View.VISIBLE);
                textViewOr.setVisibility(View.VISIBLE);
                skipLogin.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                break;

            case FragmentOnBoardingMain.VARIATION_LOGIN_TYPE_SKIP:
                facebookLogin.setVisibility(View.GONE);
                textViewOr.setVisibility(View.GONE);
                skipLogin.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                break;
            default:
                throw new IllegalArgumentException("Unknown page type: " + pageType);
        }

        facebookLogin.setOnClickListener(this);
        login.setOnClickListener(this);
        skipLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                if (!validateData()) {
                    Toast.makeText(getContext(), "Invalid Email or password", Toast.LENGTH_LONG).show();
                    break;
                }
            case R.id.button_facebook_login:
                Success success = new Success(getString(R.string.login_success), R.drawable.ic_done);
                if (getParentFragment() instanceof ChangeFragment) {
                    ChangeFragment changeFragment = (ChangeFragment) getParentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ARG_SUCCESS, success);
                    if (pageType == FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL) {
                        changeFragment.loadFragment(bundle, FragmentOnBoardingMain.CONTROL_LOGIN_SUCCESS, null);
                    } else {
                        changeFragment.loadFragment(bundle, FragmentOnBoardingMain.VARIATION_LOGIN_SUCCESS, null);
                    }
                }
                break;
            case R.id.button_skip:
                success = new Success(getString(R.string.login_skipped), R.drawable.ic_skip);
                if (getParentFragment() instanceof ChangeFragment) {
                    ChangeFragment changeFragment = (ChangeFragment) getParentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ARG_SUCCESS, success);
                    if (pageType == FragmentOnBoardingMain.CONTROL_LOGIN_TYPE_NORMAL) {
                        changeFragment.loadFragment(bundle, FragmentOnBoardingMain.CONTROL_LOGIN_SUCCESS, null);
                    } else {
                        changeFragment.loadFragment(bundle, FragmentOnBoardingMain.VARIATION_LOGIN_SUCCESS, null);
                    }
                }
                break;
        }
    }

    private boolean validateData() {
        return validateEmail(email.getText().toString()) && validatePassword(password.getText().toString());
    }

    private boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        return !TextUtils.isEmpty(password);
    }

    public static FragmentOnBoarding getInstance(@FragmentOnBoardingMain.LoginType int pageType) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_TYPE, pageType);

        FragmentOnBoarding fragmentOnBoarding = new FragmentOnBoarding();
        fragmentOnBoarding.setArguments(bundle);
        return fragmentOnBoarding;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PAGE_TYPE, pageType);
    }
}
