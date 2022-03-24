package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class NewClaimLocationScreen extends Fragment {

    private final ViewPager2 viewPager;
    public NewClaimLocationScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_location_screen, container, false);

        view.findViewById(R.id.backButtonLocationScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(2));

        view.findViewById(R.id.nextButtonLocationScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(4));

        return view;
    }
}