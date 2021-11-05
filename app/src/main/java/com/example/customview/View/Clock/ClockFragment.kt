package com.example.customview.View.Clock

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.customview.R
import kotlinx.android.synthetic.main.fragment_clock.*

class ClockFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clock, container, false)
        return view
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clock_btnChange.setOnClickListener {
            val style = AlertDialog.THEME_HOLO_DARK
            val timePickerDialog = TimePickerDialog(
                context,
                style,
                { _, hour, minute ->
                    clock_clockView.changeTime(minute,hour)
                }, 7, 0, true
            )
            timePickerDialog.show()
        }
    }
}