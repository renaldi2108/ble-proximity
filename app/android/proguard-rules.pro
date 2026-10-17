-keepattributes *Annotation*, InnerClasses, Signature

-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class id.renaldi.proximity.**$$serializer { *; }
-keepclassmembers class id.renaldi.proximity.** { *** Companion; }
-keepclasseswithmembers class id.renaldi.proximity.** {
    kotlinx.serialization.KSerializer serializer(...);
}
