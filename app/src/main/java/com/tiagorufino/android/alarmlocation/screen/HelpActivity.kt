package com.tiagorufino.android.alarmlocation.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tiagorufino.android.alarmlocation.R
import com.tiagorufino.android.alarmlocation.main.MainActivity

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(R.string.help_screen.toString(),getString(R.string.call_oncreate))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    /**
     * Call main screen
     */
    fun callMainScreen(view: View) {
        Log.i(R.string.help_screen.toString(),getString(R.string.call_main_screen))

        val intent = Intent(this, MainActivity::class.java).apply {  }
        startActivity(intent)
    }

}