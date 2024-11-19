package com.doyoonkim.knutice.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Define Custom-defined theme based on pre-defined color scheme
val ColorScheme.notificationType1: Color
    @Composable
    get() = Notification01

val ColorScheme.notificationType2: Color
    @Composable
    get() = Notification02

val ColorScheme.notificationType3: Color
    @Composable
    get() = Notification03

val ColorScheme.notificationType4: Color
    @Composable
    get() = Notification04

val ColorScheme.displayBackground: Color
    @Composable
    get() = if(isSystemInDarkTheme()) DarkBackground else WhiteBackground

val ColorScheme.containerBackground: Color
    @Composable
    get() = if(isSystemInDarkTheme()) ContainerDark else ContainerWhite

val ColorScheme.title: Color
    @Composable
    get() = if(isSystemInDarkTheme()) TitleWhite else TitleBlack

val ColorScheme.subTitle: Color
    @Composable
    get() = SubtitleAny

val ColorScheme.buttonContainer: Color
    @Composable
    get() = if(isSystemInDarkTheme()) ButtonDark else ButtonLight

val ColorScheme.textPurple: Color
    @Composable
    get() = if(isSystemInDarkTheme()) Purple80 else Purple40

val ColorScheme.animationGradientStart: Color
    @Composable
    get() = if(isSystemInDarkTheme()) GradientStartDark else GradientStartLight

val ColorScheme.animationGradientIntermediate: Color
    @Composable
    get() = if(isSystemInDarkTheme()) GradientIntermediateDark else GradientIntermediateLight

val ColorScheme.animationGradientEnd: Color
    @Composable
    get() = if(isSystemInDarkTheme()) GradientEndDark else GradientEndLight


@Composable
fun KNUTICETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}