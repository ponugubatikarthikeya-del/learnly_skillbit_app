package com.factifyft.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.factifyft.app.R;
import com.factifyft.app.activities.FactsDisplayActivity;
import com.factifyft.app.adapters.FactsCategoryAdapter;
import com.factifyft.app.data.FactCategory;
import com.factifyft.app.utils.AdManager;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class FactsFragment extends Fragment {

    private NativeAd loadedNativeAd;
    private FrameLayout nativeAdContainer;
    private View adCardWrapper;

    private NativeAd loadedNativeAdBottom;
    private FrameLayout loadedAdContainerBottom;
    private View adCardWrapperBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facts, container, false);

        setupHeader(view);
        setupRecyclerViews(view);
        
        adCardWrapper = view.findViewById(R.id.ad_card_wrapper);
        nativeAdContainer = view.findViewById(R.id.native_ad_container_1);

        adCardWrapperBottom = view.findViewById(R.id.ad_card_wrapper_bottom);
        loadedAdContainerBottom = view.findViewById(R.id.native_ad_container_bottom);

        loadAndShowNativeAd();
        loadAndShowNativeAdBottom();

        return view;
    }

    private void setupHeader(View view) {
        TextView tvUsername = view.findViewById(R.id.HelloNameView);
        SharedPreferences sp = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        String username = sp.getString("username", "Buddy");
        if (tvUsername != null) {
            tvUsername.setText(getString(R.string.welcome_user, username));
        }
    }

    private void setupRecyclerViews(View view) {
        RecyclerView recyclerInteresting = view.findViewById(R.id.recyclerInteresting);
        RecyclerView recyclerPsychology = view.findViewById(R.id.recyclerPsychology);
        RecyclerView recyclerUnknown = view.findViewById(R.id.recyclerUnKnown);
        RecyclerView recyclerHidden = view.findViewById(R.id.recyclerHidden);

        configureRecycler(recyclerInteresting, getInterestingFacts());
        configureRecycler(recyclerPsychology, getPsychologyFacts());
        configureRecycler(recyclerUnknown, getUnknownFacts());
        configureRecycler(recyclerHidden, getHiddenFacts());
    }

    private void configureRecycler(RecyclerView recyclerView, List<FactCategory> list) {
        if (recyclerView == null) return;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        
        FactsCategoryAdapter adapter = new FactsCategoryAdapter(list, category -> {
            Intent i = new Intent(requireContext(), FactsDisplayActivity.class);
            i.putExtra(getString(R.string.cat_category), category.getName());
            startActivity(i);
        });
        recyclerView.setAdapter(adapter);
    }


    // Use an Activity-based check and a safe context
    // Top Ad Loader
    private void loadAndShowNativeAd() {
        Context context = getContext();
        if (context == null) return;

        AdManager.loadNativeAd(context, new AdManager.NativeAdCallback() {
            @Override
            public void onLoaded(NativeAd nativeAd) {
                if (isAdded() && getView() != null) {
                    loadedNativeAd = nativeAd;
                    displayNativeAd(nativeAd);
                } else {
                    nativeAd.destroy();
                }
            }

            @Override
            public void onFailure() {
                // HIDE TOP AD on failure
                if (isAdded() && adCardWrapper != null) {
                    adCardWrapper.setVisibility(View.GONE);
                }
            }
        });
    }

    // Bottom Ad Loader
    private void loadAndShowNativeAdBottom() {
        Context context = getContext();
        if (context == null) return;

        AdManager.loadNativeAd(context, new AdManager.NativeAdCallback() {
            @Override
            public void onLoaded(NativeAd nativeAd) {
                if (isAdded() && getView() != null) {
                    loadedNativeAdBottom = nativeAd;
                    displayBottomNativeAd(nativeAd);
                } else {
                    nativeAd.destroy();
                }
            }

            @Override
            public void onFailure() {
                // HIDE BOTTOM AD on failure
                if (isAdded() && adCardWrapperBottom != null) {
                    adCardWrapperBottom.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayNativeAd(NativeAd nativeAd) {
        if (nativeAdContainer == null || adCardWrapper == null) return;

        NativeAdView adView = (NativeAdView) LayoutInflater.from(requireContext())
                .inflate(R.layout.native_ad_layout, nativeAdContainer, false);

        populateNativeAdView(nativeAd, adView);
        nativeAdContainer.removeAllViews();
        nativeAdContainer.addView(adView);

        // Show only when ready
        adCardWrapper.setVisibility(View.VISIBLE);
        nativeAdContainer.setVisibility(View.VISIBLE);
    }

    private void displayBottomNativeAd(NativeAd nativeAd) {
        if (loadedAdContainerBottom == null || adCardWrapperBottom == null) return;

        NativeAdView adView = (NativeAdView) LayoutInflater.from(requireContext())
                .inflate(R.layout.native_ad_layout, loadedAdContainerBottom, false);

        populateNativeAdView(nativeAd, adView);
        loadedAdContainerBottom.removeAllViews();
        loadedAdContainerBottom.addView(adView);

        // Show only when ready
        adCardWrapperBottom.setVisibility(View.VISIBLE);
        loadedAdContainerBottom.setVisibility(View.VISIBLE);
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

    private List<FactCategory> getInterestingFacts() {
        List<FactCategory> list = new ArrayList<>();
        list.add(new FactCategory(getString(R.string.cat_oceans), R.drawable.ocean_bg, getString(R.string.cat_interesting)));
        list.add(new FactCategory(getString(R.string.cat_space), R.drawable.space_bg, getString(R.string.cat_interesting)));
        list.add(new FactCategory(getString(R.string.cat_time_universe), R.drawable.time_bg, getString(R.string.cat_interesting)));
        list.add(new FactCategory(getString(R.string.cat_mind), R.drawable.mind_bg, getString(R.string.cat_interesting)));
        return list;
    }

    private List<FactCategory> getPsychologyFacts() {
        List<FactCategory> list = new ArrayList<>();
        list.add(new FactCategory(getString(R.string.cat_men), R.drawable.men_bg, getString(R.string.cat_psychology)));
        list.add(new FactCategory(getString(R.string.cat_women), R.drawable.women_bg, getString(R.string.cat_psychology)));
        list.add(new FactCategory(getString(R.string.cat_love), R.drawable.love_bg, getString(R.string.cat_psychology)));
        list.add(new FactCategory(getString(R.string.Human_behaviour), R.drawable.human_behaviour_img, getString(R.string.cat_psychology)));
        list.add(new FactCategory(getString(R.string.Human_Habits), R.drawable.habits_facts_img, getString(R.string.cat_psychology)));
        return list;
    }

    private List<FactCategory> getUnknownFacts() {
        List<FactCategory> list = new ArrayList<>();
        list.add(new FactCategory(getString(R.string.cat_nature_wonders), R.drawable.nature_bg, getString(R.string.cat_unknown)));
        list.add(new FactCategory(getString(R.string.cat_animals), R.drawable.animals_bg, getString(R.string.cat_unknown)));
        list.add(new FactCategory(getString(R.string.did_you_known), R.drawable.do_you_known_img, getString(R.string.cat_unknown)));
        return list;
    }

    private List<FactCategory> getHiddenFacts() {
        List<FactCategory> list = new ArrayList<>();
        list.add(new FactCategory(getString(R.string.world_hidden), R.drawable.hidden_talk_img, getString(R.string.cat_hidden_facts)));
        list.add(new FactCategory(getString(R.string.cat_mysteries), R.drawable.mystery_bg, getString(R.string.cat_hidden_facts)));
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadedNativeAd != null) {
            loadedNativeAd.destroy();
            loadedNativeAd = null;
        }

        if(loadedNativeAdBottom!=null){
            loadedNativeAdBottom.destroy();
            loadedNativeAdBottom = null;
        }

        nativeAdContainer = null;
        adCardWrapper = null;

        loadedAdContainerBottom = null;
        adCardWrapperBottom = null;

//        nativeContainer12 = null;
//        adCardWrapper12 = null;

    }
}
