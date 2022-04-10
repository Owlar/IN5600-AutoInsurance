package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NewClaimLocationScreen extends Fragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private final ViewPager2 viewPager;
    private final NewClaimSingleton newClaimSingleton;

    private final int PERMISSION_LOCATION_ID = 1;
    private final String TAG = "NewClaimLocationScreen";

    private GoogleMap mMap = null;
    private Button myLocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng lastPosition = null;

    private SearchView searchView;

    public NewClaimLocationScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        newClaimSingleton = NewClaimSingleton.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_location_screen, container, false);

        // TODO: Refactor view1 below and ID's in ViewPager2 XML files
        view.findViewById(R.id.backButtonLocationScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(2));

        view.findViewById(R.id.nextButtonLocationScreen).setOnClickListener(view1 -> {
            if (lastPosition == null) {
                Toast.makeText(requireContext(), "Please choose a claim position", Toast.LENGTH_SHORT).show();
                return;
            }
            viewPager.setCurrentItem(4);
            newClaimSingleton.setClaimPosition(lastPosition.latitude + "," + lastPosition.longitude);
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        myLocationButton = view.findViewById(R.id.screenLocationMyPositionButton);

        setupMapView(view, savedInstanceState);

        setupSearch(view);

        return view;
    }

    // Needed because of ViewPager
    private void setupMapView(View view, Bundle savedInstanceState) {
        MapView mapView = view.findViewById(R.id.new_claim_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
    }

    private void setupSearch(View view) {
        searchView = view.findViewById(R.id.mapSearchView);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return searchLocation();
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private boolean searchLocation() {
        String location = searchView.getQuery().toString();
        List<Address> addresses = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                addresses = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address;
            if (addresses != null) {
                address = addresses.get(0);
                lastPosition = new LatLng(address.getLatitude(), address.getLongitude());
                if (createNewMarker() != null) {
                    goToMarkedPosition();
                }
            } else {
                Toast.makeText(requireContext(), "No results for your search, please try again!", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        setMapClickListeners();
        setUISettings();

        myLocationButton.setOnClickListener(v -> requestLocationPermission());
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(PERMISSION_LOCATION_ID)
    private void requestLocationPermission() {
        if (EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Could use getCurrentLocation(), but getLastLocation() minimizes battery usage
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lastPosition = new LatLng(location.getLatitude(), location.getLongitude());
                                logLastPosition();

                                if (createNewMarker() != null) {
                                    goToMarkedPosition();
                                }
                            }
                        }
                    });
        } else {
            EasyPermissions.requestPermissions(this, "Need location access to show MyLocation", PERMISSION_LOCATION_ID, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private Marker createNewMarker() {
        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        // TODO: Potentially change title and snippet to those set in previous pages in ViewPager
        markerOptions.title("My Position");
        markerOptions.snippet("My Description");
        markerOptions.position(lastPosition);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        return mMap.addMarker(markerOptions);
    }

    private void goToMarkedPosition() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 13));
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

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        // TODO: Potentially change title and snippet to those set in previous pages in ViewPager
        markerOptions.title("My Position");
        markerOptions.snippet("My Description");
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        mMap.addMarker(markerOptions);

        lastPosition = latLng;

        logLastPosition();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Log.i(TAG, "Clicked on marker " + marker.getId() + "'s InfoWindow");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    // User should be able to change position of claim to that of an existing marker's position by clicking on it
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        lastPosition = marker.getPosition();
        logLastPosition();
        return true;
    }

    private void logLastPosition() {
        Log.i(TAG, "Last position is " + lastPosition);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.i(TAG, "Permission granted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "Permission denied");
    }
}