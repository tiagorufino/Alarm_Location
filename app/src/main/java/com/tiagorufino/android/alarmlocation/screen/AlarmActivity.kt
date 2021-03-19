package com.tiagorufino.android.alarmlocation.screen

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.tiagorufino.android.alarmlocation.R
import com.tiagorufino.android.alarmlocation.main.MainActivity


class AlarmActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(R.string.alarm_screen.toString(), getString(R.string.call_oncreate))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        stopMusic()
        showGif()
    }

    /**
     * Control the back button
     */
    override fun onBackPressed() {
        Log.i(R.string.alarm_screen.toString(), getString(R.string.call_onback))

        callMainScreen(null)
        super.onBackPressed()
    }

    /**
     * Executade the gif, the music and the vibration
     */
    private fun showGif() {
        Log.i(R.string.alarm_screen.toString(), getString(R.string.call_show_gif))

        val imageView: ImageView = findViewById(R.id.alarm_icon)
        Glide.with(this).load(R.drawable.ic_bell_gif).into(imageView)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.isLooping = true
        mediaPlayer.start() // no need to call prepare(); create() does that for you
    }

    /**
     * Cancel the music and send to the main screen
     */
    fun callMainScreen(view: View?){
        Log.i(R.string.alarm_screen.toString(), getString(R.string.call_main_screen))

        mediaPlayer.stop()
        val intent = Intent(this, MainActivity::class.java).apply {  }
        startActivity(intent)
    }

    fun stopMusic(){
        val mAudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (mAudioManager.isMusicActive) {
            val i = Intent("com.android.music.musicservicecommand")
            i.putExtra("command", "pause")
            this.sendBroadcast(i)
        }

    }
}