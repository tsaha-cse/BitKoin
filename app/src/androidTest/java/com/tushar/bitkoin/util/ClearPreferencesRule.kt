package com.tushar.bitkoin.util

import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.util.ArrayList

/**
 * This rule clears all app's SharedPreferences before running each test
 */
class ClearPreferencesRule : TestRule {

    private val allPreferencesFiles: List<SharedPreferences>
        get() {
            val context =
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            val rootPath = context.applicationInfo.dataDir + "/shared_prefs"
            val prefsFolder = File(rootPath)

            val children = prefsFolder.list() ?: return emptyList()

            val allPrefs = ArrayList<SharedPreferences>()
            for (prefFileName in children) {
                val prefName = if (prefFileName.endsWith(".xml")) {
                    prefFileName.substring(0, prefFileName.indexOf(".xml"))
                } else prefFileName
                val prefs = context.getSharedPreferences(prefName, 0)
                allPrefs.add(prefs)
            }
            return allPrefs
        }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                clearData()
                base.evaluate()
                clearData()
            }
        }
    }

    private fun clearData() {
        val allPrefs = allPreferencesFiles
        for (prefs in allPrefs) {
            prefs.edit().clear().apply()
        }
    }
}
