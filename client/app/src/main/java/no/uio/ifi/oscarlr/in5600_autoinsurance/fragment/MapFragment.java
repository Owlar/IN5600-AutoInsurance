package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private static final LatLng POSITION_UIO = new LatLng(59.940103683567294, 10.721749598320056);
    private static final String MARKER_TITLE = "Marker";

    private static final String TAG = "MapFragment";

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

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        setMapClickListeners();
        setUISettings();
        // TODO: Clusters
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

        for (Claim claim : dataProcessor.getClaims()) {
            MarkerOptions markerOptions = new MarkerOptions();
            // TODO: USE CLAIM ATTRIBUTES FOR TITLE AND POSITION
            markerOptions.title(MARKER_TITLE);
            markerOptions.snippet(claim.claimDes);
            markerOptions.position(POSITION_UIO);

            Marker marker = mMap.addMarker(markerOptions);
            assert marker != null;

            marker.setTag(claim.claimId);
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.i(TAG, "Clicked on marker " + marker.getId() + "'s InfoWindow");
    }
}
