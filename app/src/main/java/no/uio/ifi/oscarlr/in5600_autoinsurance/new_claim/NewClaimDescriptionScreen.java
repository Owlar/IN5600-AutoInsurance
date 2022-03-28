package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;


public class NewClaimDescriptionScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final NewClaimSingleton newClaimSingleton;

    public NewClaimDescriptionScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        newClaimSingleton = NewClaimSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_description_screen, container, false);

        view.findViewById(R.id.backButtonDescriptionScreen).setOnClickListener(view1 -> {
            viewPager.setCurrentItem(0);
        });

        view.findViewById(R.id.nextButtonDescriptionScreen).setOnClickListener(view1 -> {
            viewPager.setCurrentItem(2);
            TextInputEditText editText = view.findViewById(R.id.descriptionEditText);
            newClaimSingleton.setClaimDes(Objects.requireNonNull(editText.getText()).toString());
        });


        return view;
    }
}