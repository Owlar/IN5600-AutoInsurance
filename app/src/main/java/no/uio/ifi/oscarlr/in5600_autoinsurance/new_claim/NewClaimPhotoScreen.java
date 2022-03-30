package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class NewClaimPhotoScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final ActivityResultLauncher<String> test = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {

                }
            }
    );

    public NewClaimPhotoScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_photo_screen, container, false);

        view.findViewById(R.id.backButtonPhotoScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(1));

        view.findViewById(R.id.nextButtonPhotoScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(3));

        view.findViewById(R.id.screenPhotoPickImageButton).setOnClickListener(view1 -> {
            //  https://stackoverflow.com/a/63654043
            test.launch("image/*");
        });

        return view;
    }
}