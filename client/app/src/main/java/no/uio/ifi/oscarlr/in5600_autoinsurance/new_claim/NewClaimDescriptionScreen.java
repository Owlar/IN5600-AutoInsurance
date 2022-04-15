package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;


public class NewClaimDescriptionScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final int replaceClaimWithID;
    private final NewClaimSingleton newClaimSingleton;

    public NewClaimDescriptionScreen(ViewPager2 viewPager, int replaceClaimWithID) {
        this.viewPager = viewPager;
        this.replaceClaimWithID = replaceClaimWithID;

        newClaimSingleton = NewClaimSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_description_screen, container, false);

        view.findViewById(R.id.backButtonDescriptionScreen).setOnClickListener(view1 -> {
            viewPager.setCurrentItem(0);
        });

        TextInputLayout description = view.findViewById(R.id.screenTwoClaimDescription);
        setClaimDescriptionIfUpdatingClaim(description);

        view.findViewById(R.id.nextButtonDescriptionScreen).setOnClickListener(view1 -> {
            if (Objects.requireNonNull(description.getEditText()).getText().toString().isEmpty()) {
                description.requestFocus();
                Toast.makeText(requireContext(), "Please enter a claim description", Toast.LENGTH_SHORT).show();
                return;
            }
            viewPager.setCurrentItem(2);
            TextInputEditText editText = view.findViewById(R.id.descriptionEditText);
            newClaimSingleton.getClaim(replaceClaimWithID).setClaimDes((Objects.requireNonNull(editText.getText()).toString()));
        });


        return view;
    }

    private void setClaimDescriptionIfUpdatingClaim(TextInputLayout description) {
        if (replaceClaimWithID != -1) {
            DataProcessor dataProcessor = new DataProcessor(requireContext());
            Claim updateClaim = dataProcessor.getClaimById(replaceClaimWithID);
            Objects.requireNonNull(description.getEditText()).setText(updateClaim.claimDes);
        }
    }
}