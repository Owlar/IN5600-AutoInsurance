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
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewAdapter;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewInterface;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim.NewClaimDialogFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim.NewClaimSingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private SharedPreferences sharedPreferences;
    private int numberOfClaims = 0;
    private final int MAX_NUMBER_OF_CLAIMS = 5;

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
            if (numberOfClaims >= MAX_NUMBER_OF_CLAIMS) {
                Toast.makeText(getActivity(), "Max claims reached. Replace or delete a claim", Toast.LENGTH_LONG).show();
            }
            else {
                startNewClaim(view, -1);
            }
        });
    }

    private void createRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Claim> claims = new ArrayList<>();
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), claims, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        int userID = sharedPreferences.getInt(KEY_ID, 0);
        NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();

        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL +"/getMethodMyClaims?id=" + userID, null, response -> {
//            Log.d("Home", response.toString());
            try {
                numberOfClaims = Integer.parseInt(response.getString("numberOfClaims"));
                newClaimSingleton.setNumberOfClaims(numberOfClaims);
                JSONArray jsonArrayClaimDes = response.getJSONArray("claimDes");
                JSONArray jsonArrayClaimPosition = response.getJSONArray("claimLocation");
                JSONArray jsonArrayClaimId = response.getJSONArray("claimId");

                for (int i = 0; i < numberOfClaims; i++) {
                    Claim claim = new Claim();
                    claim.setClaimDes(jsonArrayClaimDes.get(i).toString());
                    claim.setClaimLocation(jsonArrayClaimPosition.get(i).toString());
                    claim.setClaimId(jsonArrayClaimId.get(i).toString());
                    claims.add(claim);
                }

                recyclerViewAdapter.notifyDataSetChanged();

                saveToLocalStorage(claims);

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
                Toast.makeText(getContext(), "Problems getting claims from server", Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }

    private void saveToLocalStorage(List<Claim> claims) {
        DataProcessor dataProcessor = new DataProcessor(getContext());
        dataProcessor.setClaims(claims);
    }

    public void startNewClaim(View view, int replaceClaimWithID) {
        DialogFragment dialogFragment = new NewClaimDialogFragment(replaceClaimWithID);
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
    }

    @Override
    public void onReplaceClick(int position) {
        startNewClaim(getView(), position);
    }
}