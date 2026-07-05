package com.example.klippr.settings.presentation.view

import com.example.klippr.preferences.domain.model.UserPreference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsSectionsTest {

    @Test
    fun accountHubSectionsMatchApprovedOrder() {
        val sections = accountSettingsSections()

        assertEquals(
            listOf(
                "Información personal",
                "Login y seguridad",
                "Privacidad",
                "Notificaciones",
                "Pagos",
                "Preferencias",
                "Beneficios Klippr",
                "Accesibilidad",
                "Ayuda",
            ),
            sections.map { it.title },
        )
        assertEquals("personal", sections.first().routeKey)
        assertEquals(SettingsSectionKind.PersonalInfo, sections.first().kind)
        assertTrue(sections.any { it.routeKey == "preferences" })
    }

    @Test
    fun preferenceSectionsExposeOnlySupportedEditableValues() {
        val preference = UserPreference.defaults("consumer-1").copy(
            languageCode = "en",
            timezone = "America/Lima",
            emailNotifications = false,
            pushNotifications = true,
            smsNotifications = false,
            profileVisibility = "public",
            dataSharingConsent = true,
        )

        assertEquals(
            listOf("Idioma", "Zona horaria", "Modo oscuro"),
            preferenceDetailRows(preference).map { it.title },
        )
        assertEquals(
            listOf("Notificaciones por correo", "Notificaciones push", "Notificaciones SMS"),
            notificationDetailRows(preference).map { it.title },
        )
        assertEquals(
            listOf("Perfil público", "Compartir datos para personalización"),
            privacyDetailRows(preference).map { it.title },
        )
        assertTrue(preferenceDetailRows(preference).any { it.title.contains("oscuro", ignoreCase = true) })
    }
}
