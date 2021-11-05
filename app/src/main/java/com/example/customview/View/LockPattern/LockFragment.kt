package com.example.customview.View.LockPattern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.customview.R
import com.example.customview.ViewModel.LockViewModel
import kotlinx.android.synthetic.main.fragment_lock.*

class LockFragment: Fragment() {
    private var lockViewModel:LockViewModel?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lock,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockViewModel = ViewModelProviders.of(this).get(LockViewModel::class.java)
        lockViewModel!!.readSharePre(context)
        lockViewModel!!.textOfButton.observe(viewLifecycleOwner, Observer {
            lock_btnCheck.text = it
        })
        lock_btnCheck.setOnClickListener {
            var list = lock_plvLock.getPassword()
            lockViewModel!!.saveOrCheck(context,list)
            lock_plvLock.reset()
        }
        lockViewModel!!.textOfResult.observe(viewLifecycleOwner, Observer {
            lock_tvResult.text = it
        })
    }
}