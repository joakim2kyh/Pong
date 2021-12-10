package com.kyhgroupd.ponggroupd

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

@RequiresApi(Build.VERSION_CODES.O)
class GameView(context: Context): SurfaceView(context), SurfaceHolder.Callback, Runnable {

    private var thread: Thread? = null
    private var running = false
    lateinit var canvas: Canvas
    var mHolder: SurfaceHolder? = holder

    var frames: Int = 0
    var lastFpsCheck: Long = 0
    var timeToUpdate  = System.currentTimeMillis()

    init {
        mHolder?.addCallback(this)

        val activity: AppCompatActivity = context as AppCompatActivity

        //SoundManager
        SoundManager.init(context)

        //Reset game
        val displayMetrics: DisplayMetrics = activity.resources.displayMetrics
        GameManager.screenSizeX = displayMetrics.widthPixels
        GameManager.screenSizeY = displayMetrics.heightPixels
        GameManager.context = context as BreakoutActivity

        //Reset game (or continue from "paused" game)?
        if(GameManager.shouldReset){
            GameManager.resetGame()
        }
    }

    fun start() {
        running = true
        thread = Thread(this)
        thread?.start()
    }

    fun stop() {
        running = false
        try {
            thread?.join()
        } catch (e: InterruptedException){
            e.printStackTrace()
        }
    }

    fun update(){
        //Bricks and paddle
        for (gameObject in GameManager.gameObjects) {
            gameObject.update()
        }

        //Brick pieces
        for (pieceObject in GameManager.pieceObjects) {
            pieceObject.update()
        }
        val pieceObjects = GameManager.pieceObjects.toMutableList()
        for (pieceObject in pieceObjects) {
            if(pieceObject.lifetime <= 0){
                GameManager.pieceObjects.remove(pieceObject)
            }
        }

        //Ball
        GameManager.ball?.update()

        //UI
        GameManager.highScoreText?.textString = "HIGH SCORE: "+GameManager.highScore.toString()
        GameManager.scoreText?.textString = "SCORE: "+GameManager.score.toString()
        GameManager.livesText?.textString = "LIVES: "+GameManager.lives.toString()
    }

    private fun draw(){
        try{
            canvas = mHolder!!.lockCanvas()

            GameManager.background1?.let { canvas.drawBitmap(it, matrix, null) }
            //canvas.drawColor(Color.BLACK) //Draws a black background

            canvas.drawRect(0f, 0f, GameManager.screenSizeX.toFloat(), GameManager.uiHeight.toFloat(), GameManager.uiPaint)

            for (gameObject in GameManager.gameObjects) {
                gameObject.draw(canvas)
            }
            for (pieceObject in GameManager.pieceObjects) {
                pieceObject.draw(canvas)
            }
            for (gameObject in GameManager.uiObjects) {
                gameObject.draw(canvas)
            }
            GameManager.ball?.draw(canvas)

            mHolder!!.unlockCanvasAndPost(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        stop()
    }

    override fun run() {
        while (running){
            if(System.currentTimeMillis() >= timeToUpdate) {
                timeToUpdate += 1000/GameManager.targetFPS

                //FPSCounter
//                frames++
//                if (System.currentTimeMillis() > lastFpsCheck + 1000) {
//                    lastFpsCheck = System.currentTimeMillis()
//                    println(frames)
//                    frames = 0
//                }

                update()
                draw()

                if (GameManager.bricksCleared()) {
                    GameManager.nextLevel()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event != null){
            GameManager.event = event
        }
        return true
    }
}