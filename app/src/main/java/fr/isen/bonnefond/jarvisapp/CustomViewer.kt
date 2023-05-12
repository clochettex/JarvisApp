package fr.isen.bonnefond.jarvisapp

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Matrix
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.*
import androidx.core.graphics.rotationMatrix
import androidx.core.graphics.values
import com.google.android.filament.Skybox
import com.google.android.filament.TransformManager
import com.google.android.filament.utils.*
import java.nio.ByteBuffer

class CustomViewer {

    companion object {
        init {
            Utils.init()
        }
    }

    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer

    private lateinit var cameraManipulator: Manipulator

    private var angle = 0f

    fun loadEntity() {
        choreographer = Choreographer.getInstance()
    }

    fun setSurfaceView(mSurfaceView: SurfaceView) {
        modelViewer = ModelViewer(mSurfaceView)
        mSurfaceView.setOnTouchListener(modelViewer)

        modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)
        modelViewer.scene.skybox?.setColor(0f, 0f, 0f, 0f)

        val kDefaultObjectPosition = Float3(0.0f, 0.0f, -4.0f)
        cameraManipulator = Manipulator.Builder()
            .targetPosition(kDefaultObjectPosition.x, kDefaultObjectPosition.y, kDefaultObjectPosition.z)
            .viewport(mSurfaceView.width, mSurfaceView.height)
            .build(Manipulator.Mode.ORBIT)
    }

    fun loadGlb(context: Context, dirName: String, name: String) {
        val buffer = readAsset(context, "models/${dirName}/${name}.glb")
        modelViewer.apply {
            loadModelGlb(buffer)
            transformToUnitCube()
        }
    }


    fun loadIndirectLight(context: Context, ibl: String) {
        // Create the indirect light source and add it to the scene.
        val buffer = readAsset(context, "environments/venetian_crossroads_2k/${ibl}_ibl.ktx")
        KTXLoader.createIndirectLight(modelViewer.engine, buffer).apply {
            intensity = 50_000f
            modelViewer.scene.indirectLight = this
        }
    }

    fun rotate() {
        modelViewer.apply {
            angle += 90f

            val rotation = rotation(Float3(0f, 1f, 0f), angle)
            val translation = translation(Float3(0f, 0f, 0f))


            val tm = modelViewer.engine.transformManager

            tm.setTransform(tm.getInstance(asset!!.root), transpose(rotation).toFloatArray())
            //tm.setTransform(tm.getInstance(asset!!.root), translation.toFloatArray())
        }
    }

    fun rotate2() {
        modelViewer.apply {
            angle -= 90f

            val rotation = rotation(Float3(0f, 1f, 0f), angle)
            val translation = translation(Float3(0f, 0f, 0f))


            val tm = modelViewer.engine.transformManager

            tm.setTransform(tm.getInstance(asset!!.root), transpose(rotation).toFloatArray())
            //tm.setTransform(tm.getInstance(asset!!.root), translation.toFloatArray())
        }
    }


    private fun readAsset(context: Context, assetName: String): ByteBuffer {
        val input = context.assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewer.render(currentTime)
        }
    }


    fun onResume() {
        choreographer.postFrameCallback(frameCallback)
    }

    fun onPause() {
        choreographer.removeFrameCallback(frameCallback)
    }

    fun onDestroy() {
        choreographer.removeFrameCallback(frameCallback)
    }
}