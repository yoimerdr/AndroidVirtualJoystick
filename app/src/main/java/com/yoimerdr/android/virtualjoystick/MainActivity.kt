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

    private var drawable: ControlDrawer? = null

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

                val drawer = drawable
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

        spnDrawerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val typeId = drawers[selected] ?: return

                if (typeId < 0) {
                    val color = colors[spnPrimaryColor.selectedItem] ?: return
                    drawable = when (typeId) {
                        -1 -> HighlightControlDrawer(color, 0.48f)
                        -2 -> {
                            val drawable = DrawableControlDrawer.getDrawable(
                                this@MainActivity,
                                R.drawable.baseline_adb_24
                            )
                            DrawableControlDrawer(drawable, color)
                        }

                        else -> null
                    }

                    if (drawable != null)
                        vJoystick.setControlDrawer(drawable!!)

                } else {
                    val type = Control.DrawerType.fromId(typeId)

                    drawable = Control
                        .DrawerBuilder()
                        .primaryColor(colors[spnPrimaryColor.selectedItem] ?: Color.RED)
                        .accentColor(colors[spnAccentColor.selectedItem] ?: Color.WHITE)
                        .type(type)
                        .build()

                    vJoystick.background = JoystickView.getBackgroundResOf(type)
                        .let {
                            DrawableControlDrawer.getDrawable(this@MainActivity, it)
                        }

                    vJoystick.setControlDrawer(drawable!!)

                }
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
            setMoveListener {
                tvDirection.text = directionFormat.format(it)

                val position = position
                val ndcPosition = ndcPosition
                tvPosition.text = positionFormat.format(position.x, position.y)
                tvNdcPosition.text = positionFormat.format(ndcPosition.x, ndcPosition.y)
            }
        }
    }

}