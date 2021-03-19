package com.tiagorufino.android.alarmlocation.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tiagorufino.android.alarmlocation.R
import com.tiagorufino.android.alarmlocation.main.MainActivity

class CancelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(R.string.cancel_screen.toString(), getString(R.string.call_oncreate))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel)
    }

    /**
     * CAll the main screen when finish
     */
    fun callMainScreen(view: View) {
        Log.i(R.string.cancel_screen.toString(), getString(R.string.call_main_screen))

        val intent = Intent(this, MainActivity::class.java).apply { }
        startActivity(intent)
    }
}