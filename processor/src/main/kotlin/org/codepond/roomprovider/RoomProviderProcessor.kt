package org.codepond.roomprovider

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.google.common.collect.SetMultimap
import org.codepond.roomprovider.contract.ContractWriter
import org.codepond.roomprovider.contract.DatabaseProcessor
import org.codepond.roomprovider.contract.ExtensionWriter
import org.codepond.roomprovider.provider.ContentProviderWriter
import org.codepond.roomprovider.provider.ProviderWriter
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element

@Suppress("unused")
@AutoService(Processor::class)
class RoomProviderProcessor : BasicAnnotationProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun initSteps(): MutableIterable<ProcessingStep> {
        return mutableListOf(ProcessEntityStep(Context(processingEnv)))
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf("kapt.kotlin.generated")
    }

    class ProcessEntityStep(context: Context) : ContextAwareProcessingStep(context) {
        override fun annotations(): MutableSet<out Class<out Annotation>> {
            return mutableSetOf(androidx.room.Database::class.java,
                    androidx.room.Dao::class.java)
        }

        override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<out Element> {
            val databases = elementsByAnnotation[androidx.room.Database::class.java]?.map { element ->
                DatabaseProcessor(context, MoreElements.asType(element)).process()
            }

            databases?.forEach {
                val contract = ContractWriter(it, context).write()
                ExtensionWriter(it, context).write()
                ProviderWriter(it, contract, context).write()
                ContentProviderWriter(it, contract, context).write()
            }

            return mutableSetOf()
        }
    }
}
