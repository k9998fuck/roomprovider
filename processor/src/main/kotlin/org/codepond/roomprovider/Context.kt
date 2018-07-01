package org.codepond.roomprovider

import org.codepond.roomprovider.log.RLog
import javax.annotation.processing.ProcessingEnvironment

data class Context(val processingEnv: ProcessingEnvironment) {
   val logger = RLog(processingEnv.messager)
}
