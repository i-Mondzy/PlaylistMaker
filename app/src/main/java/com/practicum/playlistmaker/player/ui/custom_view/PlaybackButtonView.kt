package com.practicum.playlistmaker.player.ui.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

//    region Картинки
    private var imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?
//    endregion

//    region Расчеты
    private var imageRect = RectF()
//    endregion

    //    region Флаги
    private var isPlaying = false
//    endregion

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.PlaybackButtonContainer, defStyleAttr, defStyleRes
        ).apply {
            try {
                imagePlayBitmap = getDrawable(R.styleable.PlaybackButtonContainer_imagePlayResId)?.toBitmap()
                imagePauseBitmap = getDrawable(R.styleable.PlaybackButtonContainer_imagePauseResId)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val imageBitmap = if (isPlaying) imagePauseBitmap else imagePlayBitmap

        imageBitmap?.let {
            canvas.drawBitmap(imageBitmap, null, imageRect, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setCurrentButton(playing: Boolean) {
        isPlaying = playing

        // Явно указываем виджету на необходимость перерисовки
        invalidate()
    }

}
