package com.example.learnly_skillbit_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnly_skillbit_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

       MaterialCardView profileFav=view.findViewById(R.id.profileScreen_favorite);
       MaterialCardView moreAppsToTry=view.findViewById(R.id.moreAppsToTry);

       profileFav.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               BottomNavigationView bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
               bottomNavigationView.setSelectedItemId(R.id.bottom_favorite);
           }
       });

       moreAppsToTry.setOnClickListener(v->{
                String url="https://play.google.com/store/apps/dev?id=8374043554473360551";
                   Intent intent=new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                   startActivity(intent);
               }
               );


       return view;

    }}