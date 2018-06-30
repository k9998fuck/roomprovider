package processor

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ContractGenerator(private val messager: Messager) {
    fun generate(annotation: Class<Entity>, type: TypeElement): ContractInfo {
        val entityAnnotation = type.getAnnotation(annotation)
        val tableName = getTableName(entityAnnotation, type)

        return ContractInfo("${tableName}Contract".capitalize(),
                tableName,
                getColumns(type))
    }

    private fun getColumns(type: TypeElement): Set<Pair<String, String>> {
        val fields = type.enclosedElements.filter { element -> element.kind == ElementKind.FIELD }
        val columns = mutableSetOf<Pair<String, String>>()
        for (field in fields) {
            if (field.getAnnotation(Ignore::class.java) != null) continue

            val columnInfo = field.getAnnotation(ColumnInfo::class.java)
            val columnName = columnInfo?.name ?: field.simpleName.toString()
            columns += "COLUMN_${columnName.toUpperCase()}" to columnName
        }
        if (columns.size == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Entity class ${type.qualifiedName} does not contains any fields")
        }
        return columns
    }

    private fun getTableName(entityAnnotation: Entity, type: TypeElement): String {
        val tableName = if (entityAnnotation.tableName.isEmpty()) type.simpleName.toString() else entityAnnotation.tableName
        if (tableName.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Cannot resolve class name for ${type.qualifiedName}. " +
                    "Anonymous classes cannot be annotated.")
        }
        return tableName
    }
}
