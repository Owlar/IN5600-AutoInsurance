package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager.screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager.NewClaimSingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.DataRepository;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.VolleySingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class NewClaimSummaryScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final DialogFragment dialogFragment;
    private final int replaceClaimWithID;
    private final NewClaimSingleton newClaimSingleton;

    private TextView title;
    private TextView description;
    private TextView location;
    private ImageView imageView;

    public NewClaimSummaryScreen(ViewPager2 viewPager, DialogFragment dialogFragment, int replaceClaimWithID) {
        this.viewPager = viewPager;
        this.dialogFragment = dialogFragment;
        this.replaceClaimWithID = replaceClaimWithID;
        newClaimSingleton = NewClaimSingleton.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_claim_summary_screen, container, false);

        title = view.findViewById(R.id.claim_details_title);
        description = view.findViewById(R.id.claim_details_description);
        location = view.findViewById(R.id.claim_details_location);
        imageView = view.findViewById(R.id.claim_details_image);

        view.findViewById(R.id.backButtonSummaryScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(3));

        view.findViewById(R.id.finishButtonSummaryScreen).setOnClickListener(view1 -> {

            DataRepository dataRepository = new DataRepository(requireContext());
            DataProcessor dataProcessor = new DataProcessor(requireContext());
            StringRequest stringRequest;
            StringRequest photoUploadRequest;
            String index;
            String userId = dataProcessor.getUserId();
            Claim claim = newClaimSingleton.getClaim(replaceClaimWithID);

            if (replaceClaimWithID == -1) {
                index = newClaimSingleton.getNumberOfClaims();

                stringRequest = dataRepository.postRemoteInsertNewClaim(userId, index, claim);
                photoUploadRequest = dataRepository.postRemoteUploadPhoto( userId, index, claim, convertImageToString() );

                dataProcessor.setClaimById(index, claim, false);
            }
            else {
                index = String.valueOf(replaceClaimWithID);

                stringRequest = dataRepository.postRemoteUpdateClaim(userId, index, claim);
                photoUploadRequest = dataRepository.postRemoteUploadPhoto( userId, index, claim, convertImageToString() );

                dataProcessor.setClaimById(index, claim, true);
            }
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(photoUploadRequest);

            dialogFragment.dismiss();
        });

        return view;
    }

    /* It's done here because of ViewPager2's page off limit,
    and removing off limit defeats a purpose of ViewPager2 */
    @Override
    public void onResume() {
        super.onResume();
        setClaimDetails();
    }

    private void setClaimDetails() {
        Claim claim = newClaimSingleton.getClaim(replaceClaimWithID);

        if (claim.getClaimId() != null) {
            String text = "Claim ID: " + claim.getClaimId();
            title.setText(text);
        }
        description.setText(claim.getClaimDes());
        location.setText(claim.getClaimLocation());

        if (claim.getClaimPhotoFilepath() != null) {
            File f = new File(claim.getClaimPhotoFilepath());
            if (f.exists())
                imageView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
        }
    }

    private String convertImageToString() {
        Bitmap bitmap = newClaimSingleton.getClaim(replaceClaimWithID).getClaimPhotoBitmap();;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}