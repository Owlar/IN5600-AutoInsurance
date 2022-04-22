package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.SettingsViewModel;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchAppTheme = view.findViewById(R.id.switch_app_theme);
        SwitchMaterial switchOfflineMode = view.findViewById(R.id.switch_offline_mode);

        view.findViewById(R.id.settings_back_button).setOnClickListener(v -> {
            getParentFragmentManager().popBackStackImmediate();
        });

        SettingsViewModel viewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

        // Switch should be initially checked/unchecked depending on night/day in shared preferences
        switchAppTheme.setChecked(getDisplayMode());

        viewModel.getCheckedAppTheme().observe(getViewLifecycleOwner(), checkedAppTheme -> {
            switchAppTheme.setChecked(checkedAppTheme);
        });

        viewModel.getCheckedOfflineMode().observe(getViewLifecycleOwner(), checkedOfflineMode -> {
            switchOfflineMode.setChecked(checkedOfflineMode);
        });

        switchAppTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setCheckedAppTheme(isChecked);
                setDisplayMode(isChecked);
            }
        });

        switchOfflineMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setCheckedOfflineMode(isChecked);
            }
        });

        return view;
    }


    private boolean getDisplayMode() {
        DataProcessor dataProcessor = new DataProcessor(getContext());
        if (dataProcessor.getDisplayMode().equals("night")) return true;
        else return false;
    }

    private void setDisplayMode(Boolean checked) {
        DataProcessor dataProcessor = new DataProcessor(getContext());
        if (checked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dataProcessor.setDisplayMode("night");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dataProcessor.setDisplayMode("day");
        }
    }
}
