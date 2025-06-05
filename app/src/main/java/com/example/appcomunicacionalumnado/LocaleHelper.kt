package com.example.appcomunicacionalumnado

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context.createConfigurationContext(configuration)
        } else {
            configuration.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            context
        }
    }

    fun onAttach(newBase: Context, defaultLanguage: String = "es"): ContextWrapper {
        val localeToSet = Locale(defaultLanguage)
        return ContextWrapper(updateResources(newBase, localeToSet))
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val res = context.resources
        val config = res.configuration
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }
        @Suppress("DEPRECATION")
        res.updateConfiguration(config, res.displayMetrics)
        return context
    }

    fun applyPersistedLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(LocaleList(locale))
        }
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    // Función para recrear la activity, necesaria después de cambiar el idioma
    fun recreateActivity(activity: Activity) {
        activity.recreate()
    }
}