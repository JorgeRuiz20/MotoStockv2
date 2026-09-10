package com.taller.motostock.core.domain.di

/**
 * Multiplatform Inject annotation.
 * On Android, this maps directly to javax.inject.Inject via typealias for Hilt compatibility.
 * On iOS/Native, this is an active annotation for KMP DI.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
expect annotation class Inject()

