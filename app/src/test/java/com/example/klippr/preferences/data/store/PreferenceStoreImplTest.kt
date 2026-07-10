package com.example.klippr.preferences.data.store

import com.example.klippr.preferences.data.network.PreferenceWebService
import com.example.klippr.preferences.domain.model.UserPreference
import com.example.klippr.preferences.domain.model.UserPreferenceRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreferenceStoreImplTest {

    @Test
    fun getOrCreateCurrentPreference_returnsPreferenceForCurrentUser() = runTest {
        val service = FakePreferenceWebService(
            preferences = listOf(
                UserPreference.defaults("other-user").copy(id = 1),
                UserPreference.defaults("consumer-1").copy(id = 2, languageCode = "en"),
            ),
        )
        val store = PreferenceStoreImpl(service) { "consumer-1" }

        val preference = store.getOrCreateCurrentPreference()

        assertFalse(service.created)
        assertEquals(2, preference.id)
        assertEquals("en", preference.languageCode)
    }

    @Test
    fun getOrCreateCurrentPreference_createsDefaultWhenMissing() = runTest {
        val service = FakePreferenceWebService(preferences = emptyList())
        val store = PreferenceStoreImpl(service) { "consumer-1" }

        val preference = store.getOrCreateCurrentPreference()

        assertTrue(service.created)
        assertEquals("consumer-1", service.lastCreateRequest?.userId)
        assertEquals("America/Lima", service.lastCreateRequest?.timezone)
        assertEquals(false, service.lastCreateRequest?.darkMode)
        assertEquals(99, preference.id)
    }

    @Test
    fun updatePreference_sendsFullRequestBody() = runTest {
        val service = FakePreferenceWebService(preferences = emptyList())
        val store = PreferenceStoreImpl(service) { "consumer-1" }
        val edited = UserPreference.defaults("consumer-1").copy(
            id = 5,
            darkMode = true,
            languageCode = "en",
            timezone = "America/Bogota",
            emailNotifications = false,
            pushNotifications = false,
            smsNotifications = true,
            profileVisibility = "public",
            dataSharingConsent = true,
        )

        store.updatePreference(edited)

        assertEquals(5, service.lastUpdateId)
        assertEquals(edited.toExpectedRequest(), service.lastUpdateRequest)
    }

    @Test
    fun updatePreference_withoutIdThrowsClearError() = runTest {
        val store = PreferenceStoreImpl(FakePreferenceWebService(emptyList())) { "consumer-1" }

        val exception = runCatching {
            store.updatePreference(UserPreference.defaults("consumer-1"))
        }.exceptionOrNull()

        assertEquals("No se encontro la preferencia", exception?.message)
    }

    private class FakePreferenceWebService(
        private val preferences: List<UserPreference>,
    ) : PreferenceWebService {
        var created = false
            private set
        var lastCreateRequest: UserPreferenceRequest? = null
            private set
        var lastUpdateId: Int? = null
            private set
        var lastUpdateRequest: UserPreferenceRequest? = null
            private set

        override suspend fun getPreferences(): List<UserPreference> = preferences

        override suspend fun createPreference(preference: UserPreferenceRequest): UserPreference {
            created = true
            lastCreateRequest = preference
            return preference.toDomain(id = 99)
        }

        override suspend fun updatePreference(preferenceId: Int, preference: UserPreferenceRequest): UserPreference {
            lastUpdateId = preferenceId
            lastUpdateRequest = preference
            return preference.toDomain(id = preferenceId)
        }
    }
}

private fun UserPreference.toExpectedRequest() = UserPreferenceRequest(
    userId = userId,
    darkMode = darkMode,
    languageCode = languageCode,
    timezone = timezone,
    emailNotifications = emailNotifications,
    pushNotifications = pushNotifications,
    smsNotifications = smsNotifications,
    profileVisibility = profileVisibility,
    dataSharingConsent = dataSharingConsent,
)

private fun UserPreferenceRequest.toDomain(id: Int) = UserPreference(
    id = id,
    userId = userId,
    darkMode = darkMode,
    languageCode = languageCode,
    timezone = timezone,
    emailNotifications = emailNotifications,
    pushNotifications = pushNotifications,
    smsNotifications = smsNotifications,
    profileVisibility = profileVisibility,
    dataSharingConsent = dataSharingConsent,
)
