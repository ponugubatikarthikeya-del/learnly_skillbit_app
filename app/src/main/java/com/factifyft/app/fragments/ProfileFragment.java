package com.factifyft.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.factifyft.app.R;
import com.factifyft.app.utils.customToast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {
   private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);
// Load saved name
        sp = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);

       MaterialCardView profileFav=view.findViewById(R.id.profileScreen_favorite);
       MaterialCardView moreAppsToTry=view.findViewById(R.id.moreAppsToTry);
       MaterialCardView appPrivacyBtn=view.findViewById(R.id.appPrivacyBtn);
       MaterialCardView appTermsBtn=view.findViewById(R.id.appTermsBtn);

        TextView tvUsername =view.findViewById(R.id.textUserName);
        EditText etUsername = view.findViewById(R.id.EditNameUser);
        ImageView ivEdit = view.findViewById(R.id.imageNameEdit);
        ImageView ivSave = view.findViewById(R.id.imageNameEditDone);

// Load saved name
        tvUsername.setText(sp.getString("username", "Buddy"));

// ✏️ Edit clicked
        ivEdit.setOnClickListener(v -> {
            etUsername.setText(tvUsername.getText().toString());

            tvUsername.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);

            etUsername.setVisibility(View.VISIBLE);
            ivSave.setVisibility(View.VISIBLE);

            etUsername.requestFocus();
            //keyboard controls
            InputMethodManager imm = (InputMethodManager)
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etUsername, InputMethodManager.SHOW_IMPLICIT);

        });

// ✔️ Tick clicked → SAVE
        ivSave.setOnClickListener(v -> {
            String newName = etUsername.getText().toString().trim();

            if (newName.isEmpty()) {
                etUsername.setError("Enter name");
                return;
            }

            // Save to SharedPreferences
            sp.edit().putString("username", newName).apply();

            // Update UI
            tvUsername.setText(newName);

            etUsername.setVisibility(View.GONE);
            ivSave.setVisibility(View.GONE);

            tvUsername.setVisibility(View.VISIBLE);
            ivEdit.setVisibility(View.VISIBLE);
        });

        profileFav.setOnClickListener(view1 -> {
           BottomNavigationView bottomNavigationView=requireActivity().findViewById(R.id.bottom_navigation);
           bottomNavigationView.setSelectedItemId(R.id.bottom_favorite);
       });
      //App more apps to try

        moreAppsToTry.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                openUrlSafely(getString(R.string.developer_apps_url));
            } else {
                customToast.showToast(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
            }
        });
        appPrivacyBtn.setOnClickListener( v->{
                if(isInternetAvailable()) {
                    openUrlSafely(getString(R.string.privacy_policy_url));
                } else {
                    customToast.showToast(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
//
                }
       } );

        appTermsBtn.setOnClickListener( v->{

            if(isInternetAvailable()) {
             openUrlSafely(getString(R.string.terms_url));
            } else {
                customToast.showToast(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
            }
        } );

       return view;

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void openUrlSafely(String url) {
        if (isInternetAvailable()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            requireActivity().startActivity(intent);
        } else {
            customToast.showToast(requireContext(), getString(R.string.check_network), Toast.LENGTH_SHORT);
        }
    }


    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        Network nw = cm.getActiveNetwork();
        if (nw == null) return false;

        NetworkCapabilities nc = cm.getNetworkCapabilities(nw);
        return nc != null && (
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }
}