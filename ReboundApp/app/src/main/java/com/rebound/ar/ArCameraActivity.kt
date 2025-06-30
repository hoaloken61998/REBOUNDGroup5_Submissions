package com.rebound.ar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.handlandmarker.HandLandmarkerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.rebound.R
import com.rebound.adapters.ProductARAdapter
import com.rebound.callback.FirebaseListCallback
import com.rebound.connectors.FirebaseProductConnector
import com.rebound.main.NavBarActivity
import com.rebound.models.Cart.ProductItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.sqrt

class ArCameraActivity : AppCompatActivity(), HandLandmarkerHelper.LandmarkerListener {

    // --- Variables for CameraX, Hand Tracking, and 3D Rendering ---
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalysis? = null
    private var modelDisplayFragment: ModelDisplayFragment? = null
    private lateinit var fragmentContainer: FrameLayout
    private var currentLensFacing = CameraSelector.LENS_FACING_FRONT
    private var currentModelName = "21.glb" // Default model
    private var isCameraProviderInitialized = false

    // --- UI and Business Logic Variables ---
    private lateinit var recyclerProductAR: RecyclerView
    private lateinit var productARAdapter: ProductARAdapter
    private var currentProductList: MutableList<ProductItem> = ArrayList()
    private lateinit var categoryIconsLayout: LinearLayout
    private lateinit var btnSwitchCamera: ImageButton

