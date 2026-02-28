package com.jaybox.view.setting

import android.os.Bundle
import com.jaybox.R
import com.jaybox.databinding.ActivitySettingBinding
import com.jaybox.util.inflate
import com.jaybox.view.base.BaseActivity
import com.jaybox.view.base.PermissionActivity

class SettingActivity : PermissionActivity() {

    private val viewBinding: ActivitySettingBinding by inflate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initToolbar(viewBinding.toolbarLayout.toolbar, R.string.app_setting,true)
        supportFragmentManager.beginTransaction().replace(R.id.fragment,SettingFragment()).commit()
    }

    fun setRequestCallback(callback:((Boolean)->Unit)?){
        this.requestPermissionCallback = callback
        requestStoragePermission()
    }
}