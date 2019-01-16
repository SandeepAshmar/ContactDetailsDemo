package com.example.monet_android1.contactdetailsdemo.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monet_android1.contactdetailsdemo.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.provider.MediaStore.MediaColumns.TITLE;
import static android.provider.MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.VideoColumns.DESCRIPTION;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.filterNumber;
import static com.example.monet_android1.contactdetailsdemo.helper.AppUtils.settingDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class OcrFragment extends Fragment {

    final int CAMERA_REQUEST_CODE = 200;
    final int STORAGE_REQUEST_CODE = 400;
    final int IMAGE_PICK_GALLERY_CODE = 1000;
    final int IMAGE_PICK_CAMERA_CODE = 1001;
    private String cameraPermission[];
    private String storagePermission[];
    private Uri image_uri = null;
    private ImageView img_scanAgain;
    private EditText name, mobile, email;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private TextView tv_allResult;
    private Bitmap bitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ocr, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        img_scanAgain = view.findViewById(R.id.img_scanAgain);
//        name = view.findViewById(R.id.tv_scanName);
//        mobile = view.findViewById(R.id.tv_scanMobile);
//        email = view.findViewById(R.id.tv_scanEmail);
        tv_allResult = view.findViewById(R.id.tv_allResult);

        img_scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    pickGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void pickGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (checkStoragePermission()) {
                pickGallery();
            } else {
                settingDialog(getActivity());
            }
        } else {
            requestStoragePermission();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image_uri);
                    img_scanAgain.setImageBitmap(rotateImageIfRequired(bitmap, getActivity(), image_uri));
                    scanImage(rotateImageIfRequired(bitmap, getActivity(), image_uri));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please upload correct image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Oops! something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {

        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    private void scanImage(Bitmap bitmap) {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        StringBuilder sb = new StringBuilder();

        if (items.size() != 0) {

            for (int i = 0; i < items.size(); i++) {
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("&&" +
                        "");

                String text = sb.toString();
                text = filterNumber(text);
                Log.d("TAG", "scanImage: mobile "+text);
            }
            tv_allResult.setText(sb.toString());
        } else {
            Toast.makeText(getActivity(), "There is not text found in this image, Please upload" +
                    "another image to sacn", Toast.LENGTH_LONG).show();
        }

    }

}
