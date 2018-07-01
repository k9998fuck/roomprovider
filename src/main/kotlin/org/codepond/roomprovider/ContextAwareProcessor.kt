package org.codepond.roomprovider

import javax.lang.model.element.TypeElement

abstract class ContextAwareProcessor(protected val context: Context,
                                     protected val element: TypeElement)
