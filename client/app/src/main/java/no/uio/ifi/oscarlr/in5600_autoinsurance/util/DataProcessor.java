package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_CLAIMS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void setClaims(List<Claim> claims) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(claims);
        editor.putString(KEY_CLAIMS, jsonString);
        editor.commit();
    }

    public List<Claim> getClaims() {
        List<Claim> claims = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = sharedPreferences.getString(KEY_CLAIMS, null);
        try {
            claims = objectMapper.readValue(json, new TypeReference<List<Claim>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return claims;
    }

}
