package com.example.sparky.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.sparky.R
import kotlin.math.absoluteValue

class BouncingCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circles = mutableListOf<Circle>()
    private val handler = Handler(Looper.getMainLooper())
    private val frameRate = 8L

    private val texts = listOf("BEAM", "WAVE", "PULSE", "RAY")
    private val images = listOf(
        R.drawable.exm_1,
        R.drawable.exm_2,
        R.drawable.exm_3,
        R.drawable.exm_4
    )
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#ffffff")
        strokeWidth = 6f
    }

    init {
        var countLargeCircles = 0
        repeat(75) {
            var newCircle: Circle
            val randomX = 500f
            val randomY = 1000f
            val randomRadius = (25..100).random().toFloat()

            if (randomRadius > 95 && countLargeCircles >= 8) {
                newCircle = createRandomCircle(randomX, randomY, (25..90).random().toFloat())
            } else {
                newCircle = createRandomCircle(randomX, randomY, randomRadius)
                if (randomRadius > 95) {
                    countLargeCircles++
                }
            }

            circles.add(newCircle)
        }

        startAnimation()
    }




    private fun createRandomCircle(x: Float, y: Float, radius: Float = (25..100).random().toFloat()): Circle {
        val min = -20f
        val max = 20f
        val vx = (min + Math.random() * (max - min)).toFloat()
        val vy = (min + Math.random() * (max - min)).toFloat()
        val randomText = texts.random()
        val randomImageRes = images.random()
        val bitmap = BitmapFactory.decodeResource(resources, randomImageRes)

        return Circle(
            x = x,
            y = y,
            radius = radius,
            vx = vx,
            vy = vy,
            mass = 1f,
            isSelected = false,
            bitmap = bitmap,
            text = randomText
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        circles.forEach { circle ->
            canvas.drawCircle(circle.x, circle.y, circle.radius, circlePaint)
            if (!circle.hasDrawnContent) {
                drawTextAndImageForCircle(circle)
                circle.hasDrawnContent = true
            }
            circle.cachedBitmap?.let { canvas.drawBitmap(it, circle.x - circle.radius, circle.y - circle.radius, null) }

            if (circle.isSelected) {
                canvas.drawCircle(circle.x, circle.y, circle.radius, outlinePaint)
            }
        }
    }

    private fun drawTextAndImageForCircle(circle: Circle) {
        val size = (circle.radius * 2).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val gradient = when {
            circle.radius <= 80 -> {
                RadialGradient(
                    circle.radius, circle.radius, circle.radius,
                    intArrayOf(
                        Color.parseColor("#20242F"),
                        Color.parseColor("#293031"),
                        Color.parseColor("#3C585B"),
                        Color.parseColor("#5B9AA0")
                    ),
                    floatArrayOf(0f, 0.80f, 0.88f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            else -> {
                RadialGradient(
                    circle.radius, circle.radius, circle.radius,
                    intArrayOf(
                        Color.parseColor("#20242F"),
                        Color.parseColor("#28343C"),
                        Color.parseColor("#324F65"),
                        Color.parseColor("#4583B3")
                    ),
                    floatArrayOf(0f, 0.80f, 0.88f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = gradient
        }
        canvas.drawCircle(circle.radius, circle.radius, circle.radius, paint)

        circle.scaledBitmap?.let {
            val left = circle.radius - it.width / 2
            val top = circle.radius - circle.radius / 1.5f - it.height / 2
            canvas.drawBitmap(it, left, top, null)
        }

        val cryptoNames = listOf("Bitcoin", "Ethereum", "Ripple", "Litecoin")
        val cryptoPercentages = listOf("+10%", "+5%", "+15%", "+20%")

        val randomIndex = (cryptoNames.indices).random()
        val selectedCryptoName = cryptoNames[randomIndex]
        val selectedCryptoPercentage = cryptoPercentages[randomIndex]

        val availableWidth = circle.radius * 2f
        var textSize = circle.radius / 1.8f

        textPaint.textSize = textSize
        while (textPaint.measureText(selectedCryptoName) > availableWidth && textSize > 0) {
            textSize -= 7
            textPaint.textSize = textSize
        }
        canvas.drawText(selectedCryptoName, circle.radius, circle.radius + textSize * 0.3f, textPaint)

        textPaint.textSize = circle.radius / 3.6f
        canvas.drawText(selectedCryptoPercentage, circle.radius, circle.radius + textSize * 1.1f, textPaint)

        circle.cachedBitmap = bitmap
    }

    private fun startAnimation() {
        handler.post(object : Runnable {
            override fun run() {
                handler.postDelayed({
                    updatePhysics()
                    invalidate()
                }, 300)

                handler.postDelayed(this, frameRate)
            }
        })
    }

    private fun updatePhysics() {
        val width = width.toFloat()
        val height = height.toFloat()

        val slowdownFactor = 1f
        val maxSpeed = 10f
        val minSpeed = 0.3f // Минимальная скорость шаров

        circles.forEach { circle ->
            val speed = Math.sqrt((circle.vx * circle.vx + circle.vy * circle.vy).toDouble()).toFloat()
            if (speed > minSpeed) {
                circle.vx *= slowdownFactor
                circle.vy *= slowdownFactor
            }

            if (speed > maxSpeed) {
                val scale = maxSpeed / speed
                circle.vx *= scale
                circle.vy *= scale
            }

            circle.x += circle.vx
            circle.y += circle.vy

            if (circle.x - circle.radius < 0) {
                circle.x = circle.radius
                circle.vx = -circle.vx
            } else if (circle.x + circle.radius > width) {
                circle.x = width - circle.radius
                circle.vx = -circle.vx
            }

            if (circle.y - circle.radius < 0) {
                circle.y = circle.radius
                circle.vy = -circle.vy
            } else if (circle.y + circle.radius > height) {
                circle.y = height - circle.radius
                circle.vy = -circle.vy
            }
        }

        for (i in circles.indices) {
            for (j in i + 1 until circles.size) {
                if (!circles[i].isSelected && !circles[j].isSelected) {
                    val circle1 = circles[i]
                    val circle2 = circles[j]
                    val dx = circle2.x - circle1.x
                    val dy = circle2.y - circle1.y
                    val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                    val minDistance = circle1.radius + circle2.radius + 0.3f

                    if (distance < minDistance) {
                        val overlap = minDistance - distance

                        val nx = dx / distance
                        val ny = dy / distance

                        circle1.x -= nx * overlap / 2
                        circle1.y -= ny * overlap / 2
                        circle2.x += nx * overlap / 2
                        circle2.y += ny * overlap / 2

                        val relativeVelocityX = circle2.vx - circle1.vx
                        val relativeVelocityY = circle2.vy - circle1.vy
                        val velocityAlongNormal = relativeVelocityX * nx + relativeVelocityY * ny

                        if (velocityAlongNormal < 0) {
                            val restitution = 0.9f
                            val impulse = -(1 + restitution) * velocityAlongNormal
                            val impulseX = (impulse * nx) / 2f
                            val impulseY = impulse * ny / 2f

                            circle1.vx -= impulseX / circle1.mass
                            circle1.vy -= impulseY / circle1.mass
                            circle2.vx += impulseX / circle2.mass
                            circle2.vy += impulseY / circle2.mass

                        }
                    }
                }
            }
        }
    }





    private fun selectedResolveCollision(selectedCircle: Circle) {
        val friction = 0.98f
        val minSpeed = 0.01f

        if (selectedCircle.vx.absoluteValue > minSpeed || selectedCircle.vy.absoluteValue > minSpeed) {
            selectedCircle.vx *= friction
            selectedCircle.vy *= friction
        } else {
            selectedCircle.vx = 0f
            selectedCircle.vy = 0f
        }

        circles.forEach { otherCircle ->
            if (otherCircle == selectedCircle) return@forEach

            val dx = otherCircle.x - selectedCircle.x
            val dy = otherCircle.y - selectedCircle.y
            val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            val overlap = (selectedCircle.radius + otherCircle.radius) / 4 - distance

            if (overlap > 0) {
                val nx = dx / distance
                val ny = dy / distance

                // смещение кругов
                val correction = overlap / 2
                selectedCircle.x -= nx * correction
                selectedCircle.y -= ny * correction

                otherCircle.x += nx * correction
                otherCircle.y += ny * correction

                // Импульс
                val impulseStrength = 1.5f
                otherCircle.vx += selectedCircle.vx * 0.5f + nx * impulseStrength
                otherCircle.vy += selectedCircle.vy * 0.5f + ny * impulseStrength
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                var selectedCircle: Circle? = null

                circles.forEach { circle ->
                    val dx = touchX - circle.x
                    val dy = touchY - circle.y
                    val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                    if (distance <= circle.radius) {
                        selectedCircle = circle
                    }
                }

                if (selectedCircle != null) {
                    if (selectedCircle!!.isSelected) {
                        selectedCircle!!.isSelected = false
                    } else {
                        circles.forEach { circle -> circle.isSelected = false }
                        selectedCircle!!.isSelected = true

                        circles.remove(selectedCircle)
                        circles.add(selectedCircle!!)
                    }
                } else {
                    circles.forEach { circle -> circle.isSelected = false }
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                circles.forEach { circle ->
                    if (circle.isSelected) {
                        val dx = touchX - circle.x
                        val dy = touchY - circle.y
                        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                        if (distance > 0) {
                            val normalizedDx = dx / distance
                            val normalizedDy = dy / distance

                            val impulseStrength = 4.5f
                            circle.vx = normalizedDx * impulseStrength
                            circle.vy = normalizedDy * impulseStrength

                            circle.x += circle.vx
                            circle.y += circle.vy

                            selectedResolveCollision(circle)
                        }
                    }
                }

                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                circles.forEach { circle ->
                    if (circle.isSelected) {
                        val pushStrength = 0.1f
                        val animationSteps = 30
                        val animationDelay = 16L

                        val tasks = mutableListOf<Runnable>()

                        circles.forEach { otherCircle ->
                            if (otherCircle != circle) {
                                val dx = otherCircle.x - circle.x
                                val dy = otherCircle.y - circle.y
                                val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                                if (distance < circle.radius + otherCircle.radius) {
                                    val overlap = (circle.radius + otherCircle.radius) - distance
                                    val nx = dx / distance
                                    val ny = dy / distance
                                    val targetX = otherCircle.x + nx * overlap * pushStrength
                                    val targetY = otherCircle.y + ny * overlap * pushStrength

                                    val initialX = otherCircle.x
                                    val initialY = otherCircle.y
                                    val deltaX = (targetX - initialX) / animationSteps
                                    val deltaY = (targetY - initialY) / animationSteps

                                    tasks.add(object : Runnable {
                                        var step = 0

                                        override fun run() {
                                            if (step < animationSteps) {
                                                otherCircle.x += deltaX
                                                otherCircle.y += deltaY
                                                step++
                                                postDelayed(this, animationDelay)
                                                invalidate()
                                            }
                                        }
                                    })
                                }
                            }
                        }

                        tasks.forEach { post(it) }
                    }
                    circle.isSelected = false
                }
                return true
            }




            else -> return super.onTouchEvent(event)
        }
    }


    data class Circle(
        var x: Float =  500f,
        var y: Float =  1000f,
        val radius: Float,
        var vx: Float,
        var vy: Float,
        val mass: Float,
        var isSelected: Boolean,
        val color: Int = Color.rgb((0..255).random(), (0..255).random(), (0..255).random()),
        val bitmap: Bitmap?,
        val imageSize: Float = radius / 2,
        var shouldDrawText: Boolean = true,
        var hasDrawnContent: Boolean = false,
        val text: String
    ) {
        val scaledBitmap: Bitmap? by lazy {
            bitmap?.let {
                Bitmap.createScaledBitmap(it, imageSize.toInt(), imageSize.toInt(), true)
            }
        }
        var cachedBitmap: Bitmap? = null
    }
}










