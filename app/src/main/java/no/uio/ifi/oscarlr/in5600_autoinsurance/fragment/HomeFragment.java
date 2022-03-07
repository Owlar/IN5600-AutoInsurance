package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.divider.MaterialDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.activity.MainActivity;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewAdapter;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    List<Claim> claims;
    RecyclerViewAdapter recyclerViewAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MaterialDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        claims = new ArrayList<>();


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        // TODO replace ?id=0 with the currently logged in person
        String url = "http://10.0.2.2:8080";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url +"/getMethodMyClaims?id=0", null, response -> {
            Log.d("Home", response.toString());
            try {
                int numberOfClaims = Integer.parseInt(response.getString("numberOfClaims"));
                JSONArray jsonArrayClaimDes = response.getJSONArray("claimDes");

                for (int i = 0; i < numberOfClaims; i++) {
                    Claim claim = new Claim();
                    claim.setClaimDes(jsonArrayClaimDes.get(i).toString());
                    claims.add(claim);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                recyclerViewAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), claims);
                recyclerView.setAdapter(recyclerViewAdapter);

                if (numberOfClaims == 0) {
                    view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                Log.d("Home", e.toString());
            }
        }, error -> error.printStackTrace());

        requestQueue.add(objectRequest);
        return view;
    }

}