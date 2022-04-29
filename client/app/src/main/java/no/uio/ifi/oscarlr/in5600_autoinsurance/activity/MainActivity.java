package no.uio.ifi.oscarlr.in5600_autoinsurance.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.databinding.ActivityMainBinding;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.HomeFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.MapFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Gives warning against using switch
            if (item.getItemId() == R.id.home) {
                loadFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.profile) {
                loadFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.map) {
                loadFragment(new MapFragment());
            }
            return true;
        });
    }

    /**
     * Load the fragment that was selected
     * @param fragment The fragment to be loaded
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
    }
}