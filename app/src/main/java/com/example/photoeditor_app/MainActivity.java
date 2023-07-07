package com.example.photoeditor_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.photoeditor_app.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity
{
    // to get available this class we have to add this solution :
    // buildFeatures {
    //   viewBinding true
    //}
    // in the build.gradle (app level) file
    ActivityMainBinding binding;
    int IMAGE_REQUEST_CODE = 33;
    int CAMERA_REQUEST_CODE = 99;
    int RESULT_CODE = 66;
    int REQUEST_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        onClicks();
    }

    private void onClicks()
    {
        binding.editPhoto.setOnClickListener(v->
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,IMAGE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null)
        {
            Uri inputImageUri = data.getData();
            Intent intent = new Intent(this, DsPhotoEditorActivity.class);
            intent.setData(inputImageUri);
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"photo");
            startActivityForResult(intent,RESULT_CODE);
        }

        if(requestCode == RESULT_CODE)
        {
            Intent intent = new Intent(MainActivity.this,CameraActivity.class);
            intent.setData(data.getData());
            startActivity(intent);
        }

        if(requestCode == CAMERA_REQUEST_CODE)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri uri = getImageUri(photo);
            Intent intent = new Intent(this,DsPhotoEditorActivity.class);
            intent.setData(uri);
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"photo");

            int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_CROP};
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,toolsToHide);
            startActivityForResult(intent,RESULT_CODE);
        }
    }

    private Uri getImageUri(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Title", "Description");
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quit the app")
                .setMessage("Are you sure you want to quit the app..?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}