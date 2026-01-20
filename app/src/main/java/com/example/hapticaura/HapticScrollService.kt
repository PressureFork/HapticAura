package com.example.hapticaura

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.accessibility.AccessibilityEvent

class HapticScrollService : AccessibilityService() {

    private lateinit var vibrator: Vibrator
    private lateinit var prefs: SharedPreferences
    private var lastScrollTime = 0L

    override fun onServiceConnected() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        prefs = getSharedPreferences("haptic_config", Context.MODE_PRIVATE)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!prefs.getBoolean("is_active", true)) return

        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                val speed = prefs.getInt("scroll_speed", 40).toLong() // User defined speed
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - lastScrollTime > speed) {
                    playTick()
                    lastScrollTime = currentTime
                }
            }
        }
    }

    private fun playTick() {
        if (!vibrator.hasVibrator()) return
        
        val intensity = prefs.getInt("intensity", 2) // 1=Light, 2=Med, 3=Heavy
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effectId = when(intensity) {
                1 -> VibrationEffect.EFFECT_TICK
                2 -> VibrationEffect.EFFECT_CLICK
                3 -> VibrationEffect.EFFECT_HEAVY_CLICK
                else -> VibrationEffect.EFFECT_TICK
            }
            vibrator.vibrate(VibrationEffect.createPredefined(effectId))
        } else {
            vibrator.vibrate(10)
        }
    }

    override fun onInterrupt() {}
}
