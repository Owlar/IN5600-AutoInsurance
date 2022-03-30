package no.uio.ifi.oscarlr.in5600_autoinsurance.activity;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_EMAIL;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_FIRST_NAME;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_ID;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_LAST_NAME;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.User;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private EditText email;
    private EditText password;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // If user is already logged in, go directly to MainActivity
        if (sharedPreferences.getString(KEY_FIRST_NAME, null) != null) {
            Log.i(TAG, "User is already logged in.");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        email = findViewById(R.id.editText_email_login);
        password = findViewById(R.id.editText_password_login);
    }

    public void login(View view) {
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

        RequestQueue requestQueue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin", new Response.Listener<String>() {
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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(KEY_ID, user.getId());
                    editor.putString(KEY_FIRST_NAME, user.getFirstName());
                    editor.putString(KEY_LAST_NAME, user.getLastName());
                    editor.putString(KEY_EMAIL, user.getEmail());
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't login, please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email.getText().toString());
                map.put("ph", Hash.toMD5(password.getText().toString()));
                return map;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void skipLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("ID_KEY", "1");
        startActivity(intent);
    }
}
