package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;


public class NewClaimIntroScreen extends Fragment {

    private final ViewPager2 viewPager;
    public NewClaimIntroScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_intro_screen, container, false);

        view.findViewById(R.id.nextButtonIntroScreen).setOnClickListener(view1 -> {
            viewPager.setCurrentItem(1);
        });

        return view;
    }
}