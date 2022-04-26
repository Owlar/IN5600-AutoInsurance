package no.uio.ifi.oscarlr.in5600_autoinsurance.viewpager.screen;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Objects;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.singleton.NewClaimSingleton;
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
            closeKeyboard(view);
            viewPager.setCurrentItem(0);
        });

        EditText editText = view.findViewById(R.id.screenTwoClaimDescription);
        setClaimDescriptionIfUpdatingClaim(editText);

        view.findViewById(R.id.nextButtonDescriptionScreen).setOnClickListener(view1 -> {
            closeKeyboard(view);
            if (editText.getText().toString().isEmpty()) {
                editText.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

                Toast.makeText(requireContext(), "Please enter a claim description", Toast.LENGTH_SHORT).show();
                return;
            }
            viewPager.setCurrentItem(2);
            newClaimSingleton.getClaim(replaceClaimWithID).setClaimDes((Objects.requireNonNull(editText.getText()).toString()));
        });


        return view;
    }



    private void setClaimDescriptionIfUpdatingClaim(EditText editText) {
        if (replaceClaimWithID != -1) {
            DataProcessor dataProcessor = new DataProcessor(requireContext());
            Claim updateClaim = dataProcessor.getClaimById(replaceClaimWithID);
            editText.setText(updateClaim.getClaimDes());
        }
    }

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}