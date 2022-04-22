package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class NewClaimDialogFragment extends DialogFragment {

    private final int replaceClaimWithID;

    public NewClaimDialogFragment(int replaceClaimWithID) {
        this.replaceClaimWithID = replaceClaimWithID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Base_Theme_AppCompat_DialogWhenLarge);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_dialog, container, false);

        if (replaceClaimWithID == -1) {
            NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();
            newClaimSingleton.initNewClaim();
        }
        else {
            TextView textView = view.findViewById(R.id.claimDialogTitle);
            textView.setText(R.string.replace_claim_dialog);
        }

        ViewPager2 viewPager = view.findViewById(R.id.ViewPager);
        viewPager.setOffscreenPageLimit(1); // Fixes editText losing focus on Description page, for some reason...

        // For GoogleMaps and overall consistency between pages
        viewPager.setUserInputEnabled(false);

        ArrayList<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                new NewClaimIntroScreen(viewPager),
                new NewClaimDescriptionScreen(viewPager, replaceClaimWithID),
                new NewClaimPhotoScreen(viewPager, replaceClaimWithID),
                new NewClaimLocationScreen(viewPager, replaceClaimWithID),
                new NewClaimSummaryScreen(viewPager, this, replaceClaimWithID)
        ));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        // Close the fullscreen dialog
        view.findViewById(R.id.fullscreenDialogClose).setOnClickListener(view1 -> {
            this.dismiss();
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
}