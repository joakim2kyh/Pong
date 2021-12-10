package com.kyhgroupd.ponggroupd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyhgroupd.ponggroupd.databinding.ActivityHighScoreBinding
import com.kyhgroupd.ponggroupd.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binder : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binder.root)

        binder.btnBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        this.activateSwitches()
    }

    private fun activateSwitches(){
        binder.switchSFX.isChecked = GameManager.useSFX
        binder.switchSFX.setOnCheckedChangeListener { _, b ->
            GameManager.useSFX = !GameManager.useSFX
            DataManager.saveSettings()
        }

        binder.switchMusic.isChecked = GameManager.useMusic
        binder.switchMusic.setOnCheckedChangeListener { _, b ->
            GameManager.useMusic = !GameManager.useMusic
            DataManager.saveSettings()
        }

        binder.switchColors.isChecked = GameManager.useColors
        binder.switchColors.setOnCheckedChangeListener { _, b ->
            GameManager.useColors = !GameManager.useColors
            DataManager.saveSettings()
        }
    }
}