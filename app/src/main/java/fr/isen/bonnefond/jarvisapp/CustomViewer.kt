package fr.isen.bonnefond.jarvisapp

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Matrix
import android.os.Handler
import android.view.*
import com.google.android.filament.Skybox
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
    private lateinit var surfaceView: SurfaceView

    private lateinit var cameraManipulator: Manipulator

    var angleX = 0f
    var angleY = 0f

    fun loadEntity() {
        choreographer = Choreographer.getInstance()
    }

    @SuppressLint("ClickableViewAccessibility")
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
    fun rotateLeft() {
        modelViewer.apply {
            val currentAngle = angleY
            val targetAngle = angleY + 90f
            val animator = ValueAnimator.ofFloat(currentAngle, targetAngle)
            animator.duration = 700 // durée en millisecondes
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                angleY = value
                val rotation = rotation(Float3(0f, 1f, 0f), value)
                val tm = modelViewer.engine.transformManager
                tm.setTransform(tm.getInstance(asset!!.root), rotation.toFloatArray())

                val transform = tm.getInstance(asset!!.root)

                val currentTransform = FloatArray(16)
                tm.getTransform(transform, currentTransform)

                val translation = FloatArray(16)
                Matrix.setIdentityM(translation, 0)
                Matrix.translateM(translation, 0, 0.04f, -0.1825f, 0.08f) // Translation de 0.2 unité vers le bas

                val newTransform = FloatArray(16)
                Matrix.multiplyMM(newTransform, 0, currentTransform, 0, translation, 0)
                tm.setTransform(transform, newTransform)
            }
            animator.start()
        }
    }

    fun rotateRight() {
        modelViewer.apply {
            val currentAngle = angleY
            val targetAngle = angleY - 90f
            val animator = ValueAnimator.ofFloat(currentAngle, targetAngle)
            animator.duration = 700 // durée en millisecondes
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                angleY = value
                val rotation = rotation(Float3(0f, 1f, 0f), value)
                val tm = modelViewer.engine.transformManager
                tm.setTransform(tm.getInstance(asset!!.root), rotation.toFloatArray())

                val transform = tm.getInstance(asset!!.root)

                val currentTransform = FloatArray(16)
                tm.getTransform(transform, currentTransform)

                val translation = FloatArray(16)
                Matrix.setIdentityM(translation, 0)
                Matrix.translateM(translation, 0, 0.04f, -0.1825f, 0.08f) // Translation de 0.2 unité vers le bas

                val newTransform = FloatArray(16)
                Matrix.multiplyMM(newTransform, 0, currentTransform, 0, translation, 0)
                tm.setTransform(transform, newTransform)
            }
            animator.start()
        }
    }

    fun rotateUp() {
        modelViewer.apply {
            val currentAngle = angleX
            val targetAngle = angleX + 90f
            val animator = ValueAnimator.ofFloat(currentAngle, targetAngle)
            animator.duration = 700 // durée en millisecondes
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                angleX = value
                val rotation = rotation(Float3(1f, 0f, 0f), value)
                val tm = modelViewer.engine.transformManager
                tm.setTransform(tm.getInstance(asset!!.root), rotation.toFloatArray())

                val transform = tm.getInstance(asset!!.root)

                val currentTransform = FloatArray(16)
                tm.getTransform(transform, currentTransform)

                val translation = FloatArray(16)
                Matrix.setIdentityM(translation, 0)
                Matrix.translateM(translation, 0, 0.04f, -0.1825f, 0.08f) // Translation de 0.2 unité vers le bas

                val newTransform = FloatArray(16)
                Matrix.multiplyMM(newTransform, 0, currentTransform, 0, translation, 0)
                tm.setTransform(transform, newTransform)
            }
            animator.start()
        }
    }

    fun rotateDown() {
        modelViewer.apply {
            val currentAngle = angleX
            val targetAngle = angleX - 90f
            val animator = ValueAnimator.ofFloat(currentAngle, targetAngle)
            animator.duration = 700 // durée en millisecondes
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                angleX = value
                val rotation = rotation(Float3(1f, 0f, 0f), value)
                val tm = modelViewer.engine.transformManager
                tm.setTransform(tm.getInstance(asset!!.root), rotation.toFloatArray())

                val transform = tm.getInstance(asset!!.root)

                val currentTransform = FloatArray(16)
                tm.getTransform(transform, currentTransform)

                val translation = FloatArray(16)
                Matrix.setIdentityM(translation, 0)
                Matrix.translateM(translation, 0, 0.04f, -0.1825f, 0.08f) // Translation de 0.2 unité vers le bas

                val newTransform = FloatArray(16)
                Matrix.multiplyMM(newTransform, 0, currentTransform, 0, translation, 0)
                tm.setTransform(transform, newTransform)
            }
            animator.start()
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