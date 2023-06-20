package com.soundmeter.application.view.webview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.orhanobut.hawk.Hawk
import com.soundmeter.application.R
import com.soundmeter.application.databinding.ActivityWebViewBinding
import com.soundmeter.application.utils.WEB_URL

class WebViewActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWebViewBinding
    private var isFirstLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
            build()
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Hawk.put(WEB_URL, remoteConfig.getString(WEB_URL))
                    loadWeb()
                } else {
                    loadWeb()
                }
            }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.swipeRefresh.setOnRefreshListener {
            loadWeb()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstLoad) loadWeb()
    }

    private fun loadWeb() {
        with(binding) {
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                    isFirstLoad = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                }
            }
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(Hawk.get(WEB_URL))
        }
    }
}