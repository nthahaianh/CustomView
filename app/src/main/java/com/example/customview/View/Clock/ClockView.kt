package com.example.customview.View.Clock

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.*
import com.example.customview.R
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_WIDTH = 200 // default width
    }
    private lateinit var mBlackPaint: Paint // black brush
    private lateinit var mRedPaint: Paint // red brush
    private lateinit var mBlackPaint2: Paint // black brush
    private lateinit var mTextPaint: Paint
    private lateinit var mcanvas: Canvas
    var calendar: Calendar = Calendar.getInstance()
    private var hour: Int? = null
    private var minute: Int? = null
    private var second: Int? = null
    private var isDrawScale: Boolean = false
    private val textArray = arrayOf("12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11")
    private var refreshThread: Thread? = null
    private var mHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    invalidate()
                }
            }
        }
    }

    init {
        context?.theme?.obtainStyledAttributes(
            attrs,
            R.styleable.clockView,
            0, 0
        )?.apply {
            try {
                isDrawScale = getBoolean(R.styleable.clockView_is_draw_scale, false)
            } catch (e: Exception) {
                e.stackTrace
            }
        }
        initPaints()
    }

    private fun initPaints() {
        mBlackPaint = Paint()
        with(mBlackPaint) {
            color = Color.BLACK
            strokeWidth = 4f
            isAntiAlias = true // lam min
            style = Paint.Style.STROKE
        }
        //Used for drawing surface center
        mBlackPaint2 = Paint()
        with(mBlackPaint2) {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        mRedPaint = Paint()
        with(mRedPaint) {
            color = Color.RED
            strokeWidth = 5f
            isAntiAlias = true
        }

        mTextPaint = Paint()
        with(mTextPaint) {
            color = Color.DKGRAY
            textSize = 30f
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            mcanvas = canvas
        }
        setCurrentTime()

        drawOuterCircle(canvas) //Draw the outermost circle first

        if (isDrawScale) {
            drawTimeScale(canvas) //Draw scale
        }

        drawTimeText(canvas) //Draw text

        drawHand(canvas) //Draw the needle

        drawCenter(canvas) //Draw epicenter
    }

    private fun setCurrentTime() {
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)
        calendar.add(Calendar.SECOND, 1)
    }

    fun changeTime(newMinute: Int, newHour: Int) {
        calendar.set(Calendar.HOUR, newHour)
        calendar.set(Calendar.MINUTE, newMinute)
        calendar.set(Calendar.SECOND, 0)
        invalidate()
    }

    private fun drawOuterCircle(canvas: Canvas?) {
        mBlackPaint.strokeWidth = 5f
        canvas?.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            (measuredWidth / 2 - 5).toFloat(),
            mBlackPaint
        )
    }

    private fun drawCenter(canvas: Canvas?) {
        canvas?.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            20f,
            mBlackPaint2
        )
    }

    private fun drawHand(canvas: Canvas?) {
        mBlackPaint.strokeWidth = 13f
        drawHour(canvas, mBlackPaint)
        mBlackPaint.strokeWidth = 8f
        drawMinute(canvas, mBlackPaint)
        drawSecond(canvas, mRedPaint)
    }

    private fun drawTimeText(canvas: Canvas?) {
        val textR = (measuredWidth / 2 - 45).toFloat() // radius of circle formed by text
        for (i in 0..11) {  //Draw text of hours
            val startX = (measuredWidth / 2 + textR * sin(Math.PI / 6 * i) - mTextPaint.measureText(textArray[i]) / 2).toFloat()
            val startY = (measuredHeight / 2 - textR * cos(Math.PI / 6 * i) + mTextPaint.measureText(textArray[i]) / 2).toFloat()
            canvas?.drawText(textArray[i], startX, startY, mTextPaint)
        }
    }

    private fun drawTimeScale(canvas: Canvas?) {
        var scaleLength: Float?
        canvas?.save()
        //0.. 59 for [0,59]
        for (i in 0..59) {
            if (i % 5 == 0) {   //Large scale
                mBlackPaint.strokeWidth = 5f
                scaleLength = 20f
            } else {    //Small scale
                mBlackPaint.strokeWidth = 3f
                scaleLength = 10f
            }
            canvas?.drawLine(
                measuredWidth / 2.toFloat(),
                5f,
                measuredWidth / 2.toFloat(),
                (5 + scaleLength),
                mBlackPaint
            )
            canvas?.rotate(
                360 / 60.toFloat(),
                measuredWidth / 2.toFloat(),
                measuredHeight / 2.toFloat()
            )
        }
        //Restore the original state
        canvas?.restore()
    }

    private fun drawSecond(canvas: Canvas?, paint: Paint?) {
        val longR = measuredWidth / 2 - 60
        val shortR = 60
        val startX = (measuredWidth / 2 - shortR * sin(second!!.times(Math.PI / 30))).toFloat()
        val startY = (measuredWidth / 2 + shortR * cos(second!!.times(Math.PI / 30))).toFloat()
        val endX = (measuredWidth / 2 + longR * sin(second!!.times(Math.PI / 30))).toFloat()
        val endY = (measuredWidth / 2 - longR * cos(second!!.times(Math.PI / 30))).toFloat()
        if (paint != null) {
            canvas?.drawLine(startX, startY, endX, endY, paint)
        }
    }

    private fun drawMinute(canvas: Canvas?, paint: Paint?) {
        var displayMinute= minute!! + second!!.toFloat()/60
        val longR = measuredWidth / 2 - 90
        val shortR = 50
        val startX = (measuredWidth / 2 - shortR * sin(displayMinute.times(Math.PI / 30))).toFloat()
        val startY = (measuredWidth / 2 + shortR * cos(displayMinute.times(Math.PI / 30))).toFloat()
        val endX = (measuredWidth / 2 + longR * sin(displayMinute.times(Math.PI / 30))).toFloat()
        val endY = (measuredWidth / 2 - longR * cos(displayMinute.times(Math.PI / 30))).toFloat()
        if (paint != null) {
            canvas?.drawLine(startX, startY, endX, endY, paint)
        }
    }

    private fun drawHour(canvas: Canvas?, paint: Paint?) {
        var displayHour= hour!! + minute!!.toFloat()/60
        val longR = measuredWidth / 2 - 150
        val shortR = 40
        val startX = (measuredWidth / 2 - shortR * sin(displayHour.times(Math.PI / 6))).toFloat()
        val startY = (measuredWidth / 2 + shortR * cos(displayHour.times(Math.PI / 6))).toFloat()
        val endX = (measuredWidth / 2 + longR * sin(displayHour.times(Math.PI / 6))).toFloat()
        val endY = (measuredWidth / 2 - longR * cos(displayHour.times(Math.PI / 6))).toFloat()
        if (paint != null) {
            canvas?.drawLine(startX, startY, endX, endY, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val result =
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                DEFAULT_WIDTH
            } else {
                min(widthSpecSize, heightSpecSize)
            }
        setMeasuredDimension(result, result)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshThread = Thread(Runnable {
            while (true) {
                try {
                    Thread.sleep(1000)
                    mHandler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    break
                }
            }
        })
        refreshThread?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacksAndMessages(null)
        refreshThread?.interrupt()
    }
}