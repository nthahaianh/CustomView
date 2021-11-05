package com.example.customview.View.LockPattern

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.setMargins
import com.example.customview.Model.Dot
import com.example.customview.R

class PatternLockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val INITIAL = 1
        private const val START = 2
        private const val SUCCESS = 3
        private const val ERROR = 4
        private const val FIRST = 5
        private const val SECOND = 6
    }

    private var stageState: Int = FIRST
    private var touchedPointX: Float = 0.0f
    private var touchedPointY: Float = 0.0f
    private var minCount = 4
    private var maxCount = 9
    private var markedDotList = mutableListOf<Dot>()
    private var initialDotList = mutableListOf<Dot>()
    private var state: Int = INITIAL
    private var attrIsDotAnimate = true
    private var onChangeStateListener: ((state: Int) -> Unit)? = null
    private var countDownTimer: CountDownTimer? = null
    private var stagePasswords = linkedMapOf<Int, String>()

    @ColorInt
    private var attrDotColor = Color.DKGRAY

    @ColorInt
    private var attrLineColor = Color.DKGRAY

    private val rect = Rect()
    private val dotNumberKeyArray = arrayOf(
        arrayOf("1", "2", "3"),
        arrayOf("4", "5", "6"),
        arrayOf("7", "8", "9")
    )
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 12f
        color = Color.DKGRAY
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PatternLockView,
            0,
            0
        ).apply {
            try {
                attrIsDotAnimate = getBoolean(R.styleable.PatternLockView_patternLock_isAnimate, true)
                attrDotColor = getColor(R.styleable.PatternLockView_patternLock_dotColor, Color.DKGRAY)
                attrLineColor = getColor(R.styleable.PatternLockView_patternLock_lineColor, Color.DKGRAY)
            } finally {
                recycle()
            }
        }

        orientation = VERTICAL

        paint.color = attrLineColor

        drawPatternView()
        setWillNotDraw(false)   //to make the onDraw of subclass of ViewGroup being called
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        addInitialData()

        if (state == ERROR) {
            updateViewState(state)
        }

        drawLine(canvas)

        if (state == ERROR) {
            countDownTimer = object : CountDownTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //no-op
                }

                override fun onFinish() {
                    reset()
                }
            }.start()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { motionEvent ->
            touchedPointX = motionEvent.x
            touchedPointY = motionEvent.y

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (state == SUCCESS) {
                        return false
                    }
                    reset()
                    if (isTouchedDot(touchedPointX, touchedPointY)) {
                        state = START
                        onChangeStateListener?.invoke(state)
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (markedDotList.size != 0 && markedDotList.size >= minCount) {
                        when (stageState) {
                            FIRST -> {
                                stagePasswords[FIRST] = getDrawnPatternKey()
                                state = SUCCESS
                            }
                            SECOND -> {
                                stagePasswords[SECOND] = getDrawnPatternKey()
                                state =
                                    if (stagePasswords[FIRST] != stagePasswords[SECOND]) {
                                        ERROR
                                    } else {
                                        SUCCESS
                                    }
                            }
                        }
                        updateViewState(state)
                        onChangeStateListener?.invoke(state)
                    } else if (markedDotList.size != 0) {
                        state = ERROR
                        onChangeStateListener?.invoke(state)
                    }
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (state == START && markedDotList.size != maxCount) {
                        isTouchedDot(touchedPointX, touchedPointY)
                        invalidate()
                    }
                }
            }
            return true
        }

        return false
    }

    private fun drawPatternView(
        rowSize: Int = 3,
        columnSize: Int = 3,
        layoutParams: ViewGroup.LayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
            gravity = Gravity.CENTER
        },
        nodeKeys: Array<Array<String>> = dotNumberKeyArray
    ) {
        for (rowIndex in 0 until rowSize) {
            createRow(this@PatternLockView, layoutParams).apply {
                for (columnIndex in 0 until columnSize) {
                    createRow(this, LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        weight = 1f
                        gravity = Gravity.CENTER
                    }).run {
                        createColumn(this, nodeKeys[rowIndex][columnIndex])
                    }
                }
            }
        }
    }

    private fun createRow(view: LinearLayout, layoutParams: ViewGroup.LayoutParams): LinearLayout {
        view.apply {
            addView(LinearLayout(context).apply {
                this.layoutParams = layoutParams
                this.gravity = Gravity.CENTER
            })
        }

        return view.getChildAt(view.childCount - 1) as LinearLayout
    }

    private fun createColumn(view: LinearLayout, nodeKey: String) {
        val margins = context.resources.getDimensionPixelSize(R.dimen.dot_view_margin)
        view.apply {
            addView(
                DotView(context).apply {
                    (layoutParams as MarginLayoutParams).setMargins(margins)
                    setDotViewColor(attrDotColor)
                    setKey(nodeKey)
                }
            )
        }
    }

    private fun addInitialData() {  // takes the position points, indexes, and key of each dot
        if (initialDotList.size != 0) return
        forEachIndexed { rowIndex, view ->
            (view as? ViewGroup)?.forEachIndexed { columnIndex, viewGroup ->
                (viewGroup as? ViewGroup)?.forEach { nodeView ->
                    if (nodeView !is DotView) return

                    nodeView.getLocalVisibleRect(rect)

                    offsetDescendantRectToMyCoords(nodeView, rect)

                    initialDotList.add(
                        Dot(
                            rowIndex,
                            columnIndex,
                            rect.left,
                            rect.right,
                            rect.top,
                            rect.bottom,
                            nodeView.key
                        )
                    )
                }
            }
        }
    }

    private fun drawLine(canvas: Canvas?) {
        markedDotList.forEachIndexed { index, _ ->
            if (index + 1 < markedDotList.size) {
                val startX = (markedDotList[index].rightPoint + markedDotList[index].leftPoint) / 2.toFloat()
                val startY = ((markedDotList[index].bottomPoint.toFloat()) + (markedDotList[index].topPoint.toFloat())) / 2
                val endX = (markedDotList[index + 1].rightPoint + markedDotList[index + 1].leftPoint) / 2.toFloat()
                val endY = ((markedDotList[index + 1].bottomPoint.toFloat()) + (markedDotList[index + 1].topPoint.toFloat())) / 2
                canvas?.drawLine(startX, startY, endX, endY, paint)
            }
        }

        if (state == START) {
            val dot = markedDotList.last()
            val startX = (dot.rightPoint + dot.leftPoint) / 2.toFloat()
            val startY = ((dot.bottomPoint.toFloat()) + (dot.topPoint.toFloat())) / 2
            canvas?.drawLine(startX, startY, touchedPointX, touchedPointY, paint)
        }
    }

    private fun isTouchedDot(pointX: Float, pointY: Float): Boolean {
        val touchedDot = getTouchedDotByPosition(pointX, pointY) ?: return false
        if (isDotSelected(touchedDot)) return false

        markedDotList.takeIf { it.size != 0 }?.last()?.let { lastTouchedDot ->
            val rowIndex = (lastTouchedDot.rowIndex + touchedDot.rowIndex) / 2.toFloat()
            val columnIndex = (lastTouchedDot.columnIndex + touchedDot.columnIndex) / 2.toFloat()
            getDotWithIndex(rowIndex, columnIndex)?.let { previousDot ->
                if (isDotSelected(previousDot).not()) {
                    selectDotView(previousDot)
                }
            }
        }
        if (markedDotList.size != maxCount) {
            selectDotView(touchedDot)
        }
        return true
    }

    private fun getTouchedDotByPosition(pointX: Float, pointY: Float) = initialDotList.firstOrNull {
        ((it.leftPoint) <= pointX && (it.topPoint) <= pointY)
                && ((it.rightPoint) >= pointX && (it.bottomPoint) >= pointY)
    }

    private fun isDotSelected(dot: Dot) =
        markedDotList.firstOrNull { markedDot ->
            markedDot.rowIndex == dot.rowIndex && markedDot.columnIndex == dot.columnIndex
        } != null

    private fun getDotWithIndex(rowIndex: Float, columnIndex: Float) = initialDotList.firstOrNull {
        it.rowIndex.toFloat() == rowIndex && it.columnIndex.toFloat() == columnIndex
    }

    private fun selectDotView(selectedDot: Dot) {
        markedDotList.add(selectedDot)
        if (attrIsDotAnimate) {
            (((getChildAt(selectedDot.rowIndex) as? ViewGroup)
                ?.getChildAt(selectedDot.columnIndex) as? ViewGroup)
                ?.getChildAt(0) as? DotView)
                ?.animateDotView()
        }
    }

    private fun updateViewState(
        state: Int
    ) {
        @ColorInt val dotColor: Int
        @ColorInt val lineColor: Int

        when (state) {
            SUCCESS -> {
                dotColor = R.color.black
                lineColor = R.color.black
            }
            ERROR -> {
                dotColor = Color.RED
                lineColor = Color.RED
            }
            else -> {
                dotColor = attrDotColor
                lineColor = attrLineColor
            }
        }

        paint.color = lineColor

        markedDotList.forEach { dot ->
            (((this.getChildAt(dot.rowIndex) as? ViewGroup)
                ?.getChildAt(dot.columnIndex) as? ViewGroup)
                ?.getChildAt(0) as? DotView)
                ?.setDotViewColor(dotColor)
        }
    }

    private fun getDrawnPatternKey() =
        markedDotList.map { it.key }.joinToString("")

    fun reset() {
        state = INITIAL
        updateViewState(state)
        markedDotList.clear()
        countDownTimer?.cancel()
        countDownTimer = null
        invalidate()
    }

    fun getPassword() = markedDotList
}