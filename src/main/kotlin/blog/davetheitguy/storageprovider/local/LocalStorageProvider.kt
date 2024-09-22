package blog.davetheitguy.storageprovider.local

import blog.davetheitguy.storageprovider.StorageProvider
import blog.davetheitguy.storageprovider.config.LocalStorageConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Service
@ConditionalOnMissingBean(StorageProvider::class)
class LocalStorageProvider(config: LocalStorageConfig) : StorageProvider {
    private final val myFilePath: File = File(config.storagePath)
    private final val overwrite: Boolean = config.overwrite
    private final val logger = LoggerFactory.getLogger(LocalStorageProvider::class.java)

    init {
        if (!myFilePath.exists()) throw FileNotFoundException()
        if (myFilePath.isFile) throw IOException("Path ${myFilePath.absolutePath} is a file, not a directory")
    }

    private fun cleanupName(name: String): String {
        logger.info("Renaming file: $name")
        val notAlphabet = Regex("[^a-zA-Z0-9.]")
        val result = name.replace(" ", "_").replace(notAlphabet, "")
        logger.info("New name: $result")
        return result
    }

    private fun file(name: String): File {
        return File(myFilePath, cleanupName(name))
    }

    override fun fileExists(name: String): Boolean {
        val file = file(name)
        logger.info("Checking for ${file.absolutePath}")
        val result = file.exists()
        logger.info("File exists: $result")
        return result
    }

    override fun saveFile(path: String) {
        val name = path.substring(path.lastIndexOf(File.separatorChar) + 1)
        logger.info("Saving file $path using $name")
        saveFile(path, name)
    }

    override fun saveFile(path: String, name: String) {
        val input = File(path)
        if (!input.exists()) throw FileNotFoundException()
        input.inputStream().use { inputStream ->
            saveStream(name, inputStream)
        }
    }

    override fun saveStream(name: String, stream: InputStream) {
        val file = this.file(name)
        if(file.exists() && overwrite) file.delete()
        if(file.exists()) throw IOException("File $name exists")
        file.outputStream().use { outputStream ->
            val buffer = ByteArray(4096)
            var read = stream.read(buffer)
            while (read != -1) {
                outputStream.write(buffer, 0, read)
                read = stream.read(buffer)
            }
        }
    }

    override fun saveFile(path: String, progress: (progress: Int, total: Int) -> Unit) {
        val name = path.substring(path.lastIndexOf(File.separatorChar) + 1)
        saveFile(path, name, progress)
    }

    override fun saveFile(path: String, name: String, progress: (progress: Int, total: Int) -> Unit) {
        val input = File(path)
        if (!input.exists()) throw FileNotFoundException()
        input.inputStream().use { inputStream ->
            saveStream(name, inputStream, progress)
        }
    }

    override fun saveStream(name: String, stream: InputStream, progress: (progress: Int, total: Int) -> Unit) {
        val file = this.file(name)
        if(file.exists() && overwrite) file.delete()
        if(file.exists()) throw IOException("File $name exists")
        file.outputStream().use { outputStream ->
            val buffer = ByteArray(4096)
            val total = stream.available()
            var totalRead = 0
            var read = stream.read(buffer)
            while (read != -1) {
                totalRead += read
                outputStream.write(buffer, 0, read)
                progress(totalRead, total)
                read = stream.read(buffer)
            }
        }
    }

    override fun downloadFile(source: String, destination: String) {
        if(File(destination).exists()) throw IOException("Destination $destination exists")
        File(destination).outputStream().use { outputStream ->
            downloadStream(source, outputStream)
        }
    }

    override fun downloadStream(source: String, stream: OutputStream) {
        if(!fileExists(source)) throw FileNotFoundException()
        file(source).inputStream().use { inputStream ->
            downloadStream(inputStream, stream)
        }
    }

    override fun downloadStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(4096)
        var read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
    }

    override fun downloadFile(source: String, destination: String, progress: (progress: Int, total: Int) -> Unit) {
        if(File(destination).exists()) throw IOException("Destination $destination exists")
        File(destination).outputStream().use { outputStream ->
            downloadStream(source, outputStream, progress)
        }
    }

    override fun downloadStream(source: String, stream: OutputStream, progress: (progress: Int, total: Int) -> Unit) {
        if(!fileExists(source)) throw FileNotFoundException()
        file(source).inputStream().use { inputStream ->
            downloadStream(inputStream, stream, progress)
        }
    }

    override fun downloadStream(inputStream: InputStream, outputStream: OutputStream, progress: (progress: Int, total: Int) -> Unit) {
        val buffer = ByteArray(4096)
        var read = inputStream.read(buffer)
        var readProgress = 0
        val total = inputStream.available()
        while (read != -1) {
            readProgress += read
            outputStream.write(buffer, 0, read)
            progress(readProgress, total)
            read = inputStream.read(buffer)
        }
    }

    override fun close() {
        // NOOP: Should be handled locally
    }
}
