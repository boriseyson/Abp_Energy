package com.misit.abpenergy.Model

import androidx.lifecycle.ViewModel

class ApproveModel :ViewModel(){
    var approve = false
    override fun onCleared() {
        super.onCleared()
    }
    fun updateStatus(appr:Boolean){
        approve=appr
    }
}