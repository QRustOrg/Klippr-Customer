package com.example.klippr.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.klippr.iam.domain.model.Session
import com.example.klippr.iam.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// @author Samuel Bonifacio
// Delegate a nivel de archivo: una única instancia de DataStore "session" por proceso.
private val Context.sessionStore by preferencesDataStore(name = "session")

/** Persiste datos mínimos de sesión (token + identidad) usando Preferences DataStore. */
class SessionDataStore(private val context: Context) {

    private val _sessionExpiredEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvents: SharedFlow<Unit> = _sessionExpiredEvents

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")
        val EMAIL = stringPreferencesKey("email")
        val ROLE = stringPreferencesKey("role")
    }

    /** Emite la sesión actual o `null` si no hay usuario autenticado. */
    val session: Flow<Session?> = context.sessionStore.data.map { prefs ->
        val token = prefs[Keys.TOKEN]
        val userId = prefs[Keys.USER_ID]
        val email = prefs[Keys.EMAIL]
        val role = prefs[Keys.ROLE]
        if (token.isNullOrBlank() || userId.isNullOrBlank()) {
            null
        } else {
            Session(token = token, user = User(userId = userId, email = email ?: "", role = role ?: ""))
        }
    }

    /** Lectura puntual (no reactiva) del token, usada por el interceptor de red. */
    suspend fun currentToken(): String? = session.first()?.token

    suspend fun save(session: Session) {
        context.sessionStore.edit { prefs ->
            prefs[Keys.TOKEN] = session.token
            prefs[Keys.USER_ID] = session.user.userId
            prefs[Keys.EMAIL] = session.user.email
            prefs[Keys.ROLE] = session.user.role
        }
    }

    suspend fun clear() {
        context.sessionStore.edit { it.clear() }
    }

    suspend fun expireSession() {
        clear()
        _sessionExpiredEvents.tryEmit(Unit)
    }
}
