package com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

import org.json.JSONArray
import org.json.JSONObject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kabyolokor_settings")

data class CustomThemePayload(
    val id: String = "custom_${System.currentTimeMillis()}",
    val nameBn: String,
    val primaryHex: String,
    val secondaryHex: String = "#C084FC",
    val darkBgHex: String = "#000000",
    val lightBgHex: String = "#F8F5EC"
)

class ThemePreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_THEME_ID = stringPreferencesKey("selected_theme_id")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val APP_PASSWORD_HASH = stringPreferencesKey("app_password_hash")
        val SECURITY_QUESTION = stringPreferencesKey("security_question")
        val SECURITY_ANSWER_HASH = stringPreferencesKey("security_answer_hash")
        val EDITOR_TOP_BAR_NAME = stringPreferencesKey("editor_top_bar_name")
        val IS_NOTIFICATIONS_ENABLED = booleanPreferencesKey("is_notifications_enabled")
        val FONT_SIZE_PREFERENCE = stringPreferencesKey("font_size_preference")
        val CUSTOM_THEMES_JSON = stringPreferencesKey("custom_themes_json")
        val VIEW_MODE_PREFERENCE = stringPreferencesKey("view_mode_preference")
    }

    val customThemesPayload: Flow<List<CustomThemePayload>> = context.dataStore.data.map { preferences ->
        val jsonStr = preferences[PreferencesKeys.CUSTOM_THEMES_JSON] ?: "[]"
        parseCustomThemesJson(jsonStr)
    }

    suspend fun saveCustomTheme(payload: CustomThemePayload) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.CUSTOM_THEMES_JSON] ?: "[]"
            val currentList = parseCustomThemesJson(currentJson).toMutableList()
            val index = currentList.indexOfFirst { it.id == payload.id }
            if (index >= 0) {
                currentList[index] = payload
            } else {
                currentList.add(payload)
            }
            preferences[PreferencesKeys.CUSTOM_THEMES_JSON] = encodeCustomThemesJson(currentList)
        }
    }

    suspend fun deleteCustomTheme(themeId: String) {
        context.dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.CUSTOM_THEMES_JSON] ?: "[]"
            val currentList = parseCustomThemesJson(currentJson).filterNot { it.id == themeId }
            preferences[PreferencesKeys.CUSTOM_THEMES_JSON] = encodeCustomThemesJson(currentList)
            if (preferences[PreferencesKeys.SELECTED_THEME_ID] == themeId) {
                preferences[PreferencesKeys.SELECTED_THEME_ID] = "royal_gold"
            }
        }
    }

    private fun parseCustomThemesJson(jsonStr: String): List<CustomThemePayload> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<CustomThemePayload>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CustomThemePayload(
                        id = obj.optString("id", "custom_$i"),
                        nameBn = obj.optString("nameBn", "কাস্টম থিম"),
                        primaryHex = obj.optString("primaryHex", "#FFD700"),
                        secondaryHex = obj.optString("secondaryHex", "#C084FC"),
                        darkBgHex = obj.optString("darkBgHex", "#000000"),
                        lightBgHex = obj.optString("lightBgHex", "#F8F5EC")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun encodeCustomThemesJson(list: List<CustomThemePayload>): String {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("nameBn", item.nameBn)
            obj.put("primaryHex", item.primaryHex)
            obj.put("secondaryHex", item.secondaryHex)
            obj.put("darkBgHex", item.darkBgHex)
            obj.put("lightBgHex", item.lightBgHex)
            array.put(obj)
        }
        return array.toString()
    }

    val selectedThemeId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_THEME_ID] ?: if (preferences[PreferencesKeys.IS_DARK_MODE] == false) "classic_paper" else "royal_gold"
    }

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DARK_MODE]
    }

    suspend fun setSelectedThemeId(themeId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME_ID] = themeId
        }
    }

    val isNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_NOTIFICATIONS_ENABLED] ?: true
    }

    val fontSizePreference: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FONT_SIZE_PREFERENCE] ?: "medium"
    }

    val viewModePreference: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VIEW_MODE_PREFERENCE] ?: "CARD"
    }

    val editorTopBarName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EDITOR_TOP_BAR_NAME] ?: "কাব্যলোকের ব্রক্ষকবি"
    }

    val appPasswordHash: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.APP_PASSWORD_HASH]
    }

    val securityQuestion: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SECURITY_QUESTION]
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDark
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setFontSizePreference(size: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE_PREFERENCE] = size
        }
    }

    suspend fun setViewModePreference(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIEW_MODE_PREFERENCE] = mode
        }
    }

    suspend fun setEditorTopBarName(name: String) {
        context.dataStore.edit { preferences ->
            if (name.isBlank() || name == "কাব্যলোকের ব্রক্ষকবি") {
                preferences.remove(PreferencesKeys.EDITOR_TOP_BAR_NAME)
            } else {
                preferences[PreferencesKeys.EDITOR_TOP_BAR_NAME] = name.trim()
            }
        }
    }

    suspend fun resetEditorTopBarName() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.EDITOR_TOP_BAR_NAME)
        }
    }

    suspend fun setAppPassword(password: String, question: String = "", answer: String = "") {
        val hash = hashString(password)
        val answerHash = if (answer.isNotEmpty()) hashString(answer.lowercase().trim()) else ""
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_PASSWORD_HASH] = hash
            if (question.isNotEmpty()) {
                preferences[PreferencesKeys.SECURITY_QUESTION] = question
            }
            if (answerHash.isNotEmpty()) {
                preferences[PreferencesKeys.SECURITY_ANSWER_HASH] = answerHash
            }
        }
    }

    suspend fun clearAppPassword() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.APP_PASSWORD_HASH)
            preferences.remove(PreferencesKeys.SECURITY_QUESTION)
            preferences.remove(PreferencesKeys.SECURITY_ANSWER_HASH)
        }
    }

    fun verifyPassword(inputPassword: String, savedHash: String?): Boolean {
        if (savedHash == null || savedHash.isEmpty()) return true
        return hashString(inputPassword) == savedHash
    }

    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
