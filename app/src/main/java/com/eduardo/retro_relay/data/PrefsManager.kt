package com.eduardo.retro_relay.data

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {
    private const val PREFS_NAME = "retrorelay_prefs"
    private const val KEY_IP = "ip_relay"
    private const val KEY_MODO = "modo_unico"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setIp(ip: String) {
        prefs.edit().putString(KEY_IP, ip).apply()
    }

    fun getIp(): String = prefs.getString(KEY_IP, "192.168.4.1") ?: "192.168.4.1"

    fun setModoUnico(unico: Boolean) {
        prefs.edit().putBoolean(KEY_MODO, unico).apply()
    }

    fun getModoUnico(): Boolean = prefs.getBoolean(KEY_MODO, true)
}
