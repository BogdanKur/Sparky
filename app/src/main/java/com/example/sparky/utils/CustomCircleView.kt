package com.example.sparky.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.sparky.R

class CustomCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#005fff")
        strokeWidth = 6f
    }

    private val bitmap: Bitmap?
    private var isSelected = false

    init {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.exm_1)

        setOnClickListener {
            isSelected = !isSelected
            invalidate()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) - 20f

        val gradient = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(
                Color.parseColor("#33279A29"),
                Color.parseColor("#33279A29"),
                Color.parseColor("#84279A29"),
                Color.parseColor("#6FE848")
            ),
            floatArrayOf(0f, 0.80f, 0.88f, 1f),
            Shader.TileMode.CLAMP
        )
        circlePaint.shader = gradient

        canvas.drawCircle(centerX, centerY, radius, circlePaint)

        val beamText = "BEAM"
        val availableWidth = radius * 2f
        var textSize = radius / 1.8f

        textPaint.textSize = textSize
        while (textPaint.measureText(beamText) > availableWidth && textSize > 0) {
            textSize -= 7
            textPaint.textSize = textSize
        }

        canvas.drawText(beamText, centerX, centerY + textSize / 1.5f, textPaint)

        textPaint.textSize = radius / 3.6f
        canvas.drawText("+75,6%", centerX, centerY + textPaint.textSize * 2.5f, textPaint)

        bitmap?.let {
            val bitmapSize = radius / 2
            val left = centerX - bitmapSize / 2
            val top = centerY - bitmapSize - (bitmapSize / 1.5f)
            val scaledBitmap = Bitmap.createScaledBitmap(it, bitmapSize.toInt(), bitmapSize.toInt(), true)
            canvas.drawBitmap(scaledBitmap, left, top, null)
        }

        if (isSelected) {
            canvas.drawCircle(centerX, centerY, radius, outlinePaint)
        }
    }
}




