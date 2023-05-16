package fr.isen.bonnefond.jarvisapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class DemoActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var customViewer: CustomViewer

    @SuppressLint("MissingInflatedId", "NewApi", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        surfaceView = findViewById<View>(R.id.surface_view) as SurfaceView
        customViewer = CustomViewer().apply {
            loadEntity()
            setSurfaceView(surfaceView)
            loadGlb(this@DemoActivity, "grogu", "grogu")
            loadIndirectLight(this@DemoActivity, "venetian_crossroads_2k")

        }
        val rotateDirection = intent.getStringExtra("ROTATE_DIRECTION")
        if (rotateDirection == "RIGHT") {
            customViewer.rotateRight()
        }
    }

    override fun onResume() {
        super.onResume()
        customViewer.onResume()
    }

    override fun onPause() {
        super.onPause()
        customViewer.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        customViewer.onDestroy()
    }
}