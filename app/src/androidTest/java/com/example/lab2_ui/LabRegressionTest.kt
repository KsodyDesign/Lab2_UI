package com.example.lab2_ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.widget.EditText
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import java.io.File
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class LabRegressionTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private lateinit var activity: Activity
    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
    private fun launch(type: Class<out Activity>) {
        activity = instrumentation.startActivitySync(Intent(context, type).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        main { activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        Thread.sleep(1200)
        instrumentation.waitForIdleSync()
        current()
    }
    private fun enter(id: Int, text: String) = main { activity.findViewById<EditText>(id).setText(text) }
    private fun click(id: Int) {
        Thread.sleep(2400)
        main { activity.findViewById<android.view.View>(id).performClick() }
        instrumentation.waitForIdleSync()
        Thread.sleep(350)
    }
    private fun text(id: Int): String {
        var result = ""
        main { result = activity.findViewById<TextView>(id).text.toString() }
        return result
    }
    private fun current() {
        main { activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).first() }
    }
    private fun rotate() {
        val old = activity
        main { activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        Thread.sleep(1800)
        instrumentation.waitForIdleSync()
        current()
        assertNotSame("Activity must recreate on rotation", old, activity)
    }
    private fun screenshot(name: String) {
        instrumentation.waitForIdleSync()
        Thread.sleep(1000)
        val file = File(context.getExternalFilesDir(null), "audit/$name.png")
        file.parentFile!!.mkdirs()
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }
    private fun finish() {
        main { activity.finish() }
        instrumentation.waitForIdleSync()
    }
    private fun field(name: String): Any? {
        val field = activity.javaClass.getDeclaredField(name)
        field.isAccessible = true
        return field.get(activity)
    }

    @Test fun formAndNavigation() {
        launch(MainActivity::class.java)
        click(R.id.btnSubmit)
        screenshot("01-empty-name")
        enter(R.id.etName, "Ivan")
        enter(R.id.etAge, "999999999999999")
        click(R.id.btnSubmit)
        screenshot("02-invalid-age")
        enter(R.id.etAge, "20")
        click(R.id.btnSubmit)
        screenshot("03-missing-agreement")
        main { activity.findViewById<android.widget.CheckBox>(R.id.cbAgree).isChecked = true }
        click(R.id.btnSubmit)
        screenshot("04-form-success")
        enter(R.id.etMessage, "Lab 2 Intent test")
        click(R.id.btnGo)
        current()
        assertTrue(activity is DetailActivity)
        assertTrue(text(R.id.tvResult).contains("Lab 2 Intent test"))
        screenshot("05-detail")
        click(R.id.btnBack)
        current()
        assertTrue(activity is MainActivity)
        rotate()
        assertEquals("Ivan", text(R.id.etName))
        screenshot("06-landscape")
        finish()
    }
}
