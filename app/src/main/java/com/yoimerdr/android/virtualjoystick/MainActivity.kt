package com.yoimerdr.android.virtualjoystick

import android.graphics.Color
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yoimerdr.android.virtualjoystick.databinding.ActivityMainBinding
import com.yoimerdr.android.virtualjoystick.control.Control
import com.yoimerdr.android.virtualjoystick.control.drawer.ControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.DrawableControlDrawer
import com.yoimerdr.android.virtualjoystick.control.drawer.HighlightControlDrawer
import com.yoimerdr.android.virtualjoystick.utils.log.Logger
import com.yoimerdr.android.virtualjoystick.views.JoystickView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val tvDirection: TextView get() = binding.tvDirection
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

    private var drawer: ControlDrawer? = null

    private val colors = mapOf(
        "Red" to Color.RED,
        "Blue" to Color.BLUE,
        "White" to Color.WHITE,
        "Black" to Color.BLACK,
        "Yellow" to Color.YELLOW
    )

    private val drawers = mapOf(
        "Circle" to Control.DrawerType.CIRCLE.ordinal,
        "Arc" to Control.DrawerType.ARC.ordinal,
        "Circle Arc" to Control.DrawerType.CIRCLE_ARC.ordinal,
        "Highlight" to -1, "Drawable" to -2
    )

    private val directionTypes = mapOf(
        "Complete" to Control.DirectionType.COMPLETE,
        "Simple" to Control.DirectionType.SIMPLE
    )

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

                val drawer = drawer
                if (drawer != null && drawer is DrawableControlDrawer) {
                    if (drawer.drawable is VectorDrawable)
                        drawer.drawable = drawer.drawable.apply {
                            setTint(color)
                        }
                }

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

        btnForceDirection.setOnClickListener {
            vJoystick.move(direction)
        }

        vJoystick.apply {
            setMoveStartListener {
                onMovement(it)
                Logger.log("START: $it")
            }

            setMoveEndListener {
                onMovement(Control.Direction.NONE)
                Logger.log("END")
            }

            setMoveListener {
                onMovement(it)
                Logger.log("MOVE: $it")
            }
        }
    }

    private fun updateDrawer(drawerSelected: Any) {
        val typeId = drawers[drawerSelected] ?: return
        if (typeId < 0) {
            val color = colors[spnPrimaryColor.selectedItem] ?: return
            drawer = when (typeId) {
                -1 -> HighlightControlDrawer(color, 0.48f)
                -2 -> {

                    DrawableControlDrawer.fromDrawableRes(
                        this@MainActivity,
                        R.drawable.baseline_adb_24,
                        color,
                        bounded
                    )
                }

                else -> null
            }

            if (drawer != null)
                vJoystick.setControlDrawer(drawer!!)

        } else {
            val builder = if (drawer == null)
                Control.DrawerBuilder()
            else Control.DrawerBuilder.from(drawer!!)

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
        }
    }

    private fun onMovement(direction: Control.Direction) {
        tvDirection.text = directionFormat.format(direction)

        vJoystick.apply {
            val position = position
            val ndcPosition = ndcPosition
            tvPosition.text = positionFormat.format(position.x, position.y)
            tvNdcPosition.text = positionFormat.format(ndcPosition.x, ndcPosition.y)
        }
    }

}