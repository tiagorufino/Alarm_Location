package com.tiagorufino.android.alarmlocation.main

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tiagorufino.android.alarmlocation.R
import com.tiagorufino.android.alarmlocation.screen.AddressActivity
import com.tiagorufino.android.alarmlocation.screen.HelpActivity

class MainActivity : AppCompatActivity() {

    val staticHeigth: Int = 1280
    val staticWidth: Int = 768

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val heigth = Resources.getSystem().displayMetrics.heightPixels
        val width = Resources.getSystem().displayMetrics.widthPixels
        //have to been done
        comparatorDimensions(heigth, width)
    }

    /**
     * Call back the main activity
     */
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java).apply {  }
        startActivity(intent)
        super.onBackPressed()
    }

    /**
     * Check the dimmensions of the devices and adapted for mostly of them
     */
    private fun comparatorDimensions(heigth: Int, width: Int) {

        if(staticHeigth > heigth && staticWidth > width){
            //R.integer.size = 260

        } else {
            //R.integer.size = 120
        }
    }

    /**
     * Call the help activity
     */
    fun callHelpScreen(view: View) {
        val intent = Intent(this, HelpActivity::class.java).apply {  }
        startActivity(intent)
    }

    /**
     * CAll the address activity
     */
    fun callAddressScreen(view: View) {
        val intent = Intent(this, AddressActivity::class.java).apply {  }
        startActivity(intent)
    }
}