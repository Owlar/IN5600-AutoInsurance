package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
                stringRequest = postInsertNewClaim();
            }
            else {
                // Replace claim
                stringRequest = postUpdateClaim();
            }

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

            dialogFragment.dismiss();
        });

        return view;
    }

    // TODO merge the two getParams() into 1, maybe in a separate function -> to avoid having to change value 2 places
    private StringRequest postInsertNewClaim() {
        return new StringRequest(Request.Method.POST,  URL+ "/postInsertNewClaim", new Response.Listener<String>() {
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
                Map<String, String> map = new HashMap<>();
                map.put("userId", String.valueOf(sharedPreferences.getInt(KEY_ID, 0)));
                map.put("indexUpdateClaim", newClaimSingleton.getNumberOfClaims());
                map.put("newClaimDes", newClaimSingleton.getClaimDes());
                map.put("newClaimPho", newClaimSingleton.getClaimPhoto());
                map.put("newClaimLoc", newClaimSingleton.getClaimPosition());
                map.put("newClaimSta", "na");
                return map;
            }
        };
    }

    private StringRequest postUpdateClaim() {
        return new StringRequest(Request.Method.POST, URL + "/postUpdateClaim", new Response.Listener<String>() {
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
                Map<String, String> map = new HashMap<>();
                map.put("userId", String.valueOf(sharedPreferences.getInt(KEY_ID, 0)));
                map.put("indexUpdateClaim", String.valueOf(replaceClaimWithID));
                map.put("updateClaimDes", newClaimSingleton.getClaimDes());
                map.put("updateClaimPho", newClaimSingleton.getClaimPhoto());
                map.put("updateClaimLoc", newClaimSingleton.getClaimPosition());
                map.put("updateClaimSta", "na");
                return map;
            }
        };
    }
}