package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewAdapter;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim.NewClaimDialogFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim.NewClaimSingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class HomeFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createRecyclerView(view);

        view.findViewById(R.id.floating_action_button).setOnClickListener(view1 -> {
            DialogFragment dialogFragment = new NewClaimDialogFragment();
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "tag");

            // Handle dialog dismiss and recreate the RecyclerView
            getParentFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentViewDestroyed(fm, f);
                    getParentFragmentManager().unregisterFragmentLifecycleCallbacks(this);

                    createRecyclerView(view);
                }
            }, false);
        });
    }

    private void createRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new MaterialDividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
        List<Claim> claims = new ArrayList<>();
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), claims);
        recyclerView.setAdapter(recyclerViewAdapter);

        int userID = sharedPreferences.getInt(KEY_ID, 0);
        NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL +"/getMethodMyClaims?id=" + userID, null, response -> {
//            Log.d("Home", response.toString());
            try {
                int numberOfClaims = Integer.parseInt(response.getString("numberOfClaims"));
                newClaimSingleton.setNumberOfClaims(numberOfClaims);
                JSONArray jsonArrayClaimDes = response.getJSONArray("claimDes");

                for (int i = 0; i < numberOfClaims; i++) {
                    Claim claim = new Claim();
                    claim.setClaimDes(jsonArrayClaimDes.get(i).toString());
                    claims.add(claim);
                }

                recyclerViewAdapter.notifyDataSetChanged();

                if (numberOfClaims == 0) {
                    view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                Log.d("Home", e.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }

}