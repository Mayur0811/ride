package com.bayride.presentation.homeActivity

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bayride.R
import com.bayride.common.sharedpreference.getEncryptedSharedPreferences
import com.bayride.common.utils.Constants
import com.bayride.common.views.getLoginDetails
import com.bayride.data.datasources.local.SecureStorageManager
import com.bayride.databinding.ActivityHomeBinding
import com.bayride.presentation.base.BaseActivity
import com.bayride.presentation.features.home.HomeScreenViewModel
import com.bayride.presentation.features.homedetails.driverRequest.DriverRequestBottomSheet
import com.bayride.presentation.features.logOutPopUp.LogOutPopUpDialog
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.nav_header_drawer.view.*
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity(), DriverRequestBottomSheet.BottomSheetListener {
    private lateinit var binding: ActivityHomeBinding
    val viewModel: HomeScreenViewModel by viewModels()

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment_content_drawer)
    }
    private val value by lazy {
        if (intent.extras != null) {
            intent.getBooleanExtra("request", false)
        }
    }

    @Inject
    lateinit var secureStorageManager: SecureStorageManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        updateStatusBarColor(ContextCompat.getColor(this, R.color.white))
        setupDrawerLayout()

        val decorView: View? = window?.decorView
        val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
        val windowBackground = decorView.background
        binding.blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(5F)
            .setBlurAutoUpdate(true)
//
        val file = File(
            filesDir.toString() + File.separator,
            "map1.png"
        )
        if (file.exists()) {
            binding.blurImage.setImageURI(Uri.parse(file.absolutePath))
        }
        binding.toggle.setOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        binding.navView.getHeaderView(0).txtEmail.text =
            getLoginDetails(this)?.info?.user_email_id.toString()
        binding.navView.getHeaderView(0).txtName.text = getLoginDetails(this)?.info?.user_name
        val imageProfile = Constants.imageDomain + getLoginDetails(this)?.info?.user_profile_pic
        Glide.with(this).load(imageProfile).placeholder(R.drawable.ic_user_avatar)
            .into(binding.navView.getHeaderView(0).imgUser)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (getEncryptedSharedPreferences(this)?.getInt("type", 3) == 1) {
                        if (secureStorageManager.fairId == 0) {
                            secureStorageManager.flag = false
                            navController.navigate(R.id.homeScreen2)
                        } else {
                            if (secureStorageManager.flag) {
                                navController.navigate(R.id.ongoingScreen_home_nav_graph)
                            } else {
                                navController.navigate(R.id.homeDetailsScreen)
                            }
                        }
                        //navController.navigate(R.id.homeDetailsScreen)
                    } else if (getEncryptedSharedPreferences(this)?.getInt("type", 3) == 2) {
                        navController.navigate(R.id.homeScreen2)
                    }
                }
                R.id.nav_book_history -> {
                    if (getEncryptedSharedPreferences(this)?.getInt("type", 3) == 1)
                        navController.navigate(R.id.bookingHistoryScreen)
                    else if (getEncryptedSharedPreferences(this)?.getInt("type", 3) == 2)
                        navController.navigate(R.id.driverBookingHistory)
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.profileScreen)
                }
                R.id.nav_change_password -> {
                    navController.navigate(R.id.changePasswordScreen)
                }
                R.id.nav_log_out -> {
                    binding.drawerLayout.addDrawerListener(object :
                        DrawerLayout.DrawerListener {
                        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                        }

                        override fun onDrawerOpened(drawerView: View) {

                        }

                        override fun onDrawerClosed(drawerView: View) {
                            LogOutPopUpDialog().show(supportFragmentManager, "")
                            binding.drawerLayout.removeDrawerListener(this)
                        }

                        override fun onDrawerStateChanged(newState: Int) {

                        }
                    })
                }
            }
            onNavDestinationSelected(menuItem, navController)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    private fun setupDrawerLayout() {
        binding.navView.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            finishAffinity()
        } else {
            when (navController.currentDestination?.id) {
                R.id.homeScreen2 -> {
                    finishAffinity()
                }
                R.id.homeDetailsScreen -> {
                    finishAffinity()
                }
                else -> {
                    navController.navigateUp()
                }
            }
//            finishAffinity()
        }
    }

    override fun onBottomSheetShow(show: Boolean, radius: Float) {

    }

}