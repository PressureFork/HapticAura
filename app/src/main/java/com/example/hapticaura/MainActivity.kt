package com.example.hapticaura

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Building UI programmatically so you don't need XML layout files
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }

        val title = TextView(this).apply { text = "Haptic Aura Settings"; textSize = 24f }
        val prefs = getSharedPreferences("haptic_config", Context.MODE_PRIVATE)

        // 1. Enable Switch
        val enableSwitch = Switch(this).apply {
            text = "Master Switch"
            isChecked = prefs.getBoolean("is_active", true)
            setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putBoolean("is_active", isChecked).apply()
            }
        }

        // 2. Speed Slider (Debounce)
        val speedLabel = TextView(this).apply { text = "Scroll Gap (Speed): 40ms" }
        val speedSeek = SeekBar(this).apply {
            max = 100 // 0 to 100ms
            progress = prefs.getInt("scroll_speed", 40)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, v: Int, p2: Boolean) {
                    val safeVal = if (v < 10) 10 else v
                    prefs.edit().putInt("scroll_speed", safeVal).apply()
                    speedLabel.text = "Scroll Gap: ${safeVal}ms"
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }

        // 3. Intensity Slider (1-3)
        val intensityLabel = TextView(this).apply { text = "Intensity: Medium" }
        val intensitySeek = SeekBar(this).apply {
            max = 2 // 0, 1, 2 maps to Light, Med, Heavy
            progress = prefs.getInt("intensity", 2) - 1
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, v: Int, p2: Boolean) {
                    val realVal = v + 1
                    prefs.edit().putInt("intensity", realVal).apply()
                    val mode = when(realVal) { 1 -> "Light"; 2 -> "Med"; else -> "Heavy" }
                    intensityLabel.text = "Intensity: $mode"
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }

        // 4. Permission Button
        val permButton = Button(this).apply {
            text = "Enable Accessibility Service"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        // Add views
        layout.addView(title)
        layout.addView(enableSwitch)
        layout.addView(TextView(this).apply { text = "\n" }) // spacer
        layout.addView(speedLabel)
        layout.addView(speedSeek)
        layout.addView(TextView(this).apply { text = "\n" }) // spacer
        layout.addView(intensityLabel)
        layout.addView(intensitySeek)
        layout.addView(TextView(this).apply { text = "\n\n" }) // spacer
        layout.addView(permButton)

        setContentView(layout)
    }
}
