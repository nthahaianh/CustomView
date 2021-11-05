package com.example.customview.View.CustomView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.customview.R
import com.example.customview.ViewModel.CustomViewModel
import kotlinx.android.synthetic.main.fragment_custom_view.*
import yuku.ambilwarna.AmbilWarnaDialog

class CustomViewFragment: Fragment() {
    var customViewModel:CustomViewModel?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_custom_view,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customViewModel = ViewModelProviders.of(this).get(CustomViewModel::class.java)
        customViewModel!!.color.value = frg_cv2.getColor()
        customViewModel!!.color.observe(viewLifecycleOwner, Observer {
            frg_cv1.setColor(it)
            frg_cv2.setColor(it)
            frg_cv3.setColor(it)
        })
        frg_cv2.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = AmbilWarnaDialog(context, frg_cv2.getColor(), object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {}
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                customViewModel!!.color.value = color
            }
        })
        dialog.show()
    }
}