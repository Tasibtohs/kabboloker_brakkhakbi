package com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.preferences.ThemePreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = ThemePreferencesRepository(context)
                    val isMasterEnabled = repository.isReminderMasterEnabled.first()
                    val isDailyEnabled = repository.isDailyReminderEnabled.first()
                    val hour = repository.reminderHour.first()
                    val minute = repository.reminderMinute.first()

                    if (isMasterEnabled && isDailyEnabled) {
                        DailyReminderManager.scheduleDailyReminder(context, hour, minute)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
