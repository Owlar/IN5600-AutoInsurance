package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.MapUtil;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.ClaimDetailsViewModel;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.MapViewModel;

/* The main Google Map which can be navigated to from bottom navigation */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private static final String TAG = "MapFragment";

    private MapViewModel mapViewModel;
    private String markerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        mapViewModel.getClaimId().observe(getViewLifecycleOwner(), claimId -> {
            markerId = claimId;
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        setMapClickListeners();
        setUISettings();

        showClaims();
    }

    private void setMapClickListeners() {
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
    }

    private void setUISettings() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    private void showClaims() {
        DataProcessor dataProcessor = new DataProcessor(requireActivity());
        List<Claim> claimList = dataProcessor.getClaims();
        if (claimList == null || claimList.isEmpty()) {
            Toast.makeText(requireActivity(), "You have no claims to show on map", Toast.LENGTH_LONG).show();
            return;
        }
        for (Claim claim : claimList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Claim: " + claim.getClaimId());
            markerOptions.snippet(claim.getClaimDes());
            markerOptions.position(MapUtil.stringLocationToLatLng(claim.getClaimLocation()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            Marker marker = mMap.addMarker(markerOptions);
            assert marker != null;

            marker.setTag(claim.getClaimId());

            if (Objects.equals(marker.getTag(), markerId)) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 13));
            }
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        Marker noClaimMarker = mMap.addMarker(markerOptions);
        assert noClaimMarker != null;

        noClaimMarker.setTag(-1);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        // No claim so don't show info window
        if (Objects.requireNonNull(marker.getTag()).equals(-1)) return;

        Log.i(TAG, "Clicked on marker " + marker.getTag() + "'s InfoWindow");

        ClaimDetailsViewModel viewModel = new ViewModelProvider(requireActivity()).get(ClaimDetailsViewModel.class);
        viewModel.setObject(marker.getTag());

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, new ClaimDetailsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
