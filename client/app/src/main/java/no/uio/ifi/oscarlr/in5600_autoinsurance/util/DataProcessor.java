package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_CLAIMS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_DISPLAY_MODE;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_EMAIL;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_FIRST_NAME;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_LAST_NAME;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.User;

public class DataProcessor {

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private final String TAG = "DataProcessor";

    public DataProcessor(Context ctx) {
        this.context = ctx;
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public String getUserId() {
        return String.valueOf(sharedPreferences.getInt(KEY_ID, 0));
    }

    public String getFirstName() {
        return sharedPreferences.getString(KEY_FIRST_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "0");
    }

    public void setUser(User user) {
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        editor.putString(KEY_LAST_NAME, user.getLastName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.apply();
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
        if (json == null) {
            return null;
        }
        try {
            claims = objectMapper.readValue(json, new TypeReference<List<Claim>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return claims;
    }

    public Claim getClaimById(int id) {
        Claim claim = null;
        String json = sharedPreferences.getString(KEY_CLAIMS, null);
        try {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject jsonObject = (JSONObject) jsonArray.get(id);
            Gson gson = new Gson();
            claim = gson.fromJson(jsonObject.toString(), Claim.class);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return claim;
    }

    public void setClaimById(String id, Claim claim, boolean replace) {
        List<Claim> claims = getClaims();
        if (claims == null) {
            claims = new ArrayList<>();
        }
        if (replace) {
            claims.set(Integer.parseInt(id), claim);
        }
        else {
            claims.add(claim);
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(claims);
        editor.putString(KEY_CLAIMS, jsonString);
        editor.commit();

    }

    public void setDisplayMode(String string) {
        editor.putString(KEY_DISPLAY_MODE, string);
        editor.commit();
    }

    public String getDisplayMode() {
        String displayMode = sharedPreferences.getString(KEY_DISPLAY_MODE, null);
        if (displayMode == null) {
            Log.i(TAG, "No display mode is set, will use default");
            return "day";
        }
        return displayMode;
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
