package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.List;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.activity.LoginActivity;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class ProfileFragment extends Fragment {

    private TextView textView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView imageView = view.findViewById(R.id.user_profile_picture_profile);
        if (imageView.getDrawable() == null)
            imageView.setImageResource(R.drawable.ic_baseline_person_24);

        DataProcessor dataProcessor = new DataProcessor(getContext());
        String email = dataProcessor.getEmail();

        textView = view.findViewById(R.id.email_user_profile);
        textView.setText(email);

        ImageButton menuButton = view.findViewById(R.id.menu_button_profile_fragment);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.drawer_navigation_menu_profile, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_change_password:
                                replaceFragment(new ChangePasswordFragment());
                                break;
                            case R.id.menu_log_out:
                                logout();
                                break;
                            case R.id.menu_settings:
                                replaceFragment(new SettingsFragment());
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void logout() {
        DataProcessor dataProcessor = new DataProcessor(requireActivity());

        // Delete claim photos stored locally
        List<Claim> claims = dataProcessor.getClaims();
        if (claims != null) {
            for (Claim c : claims) {
                String filepath = c.getClaimPhotoFilepath();
                File file = new File(filepath);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d("test", b +"");
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    if (getContext() != null) {
                        getContext().sendBroadcast(intent);
                    }
                }
            }
        }

        dataProcessor.clear();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        requireActivity().finish();
    }
}