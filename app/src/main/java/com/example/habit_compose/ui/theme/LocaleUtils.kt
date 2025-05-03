
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

fun updateLocale(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        config.setLocale(locale)
    } else {
        config.locale = locale
    }

    return context.createConfigurationContext(config)
}