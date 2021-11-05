package com.example.customview

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.customview.View.Animation.AnimationFragment
import com.example.customview.View.Clock.ClockFragment
import com.example.customview.View.CustomView.CustomViewFragment
import com.example.customview.View.LockPattern.LockFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var drawerToggle: ActionBarDrawerToggle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerToggle = ActionBarDrawerToggle(this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(drawerToggle!!)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpMenu()
        replaceFragment(ClockFragment())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle?.onOptionsItemSelected(item)!!) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setUpMenu(){
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_menu_clock -> {
                    replaceFragment(ClockFragment())
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                R.id.item_menu_lock_pattern -> {
                    replaceFragment(LockFragment())
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                R.id.item_menu_custom_view -> {
                    replaceFragment(CustomViewFragment())
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                R.id.item_menu_animation -> {
                    replaceFragment(AnimationFragment())
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            }
            false
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager?.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
//        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.backStackEntryCount <=0){
            finish()
        }
    }
}