package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_CLAIMS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;

public class DataProcessor {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public DataProcessor(Context ctx) {
        this.context = ctx;
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveClaims(List<Claim> claims) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(claims);
        editor.putString(KEY_CLAIMS, jsonString);
        editor.commit();
    }

}
