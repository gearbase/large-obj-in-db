package ru.gearbase.test.large.db.obj.dbstream.utils

import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import java.io.File
import java.io.InputStream
import java.nio.file.Files

//Not used
class AutoDeletingFileResource(
    private val file: File
) : InputStreamResource(file.inputStream()) {

    override fun getFilename(): String? = file.name

    override fun getInputStream(): InputStream {
        val originalStream = file.inputStream()
        return object : InputStream() {
            override fun read(): Int = originalStream.read()

            override fun read(b: ByteArray, off: Int, len: Int): Int = originalStream.read(b, off, len)

            //calls from ResourceHttpMessageConverter.writeContent(ResourceHttpMessageConverter.java:168)
            override fun close() {
                try {
                    originalStream.close()
                } finally {
                    try {
                        if (Files.exists(file.toPath())) {
                            //but not working - java.nio.file.FileSystemException: The process cannot access the file because it is being used by another process.
                            Files.deleteIfExists(file.toPath())
                            log.info("File deleted after streaming: ${file.name}")
                        }
                    }catch (e: Exception){
                        log.error("Error deleting file ${file.name}", e)
                    }



                }
            }
        }
    }
    companion object {
        private val log = LoggerFactory.getLogger(AutoDeletingFileResource::class.java)
    }
}