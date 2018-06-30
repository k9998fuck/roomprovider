package processor

import android.arch.persistence.room.Entity
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class MyProcessor : AbstractProcessor() {
    private val annotation = Entity::class.java
    private val contractGenerator by lazy { ContractGenerator(processingEnv.messager) }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(annotation.canonicalName)

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        for (type in roundEnv.getElementsAnnotatedWith(annotation)) {
            contractGenerator.generate(annotation, type as TypeElement)
        }

        return false
    }
}
