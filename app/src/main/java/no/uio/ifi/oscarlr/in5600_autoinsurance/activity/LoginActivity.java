package no.uio.ifi.oscarlr.in5600_autoinsurance.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.User;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private static final String URL = "http://10.0.2.2:8080";

    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: Go to MainActivity if already logged in

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

        // TODO: Create RequestQueue as a Singleton
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL + "/methodPostRemoteLogin", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // TODO: Add user to shared preferences
                    User user = new User(
                            jsonObject.getInt("id"),
                            jsonObject.getString("firstName"),
                            jsonObject.getString("lastName"),
                            jsonObject.getString("passClear"),
                            jsonObject.getString("passHash"),
                            jsonObject.getString("email")
                    );
                    finish();
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
        requestQueue.add(stringRequest);
    }
}
