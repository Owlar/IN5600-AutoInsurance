package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.MapUtil;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

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
        if (claimList == null) {
            Toast.makeText(requireActivity(), "You have no claims to show on map", Toast.LENGTH_LONG).show();
            return;
        }
        for (Claim claim : claimList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Id: " + claim.claimId);
            markerOptions.snippet(claim.claimDes);
            markerOptions.position(MapUtil.stringLocationToLatLng(claim.getClaimPosition()));

            Marker marker = mMap.addMarker(markerOptions);
            assert marker != null;

            marker.setTag(claim.claimId);
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mMap.addMarker(markerOptions);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.i(TAG, "Clicked on marker " + marker.getId() + "'s InfoWindow");
    }
}
