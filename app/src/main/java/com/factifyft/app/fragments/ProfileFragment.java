package com.factifyft.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.factifyft.app.R;
import com.factifyft.app.utils.AdManager;
import com.factifyft.app.utils.customToast;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private SharedPreferences sp;
    private NativeAd loadedNativeAd;
    private FrameLayout adContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sp = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

        setupProfileHeader(view);
        setupActionCards(view);
        
        adContainer = view.findViewById(R.id.profile_native_ad_container);
        loadAndShowNativeAd();

        return view;
    }

    private void setupProfileHeader(View view) {
        TextView tvUsername = view.findViewById(R.id.textUserName);
        EditText etUsername = view.findViewById(R.id.EditNameUser);
        View ivEdit = view.findViewById(R.id.imageNameEdit);
        View ivSave = view.findViewById(R.id.imageNameEditDone);

        tvUsername.setText(sp.getString("username", "Buddy"));

        ivEdit.setOnClickListener(v -> {
            etUsername.setText(tvUsername.getText().toString());
            toggleEditMode(true, tvUsername, etUsername, ivEdit, ivSave);
            
            etUsername.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etUsername, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        ivSave.setOnClickListener(v -> {
            String newName = etUsername.getText().toString().trim();
            if (newName.isEmpty()) {
                etUsername.setError("Enter name");
                return;
            }

            sp.edit().putString("username", newName).apply();
            tvUsername.setText(newName);
            toggleEditMode(false, tvUsername, etUsername, ivEdit, ivSave);
            
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
            }
        });
    }

    private void toggleEditMode(boolean editing, TextView tv, EditText et, View edit, View save) {
        tv.setVisibility(editing ? View.GONE : View.VISIBLE);
        edit.setVisibility(editing ? View.GONE : View.VISIBLE);
        et.setVisibility(editing ? View.VISIBLE : View.GONE);
        save.setVisibility(editing ? View.VISIBLE : View.GONE);
    }

    private void setupActionCards(View view) {
        MaterialCardView profileFav = view.findViewById(R.id.profileScreen_favorite);
        MaterialCardView moreApps = view.findViewById(R.id.moreAppsToTry);
        MaterialCardView privacyBtn = view.findViewById(R.id.appPrivacyBtn);
        MaterialCardView termsBtn = view.findViewById(R.id.appTermsBtn);

        profileFav.setOnClickListener(v -> {
            BottomNavigationView bnv = requireActivity().findViewById(R.id.bottom_navigation);
            if (bnv != null) bnv.setSelectedItemId(R.id.bottom_favorite);
        });

        moreApps.setOnClickListener(v -> openExternalUrl(getString(R.string.developer_apps_url)));
        privacyBtn.setOnClickListener(v -> openExternalUrl(getString(R.string.privacy_policy_url)));
        termsBtn.setOnClickListener(v -> openExternalUrl(getString(R.string.terms_url)));
    }

    private void openExternalUrl(String url) {
        if (!isInternetAvailable()) {
            customToast.showToast(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            customToast.showToast(requireContext(), "Unable to open link", Toast.LENGTH_SHORT);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        Network network = cm.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        return caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    private void loadAndShowNativeAd() {
        // 1. Context safety check
        Context context = getContext();
        if (context == null) return;

        // 2. Use the new NativeAdCallback to handle Success and Failure
        AdManager.loadNativeAd(context, new AdManager.NativeAdCallback() {
            @Override
            public void onLoaded(NativeAd nativeAd) {
                // Check if fragment is still attached to UI before updating
                if (isAdded() && getView() != null) {
                    loadedNativeAd = nativeAd;
                    displayNativeAd(nativeAd);
                } else {
                    nativeAd.destroy();
                }
            }

            @Override
            public void onFailure() {
                // THE FIX: If ad fails (No Internet), hide the container globally
                if (isAdded() && adContainer != null) {
                    adContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayNativeAd(NativeAd nativeAd) {
        if (adContainer == null) return;

        // Inflate the layout
        NativeAdView adView = (NativeAdView) LayoutInflater.from(requireContext())
                .inflate(R.layout.native_ad_layout, adContainer, false);

        populateNativeAdView(nativeAd, adView);

        adContainer.removeAllViews();
        adContainer.addView(adView);

        // Now it's safe to show the container
        adContainer.setVisibility(View.VISIBLE);
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        if (adView.getHeadlineView() instanceof TextView tv) tv.setText(nativeAd.getHeadline());
        if (adView.getBodyView() instanceof TextView tv) tv.setText(nativeAd.getBody());
        if (adView.getCallToActionView() instanceof Button btn) btn.setText(nativeAd.getCallToAction());

        if (nativeAd.getIcon() != null && adView.getIconView() instanceof ImageView iv) {
            iv.setImageDrawable(nativeAd.getIcon().getDrawable());
            iv.setVisibility(View.VISIBLE);
        } else if (adView.getIconView() != null) {
            adView.getIconView().setVisibility(View.GONE);
        }

        if (nativeAd.getAdvertiser() != null && adView.getAdvertiserView() instanceof TextView tv) {
            tv.setText(nativeAd.getAdvertiser());
            tv.setVisibility(View.VISIBLE);
        } else if (adView.getAdvertiserView() != null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        }

        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadedNativeAd != null) {
            loadedNativeAd.destroy();
            loadedNativeAd = null;
        }
        adContainer = null;
    }
}
