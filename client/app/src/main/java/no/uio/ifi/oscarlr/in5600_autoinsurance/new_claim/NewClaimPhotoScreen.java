package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.model.Claim;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.DataProcessor;

public class NewClaimPhotoScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final ActivityResultLauncher<Intent> activityResultLauncherPhotoGallery;
    private final ActivityResultLauncher<Intent> activityResultLauncherCamera;
    private final ActivityResultLauncher<String[]> activityResultLauncherPermissionsCamera;
    private ImageView imageView;
    private String currentPhotoPath;
    private final NewClaimSingleton newClaimSingleton;
    private final int replaceClaimWithID;

    public NewClaimPhotoScreen(ViewPager2 viewPager, int replaceClaimWithID) {
        this.viewPager = viewPager;
        this.replaceClaimWithID = replaceClaimWithID;
        newClaimSingleton = NewClaimSingleton.getInstance();

        activityResultLauncherPermissionsCamera = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> permissions) {
                        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
//                        Log.d("test", entry.getKey() + ", " + entry.getValue());
                            if (!entry.getValue()) {
                                return;
                            }
                        }
                        // Proceed, if permissions are granted
                        try {
                            createImageFile();
                            dispatchTakePictureIntent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        activityResultLauncherCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            galleryAddPic();
                            imageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
                        }
                    }
                }
        );

        activityResultLauncherPhotoGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            imageView.setImageURI(result.getData().getData());
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_claim_photo_screen, container, false);

        view.findViewById(R.id.backButtonPhotoScreen).setOnClickListener(view1 -> viewPager.setCurrentItem(1));

        view.findViewById(R.id.nextButtonPhotoScreen).setOnClickListener(view1 -> {
            if (imageView.getDrawable() != null) {
                // TODO avoid doing multiple times for same picture (if you go back from summary)
                newClaimSingleton.setClaimPhoto(convertImageToString());
            }
            else {
//                return; // TODO can add toast and prevent continuing without choosing a photo
            }

            viewPager.setCurrentItem(3);
        });

        imageView = view.findViewById(R.id.screenPhotoImageView);

        setClaimPhotoIfUpdatingClaim();

        view.findViewById(R.id.screenPhotoTakePhotoButton).setOnClickListener(view1 -> {
            activityResultLauncherPermissionsCamera.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
        });

        view.findViewById(R.id.screenPhotoPickImageButton).setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncherPhotoGallery.launch(intent);
        });

        return view;
    }

    private void setClaimPhotoIfUpdatingClaim() {
        DataProcessor dataProcessor = new DataProcessor(requireContext());
        Claim updateClaim = dataProcessor.getClaimById(replaceClaimWithID);
        if (replaceClaimWithID != -1) {
            /*
            currentPhotoPath = updateClaim.getClaimPhoto();
            imageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
            */
            // TODO: Set image
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                // TODO maybe change hardcoded app package name
                Uri photoURI = FileProvider.getUriForFile(requireContext(), "no.uio.ifi.oscarlr.in5600_autoinsurance", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activityResultLauncherCamera.launch(takePictureIntent);
            }
        }
    }

    // Update the gallery so the saved picture can be seen in the gallery immediately
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        requireActivity().sendBroadcast(mediaScanIntent);
    }

    private String convertImageToString() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}