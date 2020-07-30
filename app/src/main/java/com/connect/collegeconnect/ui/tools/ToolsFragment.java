package com.connect.collegeconnect.ui.tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.connect.collegeconnect.ui.CovidFragment;
import com.connect.collegeconnect.R;
import com.connect.collegeconnect.ui.event.UpcomingEvents;
import com.connect.collegeconnect.ui.RoomLocator.RoomLocFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ToolsFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    TextView tv;
    CardView cardView, cardcovid, events;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tools, null);
        cardView = view.findViewById(R.id.roomLocator);
        cardcovid = view.findViewById(R.id.covid);
        events = view.findViewById(R.id.events);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null)
            bottomNavigationView = getActivity().findViewById(R.id.bottomNav);
        tv = getActivity().findViewById(R.id.navTitle);
        tv.setText("TOOLS");
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RoomLocFragment();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        cardcovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CovidFragment();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpcomingEvents.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        bottomNavigationView.getMenu().findItem(R.id.nav_tools).setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.getMenu().findItem(R.id.nav_tools).setChecked(true);
    }
}