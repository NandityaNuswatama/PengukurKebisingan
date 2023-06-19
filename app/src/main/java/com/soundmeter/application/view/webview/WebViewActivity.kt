package com.soundmeter.application.view.webview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.soundmeter.application.R
import com.soundmeter.application.databinding.ActivityRecordingBinding
import com.soundmeter.application.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    companion object{
        fun start(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.swipeRefresh.setOnRefreshListener {
            loadWeb()
        }
    }

    override fun onResume() {
        super.onResume()
        loadWeb()
    }

    private fun loadWeb() {
        val url = "https://www.pengukurkebisingan.com/"
        with(binding) {
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                }
            }
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    }
}