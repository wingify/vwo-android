package com.vwo.sampleapp.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.databinding.FragmentSuccessBinding;
import com.vwo.sampleapp.models.Success;
import com.vwo.sampleapp.utils.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

/**
 * Created by aman on 09/08/17.
 */

public class FragmentSuccess extends Fragment {
    public static final String ARG_SUCCESS = "success";

    private Success success;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSuccessBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false);

        View view = binding.getRoot();

        if (savedInstanceState == null) {
            assert getArguments() != null;
            success = getArguments().getParcelable(ARG_SUCCESS);
        } else {
            success = savedInstanceState.getParcelable(ARG_SUCCESS);
        }

        binding.setSuccess(success);
        VWO.trackConversion(Constants.VWOKeys.GOAL_LANDING_PAGE);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_SUCCESS, success);
    }

    @BindingAdapter("app:successImage")
    public static void setSuccessImage(AppCompatImageView imageView, int imageResource) {
        Drawable drawable = VectorDrawableCompat.create(imageView.getContext().getResources(),
                imageResource, imageView.getContext().getTheme());
        imageView.setImageDrawable(drawable);
    }

    public static FragmentSuccess getInstance(Success success) {
        FragmentSuccess fragmentSuccess = new FragmentSuccess();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SUCCESS, success);
        fragmentSuccess.setArguments(bundle);

        return fragmentSuccess;
    }
}
