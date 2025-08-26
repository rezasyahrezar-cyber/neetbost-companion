package com.netboost.companion

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MainActivity : Activity() {
    private val profilePath = "/data/adb/modules/netboost.advanced.universal/profile.conf"
    private val logPath = "/data/adb/modules/netboost.advanced.universal/netboost.log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rg = findViewById<RadioGroup>(R.id.radioGroup)
        val stable = findViewById<RadioButton>(R.id.rbStable)
        val gaming = findViewById<RadioButton>(R.id.rbGaming)
        val download = findViewById<RadioButton>(R.id.rbDownload)
        val applyBtn = findViewById<Button>(R.id.btnApply)
        val viewLogBtn = findViewById<Button>(R.id.btnViewLog)
        val tvLog = findViewById<TextView>(R.id.tvLog)

        // Load current profile if possible
        try {
            val f = File(profilePath)
            if (f.exists()) {
                val p = f.readText().trim()
                when(p) {
                    "stable" -> stable.isChecked = true
                    "gaming" -> gaming.isChecked = true
                    "download" -> download.isChecked = true
                }
            }
        } catch (e: Exception) {
            // ignore
        }

        applyBtn.setOnClickListener {
            val selected = when (rg.checkedRadioButtonId) {
                R.id.rbStable -> "stable"
                R.id.rbGaming -> "gaming"
                R.id.rbDownload -> "download"
                else -> "stable"
            }
            // write using su (requires root)
            try {
                val cmd = arrayOf("su", "-c", "sh -c 'echo ${'$'}1 > ${'$'}2'", selected, profilePath)
                Runtime.getRuntime().exec(cmd).waitFor()
                Toast.makeText(this, "Profile applied: $selected", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error applying profile: ${'$'}{e.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewLogBtn.setOnClickListener {
            try {
                val f = File(logPath)
                if (!f.exists()) {
                    tvLog.text = "Log file not found: $logPath"
                } else {
                    tvLog.text = f.readText()
                }
            } catch (e: Exception) {
                tvLog.text = "Error reading log: ${'$'}{e.message}"
            }
        }
    }
}
