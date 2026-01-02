package com.iwatdigital.dropdroid.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val target = Intent(this, MainActivity::class.java).apply {
            action = intent?.action
            putExtra(Intent.EXTRA_STREAM, intent?.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
            putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                intent?.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
            )
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(target)
        finish()
    }
}
