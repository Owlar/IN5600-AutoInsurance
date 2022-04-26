package no.uio.ifi.oscarlr.in5600_autoinsurance.viewpager.screen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import no.uio.ifi.oscarlr.in5600_autoinsurance.R;
import no.uio.ifi.oscarlr.in5600_autoinsurance.singleton.NewClaimSingleton;
import no.uio.ifi.oscarlr.in5600_autoinsurance.util.FileUtils;

public class NewClaimPhotoScreen extends Fragment {

    private final ViewPager2 viewPager;
    private final ActivityResultLauncher<Intent> activityResultLauncherPhotoGallery;
    private final ActivityResultLauncher<Intent> activityResultLauncherCamera;
    private ImageView imageView;
    private String currentPhotoPath;
    private final NewClaimSingleton newClaimSingleton;
    private final int replaceClaimWithID;

    public NewClaimPhotoScreen(ViewPager2 viewPager, int replaceClaimWithID) {
        this.viewPager = viewPager;
        this.replaceClaimWithID = replaceClaimWithID;
        newClaimSingleton = NewClaimSingleton.getInstance();

        activityResultLauncherCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            galleryAddPic();
                            Uri uri = Uri.fromFile(new File(currentPhotoPath));
                            imageView.setImageURI(uri);
                            setPhotoBitmapForSingleton(uri);
                        }
                        else {
//                            Log.d("test", "camera result ikke ok");
                            File file = new File(currentPhotoPath);
                            boolean b = file.delete();
//                            Log.d("test", "file deleted:" + b);
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
                            // Get correct filepath back
                            Uri uri = result.getData().getData();
                            FileUtils fileUtils = new FileUtils(getContext());
                            currentPhotoPath = fileUtils.getPath(uri);

                            setPhotoBitmapForSingleton(uri);
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
            if (imageView.getDrawable() == null) {
                Toast.makeText(requireContext(), "Please select a photo", Toast.LENGTH_SHORT).show();
                return;
            }
            if (replaceClaimWithID == -1) {
                newClaimSingleton.getClaim(replaceClaimWithID).setClaimPhotoFilepath(currentPhotoPath);
                newClaimSingleton.getClaim(replaceClaimWithID).setClaimPhotoFilename(currentPhotoPath);
            }
            viewPager.setCurrentItem(3);
        });

        imageView = view.findViewById(R.id.screenPhotoImageView);

        setClaimPhotoIfUpdatingClaim();

        view.findViewById(R.id.screenPhotoTakePhotoButton).setOnClickListener(view1 -> {
            try {
                createImageFile();
                dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        view.findViewById(R.id.screenPhotoPickImageButton).setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncherPhotoGallery.launch(intent);
        });

        return view;
    }

    private void setClaimPhotoIfUpdatingClaim() {
        if (replaceClaimWithID != -1) {
            try {
                Bitmap bitmap = newClaimSingleton.getClaim(replaceClaimWithID).getClaimPhotoBitmap();
                imageView.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
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
                Uri photoURI = FileProvider.getUriForFile(requireContext(), getString(R.string.app_package), photoFile);
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

    private void setPhotoBitmapForSingleton(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            newClaimSingleton.getClaim(replaceClaimWithID).setClaimPhotoBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}