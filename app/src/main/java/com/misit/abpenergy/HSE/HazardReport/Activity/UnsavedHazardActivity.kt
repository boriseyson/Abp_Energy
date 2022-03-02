package com.misit.abpenergy.HSE.HazardReport.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misit.abpenergy.HSE.HazardReport.Response.ItemHazardList
import com.misit.abpenergy.HSE.HazardReport.ViewModel.HazardDetailViewModel
import com.misit.abpenergy.HSE.HazardReport.ViewModel.UnsavedViewModel
import com.misit.abpenergy.R

class UnsavedHazardActivity : AppCompatActivity() {
    lateinit var viewModel:UnsavedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsaved_hazard)
        viewModel = ViewModelProvider(this@UnsavedHazardActivity).get(UnsavedViewModel::class.java)
        loadViewModel()
    }
    private fun loadViewModel(){
        viewModel.hazardUnsave().observe(this@UnsavedHazardActivity,{
            var listHazard= it
            Log.d("ItemHazard","${it}")
        })
        viewModel.loadUnsaved(this@UnsavedHazardActivity)
    }
}