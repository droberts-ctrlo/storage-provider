package blog.davetheitguy.storageprovider.config

import org.springframework.boot.context.properties.ConfigurationProperties

@Suppress("ConfigurationProperties")
@ConfigurationProperties(prefix = "storage.local", ignoreUnknownFields = true, ignoreInvalidFields = true, value = "path")
data class LocalStorageConfig(var storagePath: String, var overwrite: Boolean = false)
