package com.vwo.sampleapp.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vwo.mobile.VWO;
import com.vwo.sampleapp.R;
import com.vwo.sampleapp.databinding.FragmentItemDetailsBinding;
import com.vwo.sampleapp.interfaces.ChangeFragment;
import com.vwo.sampleapp.models.Mobile;
import com.vwo.sampleapp.utils.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * Created by aman on 08/08/17.
 */

public class FragmentItemDetails extends Fragment {
    private static final String ARG_ITEM = "item";
    private static final String ARG_FRAGMENT_TYPE = "fragment_type";

    private Mobile mobile;
    private int fragmentType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentItemDetailsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_details, container, false);
        View view = binding.getRoot();
        AppCompatImageView closeButton = view.findViewById(R.id.button_close);
        AppCompatButton addToCart = view.findViewById(R.id.button_add_to_cart);
        AppCompatButton buyNow = view.findViewById(R.id.button_buy);
        addToCart.setOnClickListener(view1 -> VWO.trackConversion("AddToCart"));
        buyNow.setOnClickListener(view12 -> VWO.trackConversion("Bought", mobile.getPrice()));
        closeButton.setOnClickListener(view13 -> {
            if (getParentFragment() instanceof ChangeFragment) {
                ChangeFragment listener = (ChangeFragment) getParentFragment();
                Bundle bundle = new Bundle();
                listener.loadFragment(bundle, FragmentSortingMain.ID_LIST_VARIATION, FragmentSortingMain.TAG_VARIATION);
            }
        });
        if (savedInstanceState == null) {
            assert getArguments() != null;
            mobile = getArguments().getParcelable(ARG_ITEM);
            fragmentType = getArguments().getInt(ARG_FRAGMENT_TYPE);
        } else {
            mobile = savedInstanceState.getParcelable(ARG_ITEM);
            fragmentType = savedInstanceState.getInt(ARG_FRAGMENT_TYPE);
        }

        binding.setMobile(mobile);
        VWO.trackConversion(Constants.VWOKeys.GOAL_PRODUCT_VIEWED);
        VWO.trackConversion(Constants.VWOKeys.GOAL_PRODUCT_PURCHASED, mobile.getPrice());
        return view;
    }

    @BindingAdapter("app:src")
    public static void setImage(AppCompatImageView imageView, int resourceId) {
        Drawable drawable = ResourcesCompat.getDrawable(imageView.getResources(), resourceId, imageView.getContext().getTheme());
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("app:inStock")
    public static void setInStock(AppCompatTextView textView, boolean inStock) {
        if (inStock) {
            textView.setText(textView.getContext().getString(R.string.text_in_stock));
            textView.setTextColor(ResourcesCompat.getColor(textView.getContext().getResources(),
                    R.color.green, textView.getContext().getTheme()));
        } else {
            textView.setText(textView.getContext().getString(R.string.text_out_of_stock));
            textView.setTextColor(ResourcesCompat.getColor(textView.getContext().getResources(),
                    R.color.red, textView.getContext().getTheme()));
        }
    }


    @BindingAdapter("app:codAvailable")
    public static void setCodAvailable(AppCompatTextView textView, boolean inStock) {
        if (inStock) {
            textView.setText(textView.getContext().getString(R.string.cod_available));
        } else {
            textView.setText(textView.getContext().getString(R.string.cod_not_available));
        }
    }

    @BindingAdapter("app:variants")
    public static void setVariants(AppCompatTextView textView, String text) {
        textView.setText(textView.getContext().getString(R.string.txt_variant, text));
    }

    public static FragmentItemDetails getInstance(Mobile mobile, int fragmentType) {
        FragmentItemDetails fragmentItemDetails = new FragmentItemDetails();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ITEM, mobile);
        bundle.putInt(ARG_FRAGMENT_TYPE, fragmentType);
        fragmentItemDetails.setArguments(bundle);

        return fragmentItemDetails;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ITEM, mobile);
        outState.putInt(ARG_FRAGMENT_TYPE, fragmentType);
    }
}
