package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.ClaimDetailsViewModel;

public class ClaimDetailsFragment extends Fragment {

    private final String TAG = "ClaimDetailsFragment";
    private ClaimDetailsViewModel viewModel;

    private TextView title;
    private TextView status;
    private TextView description;
    private TextView location;
    private ImageView imageView;

    public ClaimDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_claim_details, container, false);

        title = view.findViewById(R.id.claim_details_title);
        status = view.findViewById(R.id.claim_details_status);
        description = view.findViewById(R.id.claim_details_description);
        location = view.findViewById(R.id.claim_details_location);
        imageView = view.findViewById(R.id.claim_details_image);

        viewModel = new ViewModelProvider(requireActivity()).get(ClaimDetailsViewModel.class);
        viewModel.getObject().observe(getViewLifecycleOwner(), object -> {
            Log.i(TAG, "Getting details of claim with id: " + object);
            getClaim(Integer.parseInt(object.toString()));
        });

        return view;
    }

    private void getClaim(int claimId) {
        DataProcessor dataProcessor = new DataProcessor(getContext());
        Claim claim = dataProcessor.getClaimById(claimId);
        if (claim == null)
            Toast.makeText(getContext(), "Could not get details of claim with id: " + claimId, Toast.LENGTH_SHORT).show();
        else
            setClaimDetails(claim);
    }

    private void setClaimDetails(Claim claim) {
        String text = "Claim: " + claim.getClaimId();
        title.setText(text);

        status.setText(claim.getClaimStatus());
        description.setText(claim.getClaimDes());
        location.setText(claim.getClaimLocation());

        File f = new File(claim.getClaimPhotoFilepath());
        if (f.exists())
            imageView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
    }
}
