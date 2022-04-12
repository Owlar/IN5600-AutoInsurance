package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class NewClaimSummaryScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final DialogFragment dialogFragment;
    private final NewClaimSingleton newClaimSingleton;
    private final SharedPreferences sharedPreferences;
    private final int replaceClaimWithID;

    public NewClaimSummaryScreen(ViewPager2 viewPager, DialogFragment dialogFragment, int replaceClaimWithID) {
        this.viewPager = viewPager;
        this.dialogFragment = dialogFragment;
        newClaimSingleton = NewClaimSingleton.getInstance();
        sharedPreferences = dialogFragment.requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.replaceClaimWithID = replaceClaimWithID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_summary_screen, container, false);

        view.findViewById(R.id.backButtonSummaryScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(3));

        view.findViewById(R.id.finishButtonSummaryScreen).setOnClickListener(view1 -> {
            StringRequest stringRequest;

            if (replaceClaimWithID == -1) {
                // Don't replace, make new claim
                stringRequest = postStringRequest("/postInsertNewClaim");
            }
            else {
                // Replace claim
                stringRequest = postStringRequest("/postUpdateClaim");
            }

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            saveToLocalStorage(replaceClaimWithID != -1);

            dialogFragment.dismiss();
        });

        return view;
    }

    private StringRequest postStringRequest(String serverEndpoint) {
        return new StringRequest(Request.Method.POST,  URL+ serverEndpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return fillGetParams(serverEndpoint);
            }
        };
    }

    private Map<String, String> fillGetParams(String stringRequestType) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(sharedPreferences.getInt(KEY_ID, 0)));
        String claimDes = newClaimSingleton.getClaimDes();
        String claimPho = newClaimSingleton.getClaimPhoto();
        String claimLoc = newClaimSingleton.getClaimPosition();
        String claimSta = "na";

        if (stringRequestType.equals("/postInsertNewClaim")) {
            map.put("indexUpdateClaim", newClaimSingleton.getNumberOfClaims());
            map.put("newClaimDes", claimDes);
            map.put("newClaimPho", claimPho);
            map.put("newClaimLoc", claimLoc);
            map.put("newClaimSta", claimSta);
        }
        else if (stringRequestType.equals("/postUpdateClaim")) {
            map.put("indexUpdateClaim", String.valueOf(replaceClaimWithID));
            map.put("updateClaimDes", claimDes);
            map.put("updateClaimPho", claimPho);
            map.put("updateClaimLoc", claimLoc);
            map.put("updateClaimSta", claimSta);
        }
        return map;
    }

    private void saveToLocalStorage(boolean replace) {
        DataProcessor dataProcessor = new DataProcessor(getContext());

        Claim claim = new Claim();
        String id = (replaceClaimWithID == -1) ? newClaimSingleton.getNumberOfClaims() : String.valueOf(replaceClaimWithID);
        claim.setClaimId(id);
        claim.setClaimDes(newClaimSingleton.getClaimDes());
        claim.setClaimPhotoFilepath(newClaimSingleton.getClaimPhotoFilepath());
        claim.setClaimLocation(newClaimSingleton.getClaimPosition());

        dataProcessor.setClaimById(id, claim, replace);
    }
}