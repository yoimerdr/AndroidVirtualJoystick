package com.yoimerdr.android.virtualjoystick

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yoimerdr.android.virtualjoystick.databinding.ActivityMainBinding
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.drawer.core.ControlDrawer
import com.yoimerdr.android.virtualjoystick.api.log.LoggerSupplier.DefaultLogger
import com.yoimerdr.android.virtualjoystick.drawer.drawable.DrawableDrawer
import com.yoimerdr.android.virtualjoystick.drawer.drawable.DirectionalDrawableDrawer
import com.yoimerdr.android.virtualjoystick.views.JoystickView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val tvDirection: TextView get() = binding.tvDirection
    private val tvMagnitude: TextView get() = binding.tvMagnitude
    private val tvPosition: TextView get() = binding.tvPosition
    private val tvNdcPosition: TextView get() = binding.tvNdcPosition
    private val vJoystick: JoystickView get() = binding.vJoystick
    private val spnPrimaryColor: Spinner get() = binding.spnPrimaryColor
    private val spnAccentColor: Spinner get() = binding.spnAccentColor
    private val spnDrawerType: Spinner get() = binding.spnDrawerType
    private val spnDirectionType: Spinner get() = binding.spnDirectionType
    private val spnForceDirection: Spinner get() = binding.spnForceDirection
    private val btnForceDirection: FloatingActionButton get() = binding.btnForceDirection
    private val spnBounded: Spinner get() = binding.spnBounded
    private val tvForceMagnitude: TextView get() = binding.tvForceMagnitude
    private val sbForceMagnitude: SeekBar get() = binding.sbForceMagnitude

    private var drawer: ControlDrawer? = null

    private val colors = mapOf(
        "Red" to Color.RED,
        "Blue" to Color.BLUE,
        "White" to Color.WHITE,
        "Black" to Color.BLACK,
        "Yellow" to Color.YELLOW
    )

    private val drawers = Control.DrawerType.entries.associate {
        it.titleName() to it.ordinal
    } + mapOf(
        "Drawable" to -1,
        "State Drawable" to -2,
    )


    private val directionTypes = Control.DirectionType.entries.associateBy {
        it.titleName()
    }

    private val boundedTypes = mapOf(
        "True" to true,
        "False" to false
    )

    private var bounded = true

    private val directions = Control.Direction.entries
        .associateBy { it.name }

    private var direction = Control.Direction.NONE

    private lateinit var positionFormat: String
    private lateinit var directionFormat: String
    private lateinit var magnitudeFormat: String
    private lateinit var ndcPositionFormat: String

    companion object {
        fun String.capitalize(): String {
            return this.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() }
        }

        fun Enum<*>.titleName(): String {
            val name = name
                .lowercase()
                .split("_")
                .joinToString(separator = " ") {
                    it.capitalize()
                }

            return name.capitalize()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        iniViews()
        initEvents()
    }

    private fun simpleArrayAdapter(items: List<String>) =
        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

    private fun iniViews() {
        spnPrimaryColor.adapter = simpleArrayAdapter(colors.keys.toList())
        spnAccentColor.adapter = simpleArrayAdapter(colors.keys.toList())
        spnDrawerType.adapter = simpleArrayAdapter(drawers.keys.toList())
        spnDirectionType.adapter = simpleArrayAdapter(directionTypes.keys.toList())
        spnForceDirection.adapter = simpleArrayAdapter(directions.keys.toList())
        spnBounded.adapter = simpleArrayAdapter(boundedTypes.keys.toList())
        directionFormat = getString(R.string.joystick_direction)
        positionFormat = getString(R.string.joystick_position)
        magnitudeFormat = getString(R.string.joystick_magnitude)
        ndcPositionFormat = getString(R.string.joystick_ndc_position)
    }

    private fun initEvents() {
        spnPrimaryColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val color = colors[selected] ?: return

                vJoystick.setPrimaryColor(color)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spnAccentColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val color = colors[selected] ?: return
                vJoystick.setAccentColor(color)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        spnBounded.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val bounded = boundedTypes[selected] ?: true

                if (bounded != this@MainActivity.bounded) {
                    this@MainActivity.bounded = bounded
                    updateDrawer(spnDrawerType.selectedItem)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (!bounded) {
                    bounded = true
                    updateDrawer(spnDrawerType.selectedItem)
                }
            }
        }

        spnDrawerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                updateDrawer(parent?.getItemAtPosition(position) ?: return)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        spnDirectionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val type = directionTypes[selected] ?: return
                vJoystick.setDirectionType(type)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        spnForceDirection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                direction = directions[selected] ?: Control.Direction.NONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                direction = Control.Direction.NONE
            }
        }

        sbForceMagnitude.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    tvForceMagnitude.text = magnitudeFormat.format(progress / 100f)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            }
        )

        btnForceDirection.setOnClickListener {
            val magnitude = sbForceMagnitude.progress / 100f
            vJoystick.move(direction, magnitude)
        }


        vJoystick.apply {
            setMoveStartListener {
                onMovement(it)
                DefaultLogger.log("START: $it")
            }

            setMoveEndListener {
                onMovement(Control.Direction.NONE)
                DefaultLogger.log("END")
            }

            setMoveListener {
                onMovement(it)
                DefaultLogger.log("MOVE: $it")
            }
        }
    }

    private fun updateDrawer(drawerSelected: Any) {
        val typeId = drawers[drawerSelected] ?: return
        if (typeId < 0) {
            val color = colors[spnPrimaryColor.selectedItem] ?: return
            tvDirection.isPressed
            drawer = when (typeId) {
                -1 -> DrawableDrawer.fromDrawableRes(
                    this@MainActivity,
                    R.drawable.baseline_adb_24,
                    color,
                    bounded
                )
                -2 -> DirectionalDrawableDrawer(
                    listOf<Pair<Control.Direction, Int>>(
                        Control.Direction.RIGHT to R.drawable.dpad_modern_r,
                        Control.Direction.UP to R.drawable.dpad_modern_u,
                        Control.Direction.LEFT to R.drawable.dpad_modern_l,
                        Control.Direction.DOWN to R.drawable.dpad_modern_d,
                    ).map {
                        it.first to DrawableDrawer.getDrawable(
                            this@MainActivity,
                            it.second
                        )
                    }

                )

                else -> null
            }

            if (drawer != null)
                vJoystick.setControlDrawer(drawer!!)

        } else {
            val builder = if (drawer == null)
                Control.DrawerBuilder()
            else Control.DrawerBuilder.from(drawer!!)

            val drawerType = Control.DrawerType
                .fromId(typeId)

            if (drawerType in listOf(
                    Control.DrawerType.WEDGE,
                )
            )
                builder.circleRadiusRatio(0.43f)
//                    .circleRadius(DrawerRadius.Zero)

            drawer = builder
                .colors(
                    colors[spnPrimaryColor.selectedItem] ?: Color.RED,
                    colors[spnAccentColor.selectedItem] ?: Color.BLUE
                )
                .type(
                    Control.DrawerType
                        .fromId(typeId)
                )
                .bounded(bounded)
                .build()

            vJoystick.setControlDrawer(
                drawer!!
            )
//            vJoystick.setInvalidRadius(0f)
        }
    }

    private fun onMovement(direction: Control.Direction) {
        tvDirection.text = directionFormat.format(direction)

        vJoystick.apply {
            val position = position
            val ndcPosition = ndcPosition
            val magnitude = magnitude
            tvMagnitude.text = magnitudeFormat.format(magnitude)
            tvPosition.text = positionFormat.format(position.x, position.y)
            tvNdcPosition.text = ndcPositionFormat.format(ndcPosition.x, ndcPosition.y)

        }
    }

}