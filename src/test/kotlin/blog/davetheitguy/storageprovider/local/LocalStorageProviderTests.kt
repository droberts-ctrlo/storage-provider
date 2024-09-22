package blog.davetheitguy.storageprovider.local

import blog.davetheitguy.storageprovider.StorageProvider
import blog.davetheitguy.storageprovider.config.LocalStorageConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException

class LocalStorageProviderTests {
    private lateinit var sut: StorageProvider

    @BeforeEach
    fun before() {
        sut = LocalStorageProvider(LocalStorageConfig(".", true))
    }

    @AfterEach
    fun after() {
        sut.close()
    }

    @Test
    fun `fileExists when file absent returns false `() {
        if (File("bob.txt").exists())
            File("bob.txt").delete()
        Assertions.assertFalse(sut.fileExists("bob.txt"))
    }

    @Test
    fun `fileExists when file present returns true `() {
        Assertions.assertTrue(sut.fileExists("pom.xml"))
    }

    // Only need to test the very base version as this calls all more complex versions

    @Test
    fun `saveFile throws an exception when the source doesn't exist`() {
        val path = "C:\\Users\\dave\\OneDrive\\Documents"
        val f = "bob.txt"
        Assertions.assertFalse(File(File(path), f).exists())
        Assertions.assertThrows(FileNotFoundException::class.java) {
            sut.saveFile("C:\\Users\\dave\\OneDrive\\Documents\\$f")
        }
    }

    @Test
    fun `saveFile can save file when source exists`() {
        val path = "C:\\Users\\dave\\OneDrive\\Documents"
        val f = "IT.docx"
        Assertions.assertTrue(File(File(path), f).exists())
        Assertions.assertDoesNotThrow {
            sut.saveFile("C:\\Users\\dave\\OneDrive\\Documents\\IT.docx")
        }
    }
}
