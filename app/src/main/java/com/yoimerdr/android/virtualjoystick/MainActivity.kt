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
    private val vJoystick: JoystickView get() = binding.vJoystick
    private val spnPrimaryColor: Spinner get() = binding.spnPrimaryColor
    private val spnAccentColor: Spinner get() = binding.spnAccentColor
    private val spnDrawerType: Spinner get() = binding.spnDrawerType
    private val spnDirectionType: Spinner get() = binding.spnDirectionType

    private var noDefaultDrawer: ControlDrawer? = null

    private val colors = mapOf(
        "Red" to Color.RED,
        "Blue" to Color.BLUE,
        "White" to Color.WHITE,
        "Black" to Color.BLACK,
        "Yellow" to Color.YELLOW
    )

    private val drawers = mapOf(
        "Circle" to Control.DefaultType.CIRCLE.id,
        "Arc" to Control.DefaultType.ARC.id,
        "Circle Arc" to Control.DefaultType.CIRCLE_ARC.id,
        "Highlight" to -1, "Drawable" to -2
    )

    private val directionTypes = mapOf(
        "Complete" to JoystickView.DirectionType.COMPLETE,
        "Simple" to JoystickView.DirectionType.SIMPLE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        iniViews()
        initEvents()
    }

    private fun simpleArrayAdapter(items: List<String>) = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

    private fun iniViews() {
        spnPrimaryColor.adapter = simpleArrayAdapter(colors.keys.toList())
        spnAccentColor.adapter = simpleArrayAdapter(colors.keys.toList())
        spnDrawerType.adapter = simpleArrayAdapter(drawers.keys.toList())
        spnDirectionType.adapter = simpleArrayAdapter(directionTypes.keys.toList())
    }

    private fun initEvents() {
        spnPrimaryColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val color = colors[selected] ?: return

                val drawer = noDefaultDrawer
                if(drawer != null && drawer is DrawableControlDrawer) {
                    if(drawer.drawable is VectorDrawable)
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
                id: Long
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
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val typeId = drawers[selected] ?: return

                if(typeId < 0) {
                    val color = colors[spnPrimaryColor.selectedItem] ?: return
                    noDefaultDrawer = when (typeId) {
                        -1 -> HighlightControlDrawer(color, 0.48f)
                        -2 -> {
                            val drawable = DrawableControlDrawer.getDrawable(this@MainActivity, R.drawable.baseline_adb_24)
                            if(drawable is VectorDrawable)
                                drawable.setTint(color)
                            DrawableControlDrawer(drawable)
                        }
                        else -> null
                    }

                    if(noDefaultDrawer != null)
                        vJoystick.setControlDrawer(noDefaultDrawer!!)

                } else {
                    noDefaultDrawer = null
                    vJoystick.setTypeAndBackground(Control.DefaultType.fromId(typeId))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        spnDirectionType.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent?.getItemAtPosition(position) ?: return
                val type = directionTypes[selected] ?: return
                vJoystick.setDirectionType(type)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }


        vJoystick.apply {
            setMoveListener {
                tvDirection.text = getString(R.string.joystick_direction).format(it.name)
                val position = this.position
                tvPosition.text = getString(R.string.joystick_position).format(position.x, position.y)
            }
        }
    }

}