package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class NewClaimLocationScreen extends Fragment implements OnMapReadyCallback {

    private final ViewPager2 viewPager;

    private GoogleMap mMap = null;

    public NewClaimLocationScreen(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_location_screen, container, false);

        view.findViewById(R.id.backButtonLocationScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(2));

        view.findViewById(R.id.nextButtonLocationScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(4));

        // Needed because of ViewPager
        MapView mapView = view.findViewById(R.id.new_claim_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

    }
}