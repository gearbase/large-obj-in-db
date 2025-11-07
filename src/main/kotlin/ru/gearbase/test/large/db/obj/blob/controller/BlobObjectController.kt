package ru.gearbase.test.large.db.obj.blob.controller

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.blob.model.dto.FileMetadataDTO
import ru.gearbase.test.large.db.obj.blob.model.dto.FileNameView
import ru.gearbase.test.large.db.obj.blob.service.FileService
import java.nio.charset.StandardCharsets
import java.nio.file.Files

@RestController
class BlobObjectController(
    val fileService: FileService
) {

    @GetMapping("/file/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFileById(@PathVariable("id") id: Long, response: HttpServletResponse) {
        val (metadata, tempFile) = fileService.getFile(id)
        val contentDisposition = ContentDisposition.builder("attachment")
            .filename(metadata.name, StandardCharsets.UTF_8)
            .build()
        val contentType = metadata.mimeType
            ?.let { runCatching { MediaType.valueOf(it) }.getOrNull() }
            ?: MediaType.APPLICATION_OCTET_STREAM

        tempFile.inputStream().use { input ->
            response.contentType = contentType.toString()
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            response.setContentLength(metadata.size.toInt())
            response.outputStream.use { output ->
                input.copyTo(output)
            }
        }
        val deleted = Files.deleteIfExists(tempFile.toPath())
        log.info("File ${if (deleted) "was deleted" else "WAS NOT DELETED"} after response sent: ${tempFile.name}")
    }

    @GetMapping("/files")
    fun getFiles(): List<FileMetadataDTO> {
        return fileService.getAllWithFullSelect()
    }

    @GetMapping("/files/view")
    fun getFilesView(): List<FileNameView> {
        return fileService.getNamesSelectOnly()
    }

    @PostMapping("/file", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveFile(@RequestBody file: MultipartFile) {
        fileService.saveFile(file)
    }

    companion object {
        private val log = LoggerFactory.getLogger(BlobObjectController::class.java)
    }
}