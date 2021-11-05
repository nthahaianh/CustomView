package com.example.customview.View.Animation

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.customview.R
import com.example.customview.ViewModel.AnimationViewModel
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {
    var animationViewModel:AnimationViewModel?=null
    lateinit var animation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        setUpViewModel()
        btn_zoom.setOnClickListener {
            animation = AnimationUtils.loadAnimation(this,R.anim.zoom_in)
            demo_ivFlower.startAnimation(animation)
        }
        btn_rotate.setOnClickListener {
            animation = AnimationUtils.loadAnimation(this,R.anim.rotate)
            demo_ivFlower.startAnimation(animation)
        }
        btn_bounce.setOnClickListener {
            animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
            demo_ivFlower.startAnimation(animation)
        }
        btn_blink.setOnClickListener {
            animation = AnimationUtils.loadAnimation(this,R.anim.blink)
            demo_ivFlower.startAnimation(animation)
        }
    }

    private fun setUpViewModel() {
        animationViewModel = ViewModelProviders.of(this).get(AnimationViewModel::class.java)
        animationViewModel!!.isLeft.observe(this, Observer {isLeft->
            btn_MoveLeftRight.setOnClickListener {
                if(isLeft){
                    demo_ivFlower.animate().translationX(400F).withLayer()
                }else{
                    demo_ivFlower.animate().translationX(0F).withLayer()
                }
                animationViewModel!!.isLeft.value = !isLeft
            }
        })
        animationViewModel!!.isDisplay.observe(this, Observer {display->
            if (display){
                btn_appear.text = "Disappear"
                animation = AnimationUtils.loadAnimation(this,R.anim.appear_up)
                demo_ivFlower.startAnimation(animation)
                demo_ivFlower.visibility = View.VISIBLE
            }else{
                btn_appear.text = "Appear"
                animation = AnimationUtils.loadAnimation(this,R.anim.disappear_down)
                demo_ivFlower.startAnimation(animation)
                demo_ivFlower.visibility  = View.GONE
            }
            btn_appear.setOnClickListener {
                animationViewModel!!.isDisplay.value = !display
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }
}