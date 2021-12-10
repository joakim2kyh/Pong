package com.kyhgroupd.ponggroupd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyhgroupd.ponggroupd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binder : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        binder.btnPlayBreakout.setOnClickListener {
            val intent = Intent(this, BreakoutActivity::class.java)
            GameManager.shouldReset = true
            startActivity(intent)
        }

        binder.btnContinue.setOnClickListener{
            if(GameManager.context != null){
                val intent = Intent(this, BreakoutActivity::class.java)
                GameManager.shouldReset = false
                startActivity(intent)
            }
        }

        binder.btnSettings.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binder.btnHighScore.setOnClickListener{
            val intent = Intent(this, HighScoreActivity::class.java)
            startActivity(intent)
        }

        //Initiate DataManager
        DataManager.initiate(filesDir.toString())
    }

    override fun onResume() {
        super.onResume()

        this.loadSettings()

        if(GameManager.context == null){
            binder.btnContinue.isEnabled = false
            return
        }
        binder.btnContinue.isEnabled = true
    }

    private fun loadSettings(){
        val settingsList = DataManager.loadSettings()
        if(settingsList.size < GameManager.numberOfSettings){
            return
        }
        GameManager.useSFX = settingsList[0]
        GameManager.useMusic = settingsList[1]
        GameManager.useColors = settingsList[2]
    }

}