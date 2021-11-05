package com.example.customview.View.CustomView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import com.example.customview.R
import kotlinx.android.synthetic.main.custom_view.view.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mColor = 0
    private var editText: EditText
    private var viewColor: View
    private var isEnable: Boolean = false

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_view, this, true)
        editText = view.findViewById(R.id.cv_edit_text)
        viewColor = view.findViewById(R.id.cv_view)
        val typeArray = context.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.customView,
            0, 0
        )
        typeArray?.apply {
            try {
                isEnable = getBoolean(R.styleable.customView_enable_choose_color, false)
                if (!isEnable) {
                    cv_view.visibility = View.GONE
                }else{
                    cv_view.visibility = View.VISIBLE
                }
                mColor = getInt(R.styleable.customView_text_color, R.color.black)
                editText.setTextColor(mColor)
                viewColor.setBackgroundColor(mColor)
            } catch (e: Exception) {
                e.stackTrace
            } finally {
                recycle()
            }
        }
        setViewColor()
    }

    private fun setViewColor() {
        viewColor.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = AmbilWarnaDialog(context, mColor, object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {}
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                setColor(color)
            }
        })
        dialog.show()
    }

    fun setColor(color:Int){
        mColor = color
        editText.setTextColor(mColor)
        viewColor.setBackgroundColor(mColor)
        invalidate()
    }

    fun getColor():Int{
        return mColor
    }
}