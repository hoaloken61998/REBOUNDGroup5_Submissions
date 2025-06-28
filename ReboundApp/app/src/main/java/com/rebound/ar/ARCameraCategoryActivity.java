package com.rebound.ar;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;
import com.rebound.main.NavBarActivity;
import com.rebound.models.Cart.ProductItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;

import com.rebound.adapters.ProductARAdapter;


public class ARCameraCategoryActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isUsingBackCamera = true;

    private RecyclerView recyclerProductAR;
    private ProductARAdapter productARAdapter;
    private List<ProductItem> currentProductList = new ArrayList<>();
    private LinearLayout categoryIconsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_arcamera_category);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textureView = findViewById(R.id.cameraPreview);
        ImageView btnCapture = findViewById(R.id.btnCapture);
        ImageView btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        ImageView btnCloseAR = findViewById(R.id.btnCloseAR);

        categoryIconsLayout = findViewById(R.id.categoryIconsLayout);
        recyclerProductAR = findViewById(R.id.recyclerProductAR);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        textureView.setSurfaceTextureListener(textureListener);

        btnCapture.setOnClickListener(v -> captureImage());

        btnSwitchCamera.setOnClickListener(v -> {
            isUsingBackCamera = !isUsingBackCamera;
            closeCamera();
            openCamera();
        });

        btnCloseAR.setOnClickListener(v -> {
            recyclerProductAR.setVisibility(View.GONE);
            categoryIconsLayout.setVisibility(View.VISIBLE);
        });

        recyclerProductAR.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productARAdapter = new ProductARAdapter(currentProductList, this);
        recyclerProductAR.setAdapter(productARAdapter);

        int paddingHorizontal = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics()
        );

        int paddingTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                50,
                getResources().getDisplayMetrics()
        );

// Áp padding: trái, trên, phải, dưới
        recyclerProductAR.setPadding(paddingHorizontal, paddingTop, paddingHorizontal, 10);


        ImageView btnClose = findViewById(R.id.btnCloseArTop);
        btnClose.setOnClickListener(v -> {
            Intent intent = new Intent(ARCameraCategoryActivity.this, NavBarActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (ActivityCompat.checkSelfPermission(ARCameraCategoryActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ARCameraCategoryActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return;
            }
            openCamera();
        }

        @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
        @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) { return false; }
        @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };

    private void openCamera() {
        try {
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                int lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if ((isUsingBackCamera && lensFacing == CameraCharacteristics.LENS_FACING_BACK)
                        || (!isUsingBackCamera && lensFacing == CameraCharacteristics.LENS_FACING_FRONT)) {
                    cameraId = id;
                    break;
                }
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override public void onDisconnected(@NonNull CameraDevice camera) { camera.close(); }
        @Override public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    private void startPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        Bitmap bitmap = textureView.getBitmap();
        if (bitmap != null) {
            saveImage(bitmap);
        }
    }

    private void saveImage(Bitmap bitmap) {
        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image = new File(imagesDir, filename);
                fos = new FileOutputStream(image);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Toast.makeText(this, getString(R.string.message_capture_success), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, getString(R.string.message_camera_permission_required), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
    }

    private void showProducts(List<ProductItem> products) {
        categoryIconsLayout.setVisibility(View.GONE);
        recyclerProductAR.setVisibility(View.VISIBLE);
        currentProductList.clear();
        currentProductList.addAll(products);
        productARAdapter.notifyDataSetChanged();
    }
}