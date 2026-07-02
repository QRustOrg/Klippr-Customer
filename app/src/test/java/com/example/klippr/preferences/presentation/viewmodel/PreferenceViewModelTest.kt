package com.example.klippr.preferences.presentation.viewmodel

import com.example.klippr.preferences.data.store.PreferenceStore
import com.example.klippr.preferences.domain.model.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_exposesCurrentPreference() = runTest(dispatcher) {
        val viewModel = PreferenceViewModel(FakePreferenceStore(preference()))

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals("consumer-1", viewModel.state.value.preference?.userId)
        assertEquals("America/Lima", viewModel.state.value.preference?.timezone)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun load_exposesErrorWhenPreferenceCannotLoad() = runTest(dispatcher) {
        val viewModel = PreferenceViewModel(FakePreferenceStore(preference(), failLoad = true))

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.preference)
        assertEquals("Error 500", viewModel.state.value.error)
    }

    @Test
    fun savePreference_updatesBackendAndState() = runTest(dispatcher) {
        val store = FakePreferenceStore(preference())
        val viewModel = PreferenceViewModel(store)
        advanceUntilIdle()

        val edited = store.preference.copy(
            languageCode = "en",
            timezone = "America/Bogota",
            darkMode = true,
            pushNotifications = false,
            profileVisibility = "public",
        )
        viewModel.savePreference(edited)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSaving)
        assertEquals(edited, store.savedPreference)
        assertEquals("en", viewModel.state.value.preference?.languageCode)
        assertEquals(true, viewModel.state.value.preference?.darkMode)
        assertEquals("Preferencias guardadas", viewModel.state.value.saveMessage)
    }

    @Test
    fun savePreference_exposesErrorAndKeepsPreviousPreference() = runTest(dispatcher) {
        val original = preference()
        val viewModel = PreferenceViewModel(FakePreferenceStore(original, failSave = true))
        advanceUntilIdle()

        viewModel.savePreference(original.copy(languageCode = "en"))
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSaving)
        assertEquals(original, viewModel.state.value.preference)
        assertEquals("No se pudieron guardar las preferencias", viewModel.state.value.error)
    }

    @Test
    fun consumeSaveMessage_clearsSuccessMessage() = runTest(dispatcher) {
        val store = FakePreferenceStore(preference())
        val viewModel = PreferenceViewModel(store)
        advanceUntilIdle()

        viewModel.savePreference(store.preference.copy(emailNotifications = false))
        advanceUntilIdle()
        viewModel.consumeSaveMessage()

        assertNull(viewModel.state.value.saveMessage)
    }

    private class FakePreferenceStore(
        initialPreference: UserPreference,
        private val failLoad: Boolean = false,
        private val failSave: Boolean = false,
    ) : PreferenceStore {
        var preference: UserPreference = initialPreference
            private set
        var savedPreference: UserPreference? = null
            private set

        override suspend fun getOrCreateCurrentPreference(): UserPreference {
            if (failLoad) throw IllegalStateException("Error 500")
            return preference
        }

        override suspend fun updatePreference(preference: UserPreference): UserPreference {
            if (failSave) throw IllegalStateException("No se pudieron guardar las preferencias")
            savedPreference = preference
            this.preference = preference
            return preference
        }
    }
}

private fun preference() = UserPreference.defaults("consumer-1").copy(id = 7)
