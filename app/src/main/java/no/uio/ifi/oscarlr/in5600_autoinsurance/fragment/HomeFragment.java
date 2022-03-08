package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewAdapter;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Claim> claims;
    private RecyclerViewAdapter recyclerViewAdapter;
    private SharedPreferences sharedPreferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MaterialDividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL));
        claims = new ArrayList<>();


        RequestQueue requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        int userId = sharedPreferences.getInt(KEY_ID, 0);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL +"/getMethodMyClaims?id=" + userId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int numberOfClaims = Integer.parseInt(response.getString("numberOfClaims"));
                    JSONArray jsonArrayClaimDes = response.getJSONArray("claimDes");

                    for (int i = 0; i < numberOfClaims; i++) {
                        Claim claim = new Claim();
                        claim.setClaimDes(jsonArrayClaimDes.get(i).toString());
                        claims.add(claim);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity().getApplicationContext()));
                    recyclerViewAdapter = new RecyclerViewAdapter(HomeFragment.this.getActivity().getApplicationContext(), claims);
                    recyclerView.setAdapter(recyclerViewAdapter);

                    if (numberOfClaims == 0) {
                        view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Log.d("Home", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
        return view;
    }

}