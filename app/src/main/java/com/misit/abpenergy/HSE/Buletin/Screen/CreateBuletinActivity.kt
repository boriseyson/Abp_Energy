package com.misit.abpenergy.HSE.Buletin.Screen

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.misit.abpenergy.R
import com.misit.abpenergy.Utils.Constants

class CreateBuletinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title="Buletin"
        setContentView(R.layout.activity_create_buletin)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
        super.onBackPressed()
    }
}