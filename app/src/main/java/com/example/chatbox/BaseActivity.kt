package com.example.chatbox

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory

open class BaseActivity : AppCompatActivity() {

    private lateinit var loading: FrameLayout

    fun showDefaultLoading(view: ViewGroup? = null) {
        if (this::loading.isInitialized.not()) {
            loading = FrameLayout(this)
            loading.tag = "LOADING"
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            loading.layoutParams = layoutParams
            loading.isClickable = true

            val loadingView: View =
                LayoutInflater.from(this).inflate(R.layout.loading_view, view, true)

            (this.window.decorView.rootView as ViewGroup).addView(loading)

            val compositionFactory =
                LottieCompositionFactory.fromAsset(this, "app_animation_loading.json")

            compositionFactory.addListener { lottieAnimation ->
                loadingView.findViewById<LottieAnimationView>(R.id.loading_view).setComposition(lottieAnimation)
            }

            compositionFactory.addFailureListener {

            }

            loading.addView(loadingView)

            loading.visibility = View.VISIBLE
        }

        loading.visibility = View.VISIBLE
    }

    fun hideDefaultLoading() {
        if (this::loading.isInitialized) {
            loading.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
    }

    fun setStatusBar() {
        // to make the status bar transparent
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
        // to change status bar title color
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}