package com.example.customview.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.customview.R

class CustomViewModel:ViewModel() {
    var color:MutableLiveData<Int> = MutableLiveData()
    var text:MutableLiveData<String> = MutableLiveData()
    init {
        color.value = R.color.black
        text.value = "Text"
    }

}