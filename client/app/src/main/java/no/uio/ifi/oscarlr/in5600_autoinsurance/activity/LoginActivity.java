package no.uio.ifi.oscarlr.in5600_autoinsurance.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.DataRepository;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;
import no.uio.ifi.oscarlr.in5600_autoinsurance.singleton.VolleySingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private EditText email;
    private EditText password;

    private ActivityResultLauncher<String[]> activityResultLauncherPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSavedDisplayMode();

        DataProcessor dataProcessor = new DataProcessor(getApplicationContext());

        activityResultLauncherPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> permissions) {
                        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                            if (!entry.getValue()) {
                                return;
                            }
                        }
                        // If user is already logged in, go directly to MainActivity
                        if (dataProcessor.getFirstName() != null) {
                            Log.i(TAG, "User is already logged in.");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else {
                            login(null);
                        }
                    }
                });
        activityResultLauncherPermissions.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});

        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        email = findViewById(R.id.editText_email_login);
        password = findViewById(R.id.editText_password_login);

        // Two-way data binding
        viewModel.getEmail().observe(this, e -> {
            // Check that changes were made
            if (!email.getText().toString().equals(e))
                email.setText(e);
        });

        viewModel.getPassword().observe(this, p -> {
            // Check that changes were made
            if (!password.getText().toString().equals(p))
                password.setText(p);
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setEmail(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void login(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncherPermissions.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
            return;
        }
        if (email.getText().toString().isEmpty()) {
            email.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.getText().toString().isEmpty()) {
            password.requestFocus();
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        DataRepository dataRepository = new DataRepository(getApplicationContext());
        StringRequest stringRequest = dataRepository.postRemoteLogin(email.getText().toString(), Hash.toMD5(password.getText().toString()));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getSavedDisplayMode() {
        DataProcessor dataProcessor = new DataProcessor(this);
        String displayMode = dataProcessor.getDisplayMode();
        switch (displayMode) {
            case "day":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "night":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
    
}
