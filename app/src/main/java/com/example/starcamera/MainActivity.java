package com.example.starcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private ImageView imageView;
    private ImageView imageIndicator;
    private Button buttonBack;
    private Button buttonConfirm;
    private Button buttonImport;
    private Button buttonCapture;
    private Bitmap bitmap;
    private Uri imageUri; // Uri变量保存照片的位置

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT <= 32) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 显示一个解释，然后再次请求权限
                    showExplanation("需要权限", "我们需要相机和读取外部存储的权限来访问你的相机和相册", new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_MEDIA_IMAGES) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_MEDIA_VIDEO) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                    // 显示一个解释，然后再次请求权限
                    showExplanation("需要权限", "我们需要相机权限以及读取媒体图像、视频和用户选择的视觉媒体的权限来访问你的相机和相册", new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED}, PERMISSION_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED},
                            PERMISSION_REQUEST_CODE);
                }
            }
        }


        imageView = findViewById(R.id.imageView);
        imageIndicator = findViewById(R.id.imageIndicator);
        buttonBack = findViewById(R.id.buttonBack);
        buttonImport = findViewById(R.id.buttonImport);
        buttonCapture = findViewById(R.id.buttonCapture);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonConfirm.setVisibility(View.GONE);

        buttonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Capture button clicked");
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Camera permission not granted, requesting permission");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    Log.d("MainActivity", "Camera permission granted, starting camera");
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // 创建一个用于保存照片的Uri
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                imageIndicator.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.GONE);
                buttonConfirm.setVisibility(View.GONE);
                buttonCapture.setVisibility(View.VISIBLE);
                buttonImport.setVisibility(View.VISIBLE);

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    // Create intent
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);

                    // Put image Uri into intent
                    intent.putExtra("imageUri", imageUri.toString());

                    // Start ResultActivity
                    startActivity(intent);
                } else {
                    // Show a message to the user
                    Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void showExplanation(String title, String message, final String[] permissions, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> ActivityCompat.requestPermissions(MainActivity.this, permissions, permissionRequestCode));
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                //}
            } else {
                // 权限被拒绝，你需要告诉用户为什么需要这个权限
                showExplanation("需要权限", "我们需要相机权限以及读取媒体图像、视频和用户选择的视觉媒体的权限来访问你的相机和相册", permissions, PERMISSION_REQUEST_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button buttonImport = findViewById(R.id.buttonImport);
        Button buttonCapture = findViewById(R.id.buttonCapture);
        Log.d("MainActivity", "onActivityResult called with requestCode " + requestCode + " and resultCode " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Log.d("MainActivity", "Image picked from gallery");
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    imageIndicator.setVisibility(View.GONE);
                    buttonBack.setVisibility(View.VISIBLE);
                    buttonConfirm.setVisibility(View.VISIBLE);

                    buttonImport.setVisibility(View.GONE);
                    buttonCapture.setVisibility(View.GONE);

                } catch (Exception e) {
                    Log.e("MainActivity", "Error getting bitmap from imageUri", e);
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.d("MainActivity", "Image captured from camera");
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    imageIndicator.setVisibility(View.GONE);
                    buttonBack.setVisibility(View.VISIBLE);
                    buttonConfirm.setVisibility(View.VISIBLE);

                    buttonImport.setVisibility(View.GONE);
                    buttonCapture.setVisibility(View.GONE);

                } catch (Exception e) {
                    Log.e("MainActivity", "Error getting bitmap from imageUri", e);
                }
            }
        }
    }
}
