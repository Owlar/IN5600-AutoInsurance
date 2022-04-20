package no.uio.ifi.oscarlr.in5600_autoinsurance.repository;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.Intent;
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
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.User;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;

/* DataRepository with Volley requests. Don't need to use AsyncTask because Volley already manages network related tasks on a separate thread */
public class DataRepository {

    private final Context ctx;
    private final DataProcessor dataProcessor;

    public DataRepository(Context ctx) {
        this.ctx = ctx;
        this.dataProcessor = new DataProcessor(ctx);
    }

    public StringRequest postRemoteLogin(String email, String password) {
        return new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
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
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ctx, "Couldn't login, please try again!", Toast.LENGTH_SHORT).show();
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
}
