package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

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
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.DataRepository;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class NewClaimSummaryScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final DialogFragment dialogFragment;
    private final int replaceClaimWithID;
    private final NewClaimSingleton newClaimSingleton;

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

        view.findViewById(R.id.backButtonSummaryScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(3));

        view.findViewById(R.id.finishButtonSummaryScreen).setOnClickListener(view1 -> {

            DataRepository dataRepository = new DataRepository(requireContext());
            DataProcessor dataProcessor = new DataProcessor(requireContext());
            StringRequest stringRequest;
            String indexUpdateClaim;
            String userId = dataProcessor.getUserId();
            Claim claim = newClaimSingleton.getClaim(replaceClaimWithID);

            if (replaceClaimWithID == -1) {
                indexUpdateClaim = newClaimSingleton.getNumberOfClaims();
                stringRequest = dataRepository.postRemoteInsertNewClaim(userId, indexUpdateClaim, claim);
            }
            else {
                indexUpdateClaim = String.valueOf(replaceClaimWithID);
                stringRequest = dataRepository.postRemoteUpdateClaim(userId, indexUpdateClaim, claim);
            }
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

            saveToLocalStorage(replaceClaimWithID != -1);

            dialogFragment.dismiss();
        });

        setClaimDetails(view);

        // TODO: Implement commented out as map in getParams() in postMethodUploadPhoto() method
        /*
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("claimId", (replaceClaimWithID == -1) ? newClaimSingleton.getNumberOfClaims() : String.valueOf(replaceClaimWithID));
        map.put("fileName", SERVER_PATH_TO_SAVED_PHOTOS + claim.getClaimPhotoFilename() + SERVER_FILETYPE_FOR_SAVED_PHOTOS);
//        Log.d("test", claim.getClaimPhotoFilepath());
//        Log.d("test", claim.getClaimPhotoFilename());
        map.put("imageStringBase64", convertImageToString());
         */

        return view;
    }

    private void setClaimDetails(View view) {
        TextView title = view.findViewById(R.id.claim_details_title);
        TextView status = view.findViewById(R.id.claim_details_status);
        TextView description = view.findViewById(R.id.claim_details_description);
        TextView location = view.findViewById(R.id.claim_details_location);
        ImageView imageView = view.findViewById(R.id.claim_details_image);

        Claim claim = newClaimSingleton.getClaim(replaceClaimWithID);

        String text = "Claim ID: " + claim.getClaimId();
        title.setText(text);

        String statusTxt = "Status: " + claim.getClaimStatus();
        status.setText(statusTxt);
        description.setText(claim.getClaimDes());
        location.setText(claim.getClaimLocation());

        File f = new File(claim.getClaimPhotoFilepath());
        if (f.exists())
            imageView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
    }

    private void saveToLocalStorage(boolean replace) {
        DataProcessor dataProcessor = new DataProcessor(getContext());

        Claim claim = newClaimSingleton.getClaim(replaceClaimWithID);
        String id = (replaceClaimWithID == -1) ? newClaimSingleton.getNumberOfClaims() : String.valueOf(replaceClaimWithID);
        claim.setClaimId(id);

        dataProcessor.setClaimById(id, claim, replace);
    }

    private String convertImageToString() {
        Bitmap bitmap = newClaimSingleton.getClaim(replaceClaimWithID).getClaimPhotoBitmap();;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}