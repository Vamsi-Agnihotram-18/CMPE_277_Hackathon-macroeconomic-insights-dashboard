package com.example.hackathon

// Import statements at the top of your file
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_annotations_data")

object AnnotationsDataStore {

    private val MACRO_ANNOTATION_TEXT_KEY = stringPreferencesKey("macro_annotation_text")
    private val AGRI_ANNOTATION_TEXT_KEY = stringPreferencesKey("agri_annotation_text")
    private val DEBT_ANNOTATION_TEXT_KEY = stringPreferencesKey("debt_annotation_text")

    // Functions for annotation texts
    fun getMacroAnnotationText(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[MACRO_ANNOTATION_TEXT_KEY] ?: ""
        }
    }

    suspend fun saveMacroAnnotationText(context: Context, text: String) {
        context.dataStore.edit { preferences ->
            preferences[MACRO_ANNOTATION_TEXT_KEY] = text
        }
    }

    fun getAgriAnnotationText(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[AGRI_ANNOTATION_TEXT_KEY] ?: ""
        }
    }

    suspend fun saveAgriAnnotationText(context: Context, text: String) {
        context.dataStore.edit { preferences ->
            preferences[AGRI_ANNOTATION_TEXT_KEY] = text
        }
    }

    fun getDebtAnnotationText(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[DEBT_ANNOTATION_TEXT_KEY] ?: ""
        }
    }

    suspend fun saveDebtAnnotationText(context: Context, text: String) {
        context.dataStore.edit { preferences ->
            preferences[DEBT_ANNOTATION_TEXT_KEY] = text
        }
    }
}


class DataViewModel(application: Application) : AndroidViewModel(application) {
    val macroAnnotationTextFlow: Flow<String> = AnnotationsDataStore.getMacroAnnotationText(getApplication())
    val agriAnnotationTextFlow: Flow<String> = AnnotationsDataStore.getAgriAnnotationText(getApplication())
    val debtAnnotationTextFlow: Flow<String> = AnnotationsDataStore.getDebtAnnotationText(getApplication())

    fun saveMacroAnnotationText(text: String) {
        viewModelScope.launch {
            AnnotationsDataStore.saveMacroAnnotationText(getApplication(), text)
        }
    }

    fun saveAgriAnnotationText(text: String) {
        viewModelScope.launch {
            AnnotationsDataStore.saveAgriAnnotationText(getApplication(), text)
        }
    }

    fun saveDebtAnnotationText(text: String) {
        viewModelScope.launch {
            AnnotationsDataStore.saveDebtAnnotationText(getApplication(), text)
        }
    }
}
