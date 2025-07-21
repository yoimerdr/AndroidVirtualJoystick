package com.yoimerdr.android.virtualjoystick.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.yoimerdr.android.virtualjoystick.R
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ColorfulControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.exceptions.LowerNumberException
import com.yoimerdr.android.virtualjoystick.geometry.position.FixedPosition
import com.yoimerdr.android.virtualjoystick.geometry.position.ImmutablePosition
import com.yoimerdr.android.virtualjoystick.geometry.Plane
import com.yoimerdr.android.virtualjoystick.geometry.size.ImmutableSize
import com.yoimerdr.android.virtualjoystick.geometry.size.Size
import com.yoimerdr.android.virtualjoystick.theme.ColorsScheme
import com.yoimerdr.android.virtualjoystick.utils.log.Logger
import com.yoimerdr.android.virtualjoystick.views.handler.TouchHoldEventHandler
import androidx.core.content.withStyledAttributes

/**
 * A view representing a virtual joystick.
 */
class JoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0,
) : View(context, attrs, defaultStyle) {

    /**
     * The [Size] representation of the view size.
     */
    private val viewSize: ImmutableSize get() = Size(width, height)

    private val viewRadius: Double get() = viewSize.width / 2.0

    /**
     * The control movement listener.
     */
    private var listener: MoveListener? = null

    /**
     * The interval for the joystick listener call when it is hold.
     */
    var holdInterval: Long = HOLD_INTERVAL
        /**
         * Changes the current interval for the joystick listener call when the control is hold.
         * @param interval An interval value in ms.
         */
        set(interval) {
            field = getHoldInterval(interval)
            touchHandler.apply {
                holdInterval = field
                activeHoldInterval = field
            }
        }

    /**
     * The control holds handler.
     */
    private val touchHandler: JoystickTouchHandler = JoystickTouchHandler(this, holdInterval)


    private var mControl: Control


    /**
     * Gets the current position.
     */
    val position: ImmutablePosition get() = mControl.position

    /**
     * Gets the current center.
     */
    val center: ImmutablePosition get() = mControl.center

    /**
     * Gets distance between current [position] and [center].
     */
    val distance: Float get() = mControl.distance

    /**
     * Gets the angle (clockwise) formed from the current [position] and the [center].
     *
     * @return A value in the range from 0 to 2PI radians.
     */
    val angle: Double get() = mControl.angle

    val centeredPosition: ImmutablePosition get() = mControl.centeredPosition

    val ndcPosition: ImmutablePosition get() = mControl.ndcPosition


    init {
        var primaryColor = ContextCompat.getColor(context, R.color.drawer_primary)
        var accentColor = ContextCompat.getColor(context, R.color.drawer_accent)

        var invalidRadius: Float = resources.getDimensionPixelSize(R.dimen.invalidRadius).toFloat()
        var backgroundRes: Int = R.drawable.circlefor_bg
        var controlType = Control.DrawerType.CIRCLE
        var directionType = Control.DirectionType.COMPLETE

        var arcSweepAngle: Float = ResourcesCompat.getFloat(resources, R.dimen.arc_sweepAngle)
        var arcStrokeWidth: Float = ResourcesCompat.getFloat(resources, R.dimen.arc_strokeWidth)

        var circleRadiusProportion: Float =
            ResourcesCompat.getFloat(resources, R.dimen.circle_radiusRatio)

        if (attrs != null) {
            context.withStyledAttributes(attrs, R.styleable.JoystickView) {
                val styles = this

                holdInterval =
                    styles.getInteger(
                        R.styleable.JoystickView_moveInterval,
                        holdInterval.toInt()
                    )
                        .toLong()

                // all types
                invalidRadius = styles.getDimensionPixelSize(
                    R.styleable.JoystickView_invalidRadius,
                    invalidRadius.toInt()
                ).toFloat()
                primaryColor = styles.getColor(
                    R.styleable.JoystickView_controlDrawer_primaryColor,
                    primaryColor
                )
                accentColor = styles.getColor(
                    R.styleable.JoystickView_controlDrawer_accentColor,
                    accentColor
                )
                controlType = Control.DrawerType.fromId(
                    styles.getInt(
                        R.styleable.JoystickView_controlType,
                        0
                    )
                )
                directionType = Control.DirectionType.fromId(
                    styles.getInt(
                        R.styleable.JoystickView_directionType,
                        0
                    )
                )
                backgroundRes = getBackgroundResOf(controlType).let {
                    styles.getResourceId(R.styleable.JoystickView_background, it)
                }

                // arc types
                arcStrokeWidth = styles.getFloat(
                    R.styleable.JoystickView_arcControlDrawer_strokeWidth,
                    arcStrokeWidth
                )
                arcSweepAngle = styles.getFloat(
                    R.styleable.JoystickView_arcControlDrawer_sweepAngle,
                    arcSweepAngle
                )

                // circle types
                circleRadiusProportion = styles.getFloat(
                    R.styleable.JoystickView_circleControlDrawer_radiusProportion,
                    circleRadiusProportion
                )
            }
        } else {
            backgroundRes = getBackgroundResOf(controlType)
        }

        mControl = Control.Builder()
            .apply {
                drawer.primaryColor(primaryColor)
                    .accentColor(accentColor)
                    .arcStrokeWidth(arcStrokeWidth)
                    .arcSweepAngle(arcSweepAngle)
                    .circleRadiusRatio(circleRadiusProportion)
                    .type(controlType)
            }.directionType(directionType)
            .invalidRadius(invalidRadius)
            .build()


        background = try {
            getCompatDrawable(backgroundRes)
        } catch (_: Exception) {
            getCompatDrawable(getBackgroundResOf(controlType))
        }

    }

    /**
     * Gets a drawable resource from this resources.
     */
    private fun getCompatDrawable(@DrawableRes id: Int): Drawable? =
        ResourcesCompat.getDrawable(resources, id, context.theme)

    companion object {
        /**
         * The default interval in ms for the joystick [listener] call.
         */
        const val HOLD_INTERVAL: Long = 150

        /**
         * Checks if the value of [interval] is not less than zero.
         * @param interval A interval value in ms.
         * @return [HOLD_INTERVAL] if interval is less than zero; otherwise, the interval value.
         */
        @JvmStatic
        fun getHoldInterval(interval: Long): Long {
            return interval.coerceAtLeast(HOLD_INTERVAL)
        }

        /**
         * Gets the id of the drawable associated with the control type.
         * @param type The control type.
         */
        @JvmStatic
        @DrawableRes
        fun getBackgroundResOf(type: Control.DrawerType): Int {
            return when (type) {
                Control.DrawerType.ARC -> R.drawable.arcfor_bg
                else -> R.drawable.circlefor_bg
            }
        }
    }

    /**
     * Joystick control movement listener.
     */
    fun interface MoveListener {
        /**
         * Called when joystick control is moved or held down.
         * @param direction The control direction
         */
        fun onMove(direction: Control.Direction)
    }

    /**
     * A handler for control holds.
     */
    private class JoystickTouchHandler(
        joystick: JoystickView,
        interval: Long,
    ) : TouchHoldEventHandler<JoystickView>(joystick, interval) {
        override fun touchHold() {
            view.move()
        }

        override fun touchDown(): Boolean {
            view.move()
            return true
        }

        override fun touchUp(): Boolean {
            view.mControl.toCenter()
            view.move(Control.Direction.NONE)
            return true
        }

        override fun touchMove(): Boolean {
            view.move()
            return true
        }

        override fun notHandledTouch(event: MotionEvent): Boolean {
            return false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mControl.onSizeChanged(viewSize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var side = resources.getDimensionPixelSize(R.dimen.width)

        if (arrayOf(widthMode, heightMode).any { it == MeasureSpec.EXACTLY }) {
            val size = MeasureSpec.getSize(widthMeasureSpec)
                .coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
            if (size > 0)
                side = size
        }

        setMeasuredDimension(side, side)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mControl.onDraw(canvas)
    }


    private fun move(direction: Control.Direction) {
        listener?.onMove(direction)
    }

    private fun move() = move(mControl.direction)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return false

        val touchPosition = FixedPosition(event.x, event.y)
        if (event.action != MotionEvent.ACTION_MOVE && Plane.distanceBetween(
                touchPosition,
                center
            ) > viewRadius
        ) {
            if (!mControl.isInCenter()) {
                mControl.toCenter()
                invalidate()
            }
            return false
        }


        try {
            mControl.setPosition(touchPosition)
        } catch (e: LowerNumberException) {
            Logger.errorFromClass(this@JoystickView, e)
            return false
        }

        return touchHandler.onTouchEvent(event).let {
            invalidate()
            it
        }

    }

    /**
     * Changes the current joystick move listener.
     * @param listener The new listener.
     */
    fun setMoveListener(listener: MoveListener) {
        this.listener = listener
    }


    /**
     * Changes the current interval for the joystick listener call when the control is hold.
     * @param interval An interval value in ms.
     */
    fun setHoldInterval(interval: Int) {
        holdInterval = interval.toLong()
    }


    /**
     * Changes the current control to one defined in the package.
     *
     * If you also want to change the background for the [type], use [setTypeAndBackground] instead.
     * @param type The new control type.
     */
    fun setType(type: Control.DrawerType) {
        mControl.drawer = Control.DrawerBuilder
            .from(mControl.drawer)
            .type(type)
            .build()
        invalidate()
    }

    /**
     * Changes the current control to one defined in the package,
     * and also changes the background to the one associated with the [type] according to [getBackgroundResOf].
     *
     * If you only want to change the the [type], use [setType]
     * @param type The new control type.
     */
    fun setTypeAndBackground(type: Control.DrawerType) {
        setType(type)
        val drawable = getCompatDrawable(getBackgroundResOf(type))
        if (drawable != null)
            background = drawable
    }

    private val colorfulDrawer: ColorfulControlDrawer?
        get() = mControl.drawer
            .let {
                if (it is ColorfulControlDrawer)
                    it
                else null
            }

    /**
     * Changes the primary colour of the current control's drawer.
     *
     * If the current control's drawer is custom (not defined in the package)
     * that does not inherit from [ColorfulControlDrawer], nothing will be changed.
     */
    fun setPrimaryColor(@ColorInt color: Int) {
        colorfulDrawer?.primaryColor = color
        invalidate()
    }

    /**
     * Changes the accent colour of the current control's drawer.
     *
     * If the current control's drawer is custom (not defined in the package)
     * that does not inherit from [ColorfulControlDrawer], nothing will be changed.
     */
    fun setAccentColor(@ColorInt color: Int) {
        colorfulDrawer?.accentColor = color
        invalidate()
    }

    /**
     * Changes the colors of the current control's drawer.
     *
     * If the current control's drawer is custom (not defined in the package)
     * that does not inherit from [ColorfulControlDrawer], nothing will be changed.
     */
    fun setColors(colors: ColorsScheme) {
        colorfulDrawer?.setColors(colors)
        invalidate()
    }

    /**
     * Changes the [Control.invalidRadius] of the current control.
     */
    fun setInvalidRadius(radius: Float) {
        mControl.invalidRadius = radius
    }

    /**
     * Changes the [Control.directionType] property of the current control.
     */
    fun setDirectionType(type: Control.DirectionType) {
        mControl.directionType = type
    }

    /**
     * Changes the drawer of the current control drawer.
     */
    fun setControlDrawer(drawer: ControlDrawer) {
        mControl.drawer = drawer
        invalidate()
    }

    /**
     * Changes the current joystick control.
     * @param control The new [Control]
     */
    @Throws(LowerNumberException::class)
    fun setControl(control: Control) {
        mControl = control.apply {
            val size = viewSize
            if (!size.isEmpty()) {
                onSizeChanged(size)
                setPosition(mControl.position)
                invalidate()
            }
        }
    }
}