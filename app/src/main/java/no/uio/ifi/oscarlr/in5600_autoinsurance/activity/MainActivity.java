package no.uio.ifi.oscarlr.in5600_autoinsurance.activity;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.SharedPreferencesConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logout(View view) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // TODO: Delete the user's claims and any other internal data

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}