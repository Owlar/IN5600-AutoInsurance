package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public NewClaimSummaryScreen(ViewPager2 viewPager, DialogFragment dialogFragment) {
        this.viewPager = viewPager;
        this.dialogFragment = dialogFragment;
        newClaimSingleton = NewClaimSingleton.getInstance();
        sharedPreferences = dialogFragment.requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_summary_screen, container, false);

        view.findViewById(R.id.backButtonSummaryScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(3));

        view.findViewById(R.id.finishButtonSummaryScreen).setOnClickListener(view1 -> {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,  URL+ "/postInsertNewClaim", new Response.Listener<String>() {
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
                    map.put("newClaimPho", "na");
                    map.put("newClaimLoc", "na");
                    map.put("newClaimSta", "na");
                    return map;
                }
            };

            VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

            dialogFragment.dismiss();
        });

        return view;
    }
}