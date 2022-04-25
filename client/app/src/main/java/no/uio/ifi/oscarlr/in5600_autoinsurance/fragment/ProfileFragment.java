package no.uio.ifi.oscarlr.in5600_autoinsurance.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.activity.LoginActivity;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class ProfileFragment extends Fragment {

    private DataProcessor dataProcessor;
    private TextView textView;
    private ImageView imageView;
    private String currentPhotoPath;
    private final ActivityResultLauncher<Intent> activityResultLauncherCamera;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        activityResultLauncherCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri uri = Uri.fromFile(new File(currentPhotoPath));
                            imageView.setImageURI(uri);
                            dataProcessor.setProfilePicPhotoPath(currentPhotoPath);
                        }
                    }
                }
        );
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.user_profile_picture_profile);

        dataProcessor = new DataProcessor(getContext());
        setExistingProfilePic();

        String email = dataProcessor.getEmail();

        textView = view.findViewById(R.id.email_user_profile);
        textView.setText(email);

        view.findViewById(R.id.new_picture_profile).setOnClickListener(view1 -> {
            try {
                createImageFile();
                dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ImageButton menuButton = view.findViewById(R.id.menu_button_profile_fragment);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.drawer_navigation_menu_profile, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Warning against using switch
                        if (item.getItemId() == R.id.menu_change_password)
                            replaceFragment(new ChangePasswordFragment());
                        else if (item.getItemId() == R.id.menu_log_out)
                            logout();
                        else if (item.getItemId() == R.id.menu_settings)
                            replaceFragment(new SettingsFragment());

                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        return view;
    }

    private void setExistingProfilePic() {
        String photoPath = dataProcessor.getProfilePicPhotoPath();
        Log.d(TAG, photoPath + "");
        if (photoPath != null) {
            Uri uri = Uri.fromFile(new File(photoPath));
            imageView.setImageURI(uri);
        }
        else if (imageView.getDrawable() == null) {
            imageView.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timestamp, ".png", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(), getString(R.string.app_package), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityResultLauncherCamera.launch(takePictureIntent);
            }
        }
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
                Log.d(TAG, "Filepath of claim image is: " + filepath);
                File file = new File(filepath);
                if (file.exists()) {
                    boolean b = file.delete();
                    Log.d(TAG, "Claim images were deleted: " + b);
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