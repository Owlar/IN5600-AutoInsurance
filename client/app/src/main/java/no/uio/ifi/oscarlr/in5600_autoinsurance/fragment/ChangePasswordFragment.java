package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.StringRequest;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.DataRepository;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;
import no.uio.ifi.oscarlr.in5600_autoinsurance.repository.VolleySingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel.ChangePasswordViewModel;

public class ChangePasswordFragment extends Fragment {

    private static final String TAG = "ChangePasswordFragment";

    private EditText editText_newPassword;
    private EditText editText_confirmNewPassword;

    private String email;
    private String newPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        DataProcessor dataProcessor = new DataProcessor(getContext());
        email = dataProcessor.getEmail();

        TextView textView = view.findViewById(R.id.textView_email);
        if (email != null && !email.isEmpty()) {
            textView.setText(email);
        }

        view.findViewById(R.id.change_password_back_button).setOnClickListener(v -> {
            getParentFragmentManager().popBackStackImmediate();
        });

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
        DataRepository dataRepository = new DataRepository(requireActivity());
        StringRequest stringRequest = dataRepository.postRemoteChangePassword(
                email,
                editText_newPassword,
                editText_confirmNewPassword,
                getParentFragmentManager().beginTransaction()
        );

        VolleySingleton.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
