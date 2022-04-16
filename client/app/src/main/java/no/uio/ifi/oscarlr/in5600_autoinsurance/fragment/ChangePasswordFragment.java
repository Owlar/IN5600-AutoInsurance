package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.KEY_EMAIL;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.SharedPreferencesConstants.SHARED_PREFERENCES;
import static no.uio.ifi.oscarlr.in5600_autoinsurance.util.constant.VolleyConstants.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.Hash;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.VolleySingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {

    private static final String TAG = "ChangePasswordFragment";

    private SharedPreferences sharedPreferences;

    private EditText editText_newPassword;
    private EditText editText_confirmNewPassword;

    private String email;
    private String newPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(KEY_EMAIL, "0");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        TextView textView = view.findViewById(R.id.textView_email);
        if (email != null && !email.isEmpty()) {
            textView.setText(email);
        }

        Button button = view.findViewById(R.id.change_password);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        editText_newPassword = view.findViewById(R.id.editText_new_password);
        editText_confirmNewPassword = view.findViewById(R.id.editText_confirm_new_password);

        ChangePasswordViewModel viewModel = new ViewModelProvider(requireActivity()).get(ChangePasswordViewModel.class);

        // Below in onCreateView is two-way data binding
        viewModel.getNewPassword().observe(getViewLifecycleOwner(), newPassword -> {
            if (!editText_newPassword.getText().toString().equals(newPassword))
                editText_newPassword.setText(newPassword);
        });
        viewModel.getConfirmNewPassword().observe(getViewLifecycleOwner(), confirmNewPassword -> {
            if (!editText_confirmNewPassword.getText().toString().equals(confirmNewPassword))
                editText_confirmNewPassword.setText(confirmNewPassword);
        });

        editText_newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setNewPassword(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        editText_confirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setConfirmNewPassword(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    public void changePassword() {
        if (editText_newPassword.getText().toString().isEmpty()) {
            editText_newPassword.requestFocus();
            Toast.makeText(requireContext(), "Please enter a new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editText_confirmNewPassword.getText().toString().isEmpty()) {
            editText_confirmNewPassword.requestFocus();
            Toast.makeText(requireContext(), "Please confirm the new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!editText_newPassword.getText().toString().equals(editText_confirmNewPassword.getText().toString())) {
            Toast.makeText(requireContext(), "Passwords are not the same!", Toast.LENGTH_SHORT).show();
            return;
        }
        newPassword = editText_confirmNewPassword.getText().toString();

        RequestQueue requestQueue = VolleySingleton.getInstance(requireActivity().getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL + "/methodPostChangePasswd", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i(TAG, response);
                    if (response.equals("OK")) {
                        editText_newPassword.getText().clear();
                        editText_confirmNewPassword.getText().clear();

                        goToProfileFragment();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(requireActivity().getApplicationContext(), "Couldn't change password, please try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("em", email);
                map.put("np", newPassword);
                map.put("ph", Hash.toMD5(newPassword));
                return map;
            }
        };
        VolleySingleton.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void goToProfileFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, new ProfileFragment());
        fragmentTransaction.commit();

        Toast.makeText(requireActivity().getApplicationContext(), "Password has been changed", Toast.LENGTH_SHORT).show();

    }
}
