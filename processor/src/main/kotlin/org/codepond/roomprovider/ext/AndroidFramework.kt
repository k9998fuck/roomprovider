package org.codepond.roomprovider.ext

import com.squareup.javapoet.ClassName

object AndroidTypeNames {
    val URI = ClassName.get("android.net", "Uri")
    val CONTENT_RESOLVER = ClassName.get("android.content", "ContentResolver")
    val URI_MATCHER = ClassName.get("android.content", "UriMatcher")
}
