package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.SERVER_FILETYPE_FOR_SAVED_PHOTOS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.SERVER_PATH_TO_SAVED_PHOTOS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewAdapter;
import no.uio.ifi.oscarlr.in5600_autoinsurance.adapter.RecyclerViewInterface;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager.NewClaimDialogFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.viewpager.NewClaimSingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.VolleySingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.ClaimDetailsViewModel;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.MapViewModel;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private static final String TAG = "HomeFragment";
    private SharedPreferences sharedPreferences;
    private int numberOfClaims = 0;
    private final int MAX_NUMBER_OF_CLAIMS = 5;
    private List<Claim> claims;
    private AtomicInteger downloadPhotosFromServerCounter;
    private ClaimDetailsViewModel claimDetailsViewModel;
    private MapViewModel mapViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        downloadPhotosFromServerCounter = new AtomicInteger();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        claimDetailsViewModel = new ViewModelProvider(requireActivity()).get(ClaimDetailsViewModel.class);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        createRecyclerView(view);

        view.findViewById(R.id.floating_action_button).setOnClickListener(view1 -> {
            if (numberOfClaims >= MAX_NUMBER_OF_CLAIMS) {
                Toast.makeText(getActivity(), "Max claims reached", Toast.LENGTH_LONG).show();
            }
            else {
                startNewClaim(view, -1);
            }
        });
    }

    private void createRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        claims = new ArrayList<>();
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), claims, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        int userID = sharedPreferences.getInt(KEY_ID, 0);
        NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();

        // It's here and not in DataRepository because of RecyclerView
        @SuppressLint("NotifyDataSetChanged")
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL +"/getMethodMyClaims?id=" + userID, null, response -> {
            try {
                numberOfClaims = Integer.parseInt(response.getString("numberOfClaims"));
                JSONArray jsonArrayClaimDes = response.getJSONArray("claimDes");
                JSONArray jsonArrayClaimPosition = response.getJSONArray("claimLocation");
                JSONArray jsonArrayClaimId = response.getJSONArray("claimId");
                JSONArray jsonArrayClaimPhoto = response.getJSONArray("claimPhoto");
                JSONArray jsonArrayClaimStatus = response.getJSONArray("claimStatus");

                // If a claim's image is not stored locally, need to download from server
                boolean waitingOnServerPhotoDownload = false;
                ArrayList<StringRequest> stringRequests = new ArrayList<>();

                for (int i = 0; i < numberOfClaims; i++) {
                    Claim claim = new Claim();
                    claim.setClaimDes(jsonArrayClaimDes.get(i).toString());
                    claim.setClaimLocation(jsonArrayClaimPosition.get(i).toString());
                    claim.setClaimId(jsonArrayClaimId.get(i).toString());
                    claim.setClaimStatus(jsonArrayClaimStatus.get(i).toString());
                    try {
                        String filepathSavedOnServer = jsonArrayClaimPhoto.get(i).toString();
                        File file = new File(filepathSavedOnServer);
                        claim.setClaimPhotoFilename(filepathSavedOnServer);
                        if (file.exists()) {
                            claim.setClaimPhotoBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                            claim.setClaimPhotoFilepath(filepathSavedOnServer);
                        }
                        else {
                            // Download photo from server and save locally
                            StringRequest stringRequest = getMethodDownloadPhoto(claim.getClaimPhotoFilename(), filepathSavedOnServer, view);
                            stringRequests.add(stringRequest);
                            waitingOnServerPhotoDownload = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    claims.add(claim);
                }
                if (waitingOnServerPhotoDownload) {
                    downloadPhotosFromServerCounter.set(stringRequests.size());
                    for (StringRequest stringRequest : stringRequests) {
                        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                    }
                    return;
                }

                saveToLocalStorage(claims);
                recyclerViewAdapter.notifyDataSetChanged();
                newClaimSingleton.setClaims(claims);

                if (claims.size() == 0) {
                    view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "Problems getting claims from server", Toast.LENGTH_SHORT).show();
                DataProcessor dataProcessor = new DataProcessor(getContext());
                List<Claim> processorClaims = dataProcessor.getClaims();
                recyclerViewAdapter.disableReplaceButton();
                if (processorClaims != null) {
                    claims.addAll(processorClaims);
                    recyclerViewAdapter.notifyDataSetChanged();
                    NewClaimSingleton newClaimSingleton = NewClaimSingleton.getInstance();
                    newClaimSingleton.setClaims(claims);
                }

                numberOfClaims = claims.size();
                if (numberOfClaims == 0) {
                    view.findViewById(R.id.textView_forEmpty_recyclerView).setVisibility(View.VISIBLE);
                }
            }
        });

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(objectRequest);
    }

    private Bitmap convertBase64StringToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
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

    private void seeClaimDetails() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, new ClaimDetailsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSeeDetailsClick(int position) {
        claimDetailsViewModel.setObject(position);
        seeClaimDetails();
    }

    private void seeMapFragmentWithMarkedPosition() {
        if (getActivity() != null) {
            BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
            bnv.setSelectedItemId(R.id.map);
        }
    }

    @Override
    public void onSeeLocationClick(int position) {
        mapViewModel.setClaimId(String.valueOf(position));
        seeMapFragmentWithMarkedPosition();
    }

    // It's here and not in DataRepository because of RecyclerView
    public StringRequest getMethodDownloadPhoto(String filename, String fullFilepath, View view) {
        String fileName = SERVER_PATH_TO_SAVED_PHOTOS + filename + SERVER_FILETYPE_FOR_SAVED_PHOTOS;
        return new StringRequest(Request.Method.GET,  URL + "/getMethodDownloadPhoto?fileName=" + fileName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bitmap bitmap = convertBase64StringToBitmap(response);
                if (bitmap != null) {
                    try {
                        if (fullFilepath.contains("/Camera/")) {
                            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera");
                            boolean b = f.mkdir();
                        }
                        File imageFile = new File(fullFilepath);
                        try (FileOutputStream fileOutputStream = new FileOutputStream(imageFile)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri contentUri = Uri.fromFile(imageFile);
                            mediaScanIntent.setData(contentUri);
                            requireActivity().sendBroadcast(mediaScanIntent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int photosSavedLeftTodo = downloadPhotosFromServerCounter.decrementAndGet();
                    if (photosSavedLeftTodo == 0) {
                        // Last claim to download photo
                        createRecyclerView(view);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
}