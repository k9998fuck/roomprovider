package org.codepond.roomprovider.ext

import com.squareup.javapoet.ClassName

object AndroidTypeNames {
    val URI = ClassName.get("android.net", "Uri")
    val CONTENT_RESOLVER = ClassName.get("android.content", "ContentResolver")
    val URI_MATCHER = ClassName.get("android.content", "UriMatcher")
    val CONTENT_VALUES = com.squareup.kotlinpoet.ClassName("android.content", "ContentValues")
    val CONTENT_PROVIDER = ClassName.get("android.content", "ContentProvider")
    val BASE_COLUMNS = ClassName.get("android.provider", "BaseColumns")
    val SUPPORT_SQLITE_QUERY_BUILDER = ClassName.get("androidx.sqlite.db", "SupportSQLiteQueryBuilder")
    val SUPPORT_SQLITE_QUERY = ClassName.get("androidx.sqlite.db", "SupportSQLiteQuery")
    val CURSOR = ClassName.get("android.database", "Cursor")

}
