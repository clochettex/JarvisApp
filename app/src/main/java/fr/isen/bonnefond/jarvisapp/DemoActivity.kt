package fr.isen.bonnefond.jarvisapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DemoActivity : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var customViewer: CustomViewer

    @SuppressLint("MissingInflatedId", "NewApi")
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

        val button = findViewById<Button>(R.id.button) as Button
        button.setOnClickListener {
            customViewer.rotate()
        }

        val button2 = findViewById<Button>(R.id.button2) as Button
        button2.setOnClickListener {
            customViewer.rotate2()
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