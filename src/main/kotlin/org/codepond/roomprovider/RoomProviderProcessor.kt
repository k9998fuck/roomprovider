package org.codepond.roomprovider

import android.arch.persistence.room.Database
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.google.common.collect.SetMultimap
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element

@AutoService(Processor::class)
class RoomProviderProcessor : BasicAnnotationProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun initSteps(): MutableIterable<ProcessingStep> {
        return mutableListOf(ProcessEntityStep(Context(processingEnv)))
    }

    class ProcessEntityStep(context: Context) : ContextAwareProcessingStep(context) {
        override fun annotations(): MutableSet<out Class<out Annotation>> {
            return mutableSetOf(Database::class.java)
        }

        override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<out Element> {
            val contracts = elementsByAnnotation[Database::class.java]?.map { element ->
                DatabaseProcessor(context, MoreElements.asType(element)).process()
            }
            context.logger.d("Contracts: ${contracts?.toString()}")
            return mutableSetOf()
        }
    }
}
