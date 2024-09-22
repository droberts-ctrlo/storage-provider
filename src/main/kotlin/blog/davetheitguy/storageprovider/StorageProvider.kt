package blog.davetheitguy.storageprovider

import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

interface StorageProvider: Closeable {
    fun fileExists(name: String): Boolean
    fun saveFile(path: String)
    fun saveFile(path: String, progress: (progress: Int, total: Int) -> Unit)
    fun saveFile(path: String, name: String)
    fun saveFile(path: String, name: String, progress: (progress: Int, total: Int) -> Unit)
    fun saveStream(name: String, stream: InputStream)
    fun saveStream(name: String, stream: InputStream, progress: (progress: Int, total: Int) -> Unit)
    fun downloadFile(source: String, destination: String)
    fun downloadFile(source: String, destination: String, progress: (progress: Int, total: Int) -> Unit)
    fun downloadStream(source: String, stream: OutputStream)
    fun downloadStream(source: String, stream: OutputStream, progress: (progress: Int, total: Int) -> Unit)
    fun downloadStream(inputStream: InputStream, outputStream: OutputStream)
    fun downloadStream(inputStream: InputStream, outputStream: OutputStream, progress: (progress: Int, total: Int) -> Unit)
}