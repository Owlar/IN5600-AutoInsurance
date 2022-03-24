package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class NewClaimDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Base_Theme_AppCompat_DialogWhenLarge);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_dialog, container, false);
        NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();

        ViewPager2 viewPager = view.findViewById(R.id.ViewPager);
        ArrayList<Fragment> fragmentList = new ArrayList<>(Arrays.asList(
                new NewClaimIntroScreen(viewPager),
                new NewClaimDescriptionScreen(viewPager),
                new NewClaimPhotoScreen(viewPager),
                new NewClaimLocationScreen(viewPager),
                new NewClaimSummaryScreen(viewPager, this)
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