package com.example.customview.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnimationViewModel:ViewModel() {
    var isLeft:MutableLiveData<Boolean> = MutableLiveData()
    var isDisplay:MutableLiveData<Boolean> = MutableLiveData()

    init {
        isLeft.value = true
        isDisplay.value = true
    }
}