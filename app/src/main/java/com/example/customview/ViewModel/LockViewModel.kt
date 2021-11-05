package com.example.customview.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appmusicmvvm.SQLite.SQLHelper
import com.example.customview.Model.Dot

class LockViewModel:ViewModel() {
    var textOfButton:MutableLiveData<String> = MutableLiveData()
    var textOfResult:MutableLiveData<String> = MutableLiveData()
    private var havePass:Boolean
    init {
        textOfButton.value = "Check password"
        textOfButton.value = ""
        havePass = false
    }

    fun readSharePre(context: Context?) {
        context?.let {
            val sharedPreferences = context.getSharedPreferences("SharePreferences", Context.MODE_PRIVATE)
            havePass = sharedPreferences.getBoolean("isPassword",false)
            if (havePass){
                textOfButton.value = "Check password"
            }else{
                textOfButton.value = "Set pass"
            }
        }
    }

    fun saveOrCheck(context: Context?,list:MutableList<Dot>){
        if (havePass){
            checkPassword(context,list)
        }else{
            savePassword(context,list)
        }
    }

    private fun savePassword(context: Context?, list:MutableList<Dot>){
        Log.e("save","----------------------------------------------------------")
        context?.let {
            var sqlHelper = SQLHelper(it)
            try {
                sqlHelper.removeAll()
                for (i in list) {
                    sqlHelper.addKey(i.key.toString())
                }
                havePass = true
                val sharedPreferences = it.getSharedPreferences("SharePreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isPassword",true)
                editor.apply()
                textOfButton.value = "Check password"
                textOfResult.value = "Saved password"
            } catch (e: Exception) {
                e.stackTrace
                Log.e("SQL", "Read SQL error")
            }
        }
    }

    private fun checkPassword(context: Context?, list:MutableList<Dot>){
        Log.e("check","----------------------------------------------------------")
        var sqlHelper = SQLHelper(context)
        try {
            val sqlList = sqlHelper.getAll()
            var listPass:MutableList<String> = mutableListOf()
            for (item in list){
                listPass.add(item.key.toString())
            }
            if (listPass==sqlList){
                textOfResult.value = "Password is correct"
            }else{
                textOfResult.value = "Password is not correct"
            }
        } catch (e: Exception) {
            e.stackTrace
            Log.e("SQL", "Read SQL error")
        }
    }
}