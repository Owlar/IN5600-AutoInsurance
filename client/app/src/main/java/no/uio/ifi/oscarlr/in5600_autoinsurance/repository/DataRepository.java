package no.uio.ifi.oscarlr.in5600_autoinsurance.repository;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.SERVER_FILETYPE_FOR_SAVED_PHOTOS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.SERVER_PATH_TO_SAVED_PHOTOS;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.activity.MainActivity;
import no.uio.ifi.oscarlr.in5600_autoinsurance.fragment.ProfileFragment;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.User;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.ClaimStatus;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;

public class DataRepository {

    private final Context ctx;
    private final DataProcessor dataProcessor;
    private static final String TAG = "DataRepository";

    public DataRepository(Context ctx) {
        this.ctx = ctx;
        this.dataProcessor = new DataProcessor(ctx);
    }

    public StringRequest postRemoteLogin(String email, String password) {
        return new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.isEmpty()) {
                        Log.d(TAG, response);
                        Toast.makeText(ctx, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject(response);

                        User user = new User(
                                jsonObject.getInt("id"),
                                jsonObject.getString("firstName"),
                                jsonObject.getString("lastName"),
                                jsonObject.getString("passClear"),
                                jsonObject.getString("passHash"),
                                jsonObject.getString("email")
                        );
                        dataProcessor.setUser(user);

                        Intent intent = new Intent(ctx, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ctx, "Couldn't login due to server issues, please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email);
                map.put("ph", password);
                return map;
            }
        };
    }

    public void postRemoteLoginWithModifiedPassword(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.isEmpty()) {
                        Log.d(TAG, response);
                        Toast.makeText(ctx, "Wrong password due to new instance of server", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject(response);

                        User user = new User(
                                jsonObject.getInt("id"),
                                jsonObject.getString("firstName"),
                                jsonObject.getString("lastName"),
                                jsonObject.getString("passClear"),
                                jsonObject.getString("passHash"),
                                jsonObject.getString("email")
                        );
                        dataProcessor.setUser(user);
                        dataProcessor.setPasswordHash(user.getPassClear());
                    }

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email);
                map.put("ph", password);
                return map;
            }
        };
        VolleySingleton.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public StringRequest postRemoteChangePassword(String email, EditText newPassword, EditText confirmNewPassword, FragmentTransaction fragmentTransaction) {
        return new StringRequest(Request.Method.POST, URL + "/methodPostChangePasswd", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("OK")) {
                        newPassword.getText().clear();
                        confirmNewPassword.getText().clear();

                        fragmentTransaction.replace(R.id.fragment_container_view, new ProfileFragment());
                        fragmentTransaction.commit();

                        Toast.makeText(ctx.getApplicationContext(), "Password has been changed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ctx.getApplicationContext(), "Couldn't change password, please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email);
                map.put("np", newPassword.getText().toString());
                map.put("ph", Hash.toMD5(newPassword.getText().toString()));
                return map;
            }
        };
    }

    public StringRequest postRemoteNotifyServerPasswordChange(String email, String newPassword, String newPasswordHash) {
        return new StringRequest(Request.Method.POST, URL + "/methodPostChangePasswd", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("OK")) {
                        Log.d(TAG, "Change password to notify server: " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ctx.getApplicationContext(), "Couldn't change password, please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email);
                map.put("np", newPassword);
                map.put("ph", newPasswordHash);
                return map;
            }
        };
    }

    public StringRequest postRemoteInsertNewClaim(String userId, String indexUpdateClaim, Claim claim) {
        return new StringRequest(Request.Method.POST,  URL + "/postInsertNewClaim", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("OK"))
                    Toast.makeText(ctx.getApplicationContext(), ClaimStatus.OPENED + " claim", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("indexUpdateClaim", indexUpdateClaim);
                map.put("newClaimDes", claim.getClaimDes());
                map.put("newClaimPho", claim.getClaimPhotoFilepath());
                map.put("newClaimLoc", claim.getClaimLocation());
                map.put("newClaimSta", ClaimStatus.OPENED.toString());
                return map;
            }
        };
    }

    public StringRequest postRemoteUpdateClaim(String userId, String indexUpdateClaim, Claim claim) {
        return new StringRequest(Request.Method.POST,  URL + "/postUpdateClaim", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("OK"))
                    Toast.makeText(ctx.getApplicationContext(), ClaimStatus.REOPENED + " claim", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("indexUpdateClaim", indexUpdateClaim);
                map.put("updateClaimDes", claim.getClaimDes());
                map.put("updateClaimPho", claim.getClaimPhotoFilepath());
                map.put("updateClaimLoc", claim.getClaimLocation());
                map.put("updateClaimSta", ClaimStatus.REOPENED.toString());
                return map;
            }
        };
    }

    public StringRequest postRemoteUploadPhoto(String userId, String claimId, Claim claim, String imageStringBase64) {
        return new StringRequest(Request.Method.POST,  URL + "/postMethodUploadPhoto", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("OK"))
                    Log.i(TAG, "Photo uploaded successfully");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("userId", userId);
                map.put("claimId", claimId);
                map.put("fileName", SERVER_PATH_TO_SAVED_PHOTOS + claim.getClaimPhotoFilename() + SERVER_FILETYPE_FOR_SAVED_PHOTOS);
                map.put("imageStringBase64", imageStringBase64);
                return map;
            }
        };

    }
}
