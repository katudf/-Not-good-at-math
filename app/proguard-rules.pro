# kotlinx.serialization keeps generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.katudf.notgoodatmath.** {
    *** Companion;
}
-keep @kotlinx.serialization.Serializable class com.katudf.notgoodatmath.** { *; }
