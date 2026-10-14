package cat.freya.alloy.annotation

import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget
import kotlin.annotation.Retention

@Target(AnnotationTarget.PROPERTY) @Retention(AnnotationRetention.RUNTIME) annotation class Section(val text: String)
