package org.codepond.roomprovider

import com.google.auto.common.BasicAnnotationProcessor

abstract class ContextAwareProcessingStep(protected val context: Context) : BasicAnnotationProcessor.ProcessingStep
