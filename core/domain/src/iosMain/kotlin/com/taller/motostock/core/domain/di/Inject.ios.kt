package com.taller.motostock.core.domain.di

/**
 * iOS actual implementation of Inject annotation.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
actual annotation class Inject actual constructor()

