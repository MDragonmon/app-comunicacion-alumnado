package com.example.appcomunicacionalumnado.util

import android.content.Context
import java.util.Locale

fun Context.updateLocale(language: String): Context {
    val locale = when (language.lowercase()) {
        "galego" -> Locale("gl")
        "english", "en" -> Locale("en")
        else -> Locale("es")
    }

    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    return createConfigurationContext(config)
}
