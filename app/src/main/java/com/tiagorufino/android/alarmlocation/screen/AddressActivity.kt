package com.tiagorufino.android.alarmlocation.screen

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tiagorufino.android.alarmlocation.R


class AddressActivity : AppCompatActivity() {

    private lateinit var addressClient: String
    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(R.string.address_screen.toString(), getString(R.string.call_oncreate))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.maps_key))
        }
        editText = findViewById(R.id.address_text)

        //hidden the keyboard
        editText.imeOptions = EditorInfo.IME_ACTION_DONE;

        editText.setOnClickListener{
            val fieldList: List<Place.Field> = arrayListOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(this)
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK){
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)
            editText.setText(place.address)
        } else {
            callToast(getString(R.string.address_not_found))
        }
    }

    /**
     * Call the cancel screen
     */
    fun callCancelScreen(view: View) {
        Log.i(R.string.address_screen.toString(), getString(R.string.call_cancel_screen))

        val intent = Intent(this, CancelActivity::class.java).apply { }
        startActivityForResult(intent, 100)
    }

    /**
     * Call the map screen passing the location through the intent
     */
    @SuppressWarnings
    fun callMapScreen(view: View) {
        Log.i(R.string.address_screen.toString(), getString(R.string.call_maps_screen))

        addressClient = findViewById<EditText>(R.id.address_text).text.toString()

        if(addressClient.isBlank() || addressClient.isEmpty()){
            callToast(getString(R.string.blank_address))
        } else {
            val geo = Geocoder(this)
            val addresses = geo.getFromLocationName(addressClient, 5)
            if (null == addresses || addresses.isEmpty()) {
                callToast(getString(R.string.address_not_found))
            } else {
                val intent = Intent(this, MapsActivity::class.java).apply { }
                intent.putExtra("addressClient", addressClient)
                startActivity(intent)
            }
        }
    }

    /**
     * Call toast
     */
    private fun callToast(msg: String){
        val toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }
}