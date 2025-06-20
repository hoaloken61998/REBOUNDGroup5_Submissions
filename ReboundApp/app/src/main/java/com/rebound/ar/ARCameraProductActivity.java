//package com.rebound.ar;
//
//import android.Manifest;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.*;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.Surface;
//import android.view.TextureView;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.LinearSnapHelper;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.SnapHelper;
//
//import com.rebound.R;
//import com.rebound.adapters.ProductARAdapter;
//import com.rebound.data.ProductData;
//import com.rebound.models.Cart.ProductItem;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.Arrays;
//import java.util.List;
//
//public class ARCameraProductActivity extends AppCompatActivity {
//
//    private static final int CAMERA_REQUEST_CODE = 101;
//    private TextureView textureView;
//    private CameraDevice cameraDevice;
//    private CameraCaptureSession captureSession;
//    private CaptureRequest.Builder captureRequestBuilder;
//    private CameraManager cameraManager;
//    private String cameraId;
//    private boolean isUsingBackCamera = true;
//
//    private RecyclerView recyclerViewProductAR;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_arcamera_product);
//
//        // Áp dụng padding tránh overlap với hệ thống
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Setup camera
//        textureView = findViewById(R.id.cameraPreview);
//        ImageView btnCapture = findViewById(R.id.btnCapture);
//        ImageView btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
//        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//
//        textureView.setSurfaceTextureListener(textureListener);
//        btnCapture.setOnClickListener(v -> captureImage());
//        btnSwitchCamera.setOnClickListener(v -> {
//            isUsingBackCamera = !isUsingBackCamera;
//            closeCamera();
//            openCamera();
//        });
//
//        // Setup RecyclerView sản phẩm
//        recyclerViewProductAR = findViewById(R.id.recyclerViewProductAR);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewProductAR.setLayoutManager(layoutManager);
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerViewProductAR);
//
//        List<ProductItem> productList = ProductData.getEarringList();
//        ProductARAdapter adapter = new ProductARAdapter(productList, this);
//        recyclerViewProductAR.setAdapter(adapter);
//    }
//
//    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            if (ActivityCompat.checkSelfPermission(ARCameraProductActivity.this, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ARCameraProductActivity.this,
//                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//                return;
//            }
//            openCamera();
//        }
//
//        @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
//        @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) { return false; }
//        @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
//    };
//
//    private void openCamera() {
//        try {
//            for (String id : cameraManager.getCameraIdList()) {
//                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
//                int lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
//                if ((isUsingBackCamera && lensFacing == CameraCharacteristics.LENS_FACING_BACK) ||
//                        (!isUsingBackCamera && lensFacing == CameraCharacteristics.LENS_FACING_FRONT)) {
//                    cameraId = id;
//                    break;
//                }
//            }
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//                return;
//            }
//            cameraManager.openCamera(cameraId, stateCallback, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(@NonNull CameraDevice camera) {
//            cameraDevice = camera;
//            startPreview();
//        }
//
//        @Override public void onDisconnected(@NonNull CameraDevice camera) { camera.close(); }
//        @Override public void onError(@NonNull CameraDevice camera, int error) {
//            camera.close(); cameraDevice = null;
//        }
//    };
//
//    private void startPreview() {
//        try {
//            SurfaceTexture texture = textureView.getSurfaceTexture();
//            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
//            Surface surface = new Surface(texture);
//
//            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            captureRequestBuilder.addTarget(surface);
//
//            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
//                @Override public void onConfigured(@NonNull CameraCaptureSession session) {
//                    captureSession = session;
//                    try {
//                        captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
//                    } catch (CameraAccessException e) { e.printStackTrace(); }
//                }
//                @Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {}
//            }, null);
//
//        } catch (CameraAccessException e) { e.printStackTrace(); }
//    }
//
//    private void captureImage() {
//        Bitmap bitmap = textureView.getBitmap();
//        if (bitmap != null) saveImage(bitmap);
//    }
//
//    private void saveImage(Bitmap bitmap) {
//        String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
//        OutputStream fos;
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
//                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
//
//                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                fos = getContentResolver().openOutputStream(imageUri);
//            } else {
//                File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                File image = new File(imagesDir, filename);
//                fos = new FileOutputStream(image);
//            }
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.close();
//            Toast.makeText(this, "Đã chụp ảnh!", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) { e.printStackTrace(); }
//    }
//
//    private void closeCamera() {
//        if (cameraDevice != null) {
//            cameraDevice.close();
//            cameraDevice = null;
//        }
//        if (captureSession != null) {
//            captureSession.close();
//            captureSession = null;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            openCamera();
//        } else {
//            Toast.makeText(this, "Cần quyền camera để sử dụng tính năng này!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        closeCamera();
//    }
//}