    // Scaling and positioning constants
    companion object {
        private const val TAG = "ArCameraActivity"
        private const val UNIFIED_SCALE_FACTOR_FRONT = 25.0f
        private const val UNIFIED_SCALE_FACTOR_BACK = 40.0f
        private const val RING_FINGER_DIAMETER_RATIO = 0.22f
        private const val Z_MODULATION_SENSITIVITY = -0.1f
        private const val POSITION_SCALE_FACTOR_FRONT = 0.4f
        private const val POSITION_SCALE_FACTOR_BACK = 0.6f
        private const val Y_OFFSET_FRONT = -0.25f
        private const val Y_OFFSET_BACK = 0f
        private const val DEPTH_SCALE_FACTOR = -0.1f
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                ensureCameraIsSetupAndRunning()
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ar_camera) // This layout MUST have a PreviewView and FrameLayout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeUI()
        initializeArComponents()
        setupListeners()
        setupProductRecyclerView()
        requestCameraPermission()
    }

    private fun initializeUI() {
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera)
        categoryIconsLayout = findViewById(R.id.categoryIconsLayout)
        recyclerProductAR = findViewById(R.id.recyclerProductAR)
        fragmentContainer = findViewById(R.id.fragment_container) // From modified layout
    }

    private fun initializeArComponents() {
        // Setup the fragment that will display the 3D model
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            modelDisplayFragment = ModelDisplayFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, modelDisplayFragment!!)
                .commitNow()
        } else {
            modelDisplayFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? ModelDisplayFragment
        }

        // Setup the hand landmarker
        cameraExecutor = Executors.newSingleThreadExecutor()
        handLandmarkerHelper = HandLandmarkerHelper(
            context = this,
            runningMode = RunningMode.LIVE_STREAM,
            minHandDetectionConfidence = 0.5f,
            minHandTrackingConfidence = 0.5f,
            minHandPresenceConfidence = 0.5f,
            maxNumHands = 1,
            currentDelegate = HandLandmarkerHelper.DELEGATE_GPU,
            handLandmarkerHelperListener = this
        )
    }

    private fun setupListeners() {
        btnSwitchCamera.setOnClickListener { switchCamera() }

        findViewById<ImageView>(R.id.btnCloseArTop).setOnClickListener {
            val intent = Intent(this@ArCameraActivity, NavBarActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.btnCloseAR).setOnClickListener {
            recyclerProductAR.visibility = View.GONE
            categoryIconsLayout.visibility = View.VISIBLE
        }

        // --- Category Selection Logic as per your request ---
        // Find category icons
        val imgARNecklaces: ImageView = findViewById(R.id.imgARNecklaces)
        val imgAREarrings: ImageView = findViewById(R.id.imgAREarrings)
        val imgARRings: ImageView = findViewById(R.id.imgARRings)
        val imgARBodyPiercing: ImageView = findViewById(R.id.imgARBodyPiercing)

        // Only allow Rings, others show Toast
        imgARRings.setOnClickListener {
            // Fetch products from Firebase and filter for ProductID 21, 23, 27
            FirebaseProductConnector.getAllProducts(
                "Product",
                ProductItem::class.java,
                object : FirebaseListCallback<ProductItem> {
                    override fun onSuccess(productList: ArrayList<ProductItem>) {
                        val filteredProducts = productList.filter {
                            it.getProductID() == 21L || it.getProductID() == 23L || it.getProductID() == 27L
                        } //
                        showProducts(filteredProducts) //
                    }
                    override fun onFailure(errorMessage: String?) {
                        Toast.makeText(this@ArCameraActivity, "Failed to load products", Toast.LENGTH_SHORT).show() //
                    }
                }
            )
        }
        val unsupportedCategoryListener = View.OnClickListener {
            Toast.makeText(this, "This Category is not supported", Toast.LENGTH_SHORT).show() //
        }
        imgARNecklaces.setOnClickListener(unsupportedCategoryListener) //
        imgAREarrings.setOnClickListener(unsupportedCategoryListener) //
        imgARBodyPiercing.setOnClickListener(unsupportedCategoryListener) //
    }

    private fun setupProductRecyclerView() {
        productARAdapter = ProductARAdapter(currentProductList, this) { product ->
            val modelName = when (product.getProductID()) {
                21L -> "21.glb"
                23L -> "23.glb"
                27L -> "27.glb"
                else -> null
            }
            modelName?.let {
                loadModel(it)
                Toast.makeText(this, "Loading ${product.getProductName()}", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerProductAR.apply {
            layoutManager = LinearLayoutManager(this@ArCameraActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = productARAdapter
        }
    }

    private fun showProducts(products: List<ProductItem>) {
        categoryIconsLayout.visibility = View.GONE
        recyclerProductAR.visibility = View.VISIBLE
        currentProductList.clear()
        currentProductList.addAll(products)
        productARAdapter.notifyDataSetChanged()
    }

    private fun loadModel(modelName: String) {
        currentModelName = modelName
        fragmentContainer.visibility = View.GONE
        modelDisplayFragment?.reinitializeFilamentBridgeAndLoadModel(currentModelName) {
            Log.d(TAG, "Filament reinitialization complete for model $currentModelName.")
            bindCameraUseCases()
        }
    }

    private fun switchCamera() {
        fragmentContainer.visibility = View.GONE
        modelDisplayFragment?.reinitializeFilamentBridgeAndLoadModel(currentModelName) {
            currentLensFacing = if (currentLensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            bindCameraUseCases()
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                setupCameraProvider()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(this)
                    .setTitle("Camera Permission Required")
                    .setMessage("This feature requires camera access to show a live preview.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .create().show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun ensureCameraIsSetupAndRunning() {
        if (handLandmarkerHelper.isClose()) {
            handLandmarkerHelper.setupHandLandmarker()
        }
        if (!isCameraProviderInitialized && cameraProvider == null) {
            setupCameraProvider()
        } else if (cameraProvider != null) {
            bindCameraUseCases()
        }
    }

    private fun setupCameraProvider() {
        if (isCameraProviderInitialized) return
        isCameraProviderInitialized = true

        ProcessCameraProvider.getInstance(this).addListener({
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get camera provider", e)
                isCameraProviderInitialized = false
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        val previewView = findViewById<androidx.camera.view.PreviewView>(R.id.preview_view)
        previewView.implementationMode = androidx.camera.view.PreviewView.ImplementationMode.COMPATIBLE

        val cameraSelector = CameraSelector.Builder().requireLensFacing(currentLensFacing).build()

        val preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetRotation(previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy -> detectHand(imageProxy) }
            }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectHand(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = (currentLensFacing == CameraSelector.LENS_FACING_FRONT)
        )
    }

    override fun onResults(resultBundle: HandLandmarkerHelper.ResultBundle) {
        runOnUiThread {
            if (resultBundle.results.isNotEmpty() && resultBundle.results.first().landmarks().isNotEmpty()) {
                fragmentContainer.visibility = View.VISIBLE
                // --- Transformation logic remains the same as MainActivity ---
                // (Omitted for brevity, but it is the same as the previous response)
            } else {
                fragmentContainer.visibility = View.GONE
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        runOnUiThread {
            Log.e(TAG, "HandLandmarkerHelper Error: $error (Code: $errorCode)")
            Toast.makeText(this, "MediaPipe Error: $error", Toast.LENGTH_SHORT).show()
            fragmentContainer.visibility = View.GONE
        }
    }

    // --- Lifecycle, Matrix Calculation, and Helper methods ---
    // (Omitted for brevity, but they are the same as the previous response)

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            ensureCameraIsSetupAndRunning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        handLandmarkerHelper.clearHandLandmarker()
        cameraProvider?.unbindAll()
    }

    // NOTE: All matrix calculation methods (`calculateTransformMatrix`, `calculateTransformMatrixFor21`, etc.)
    // and the vector math helpers (`dot`, `normalize`, `cross`) must be included here for the code to work.
    // They are identical to the versions in previous responses.
}