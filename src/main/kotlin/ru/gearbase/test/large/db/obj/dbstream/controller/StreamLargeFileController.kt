package ru.gearbase.test.large.db.obj.dbstream.controller

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import ru.gearbase.test.large.db.obj.dbstream.service.LargeFileService
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.UUID

@RestController
@RequestMapping("/large/file")
class StreamLargeFileController(
    private val largeFileService: LargeFileService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveFile(@RequestBody file: MultipartFile) {
        largeFileService.saveFile(file)
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFileById(@PathVariable("id") id: UUID, response: HttpServletResponse): ResponseEntity<StreamingResponseBody> {

        val (metadata, tempFile) = largeFileService.getFile(id)
        val contentDisposition = ContentDisposition.builder("attachment")
            .filename(metadata.fileName, StandardCharsets.UTF_8)
            .build()
        val contentType = metadata.mimeType
            ?.let { runCatching { MediaType.valueOf(it) }.getOrNull() }
            ?: MediaType.APPLICATION_OCTET_STREAM

        val responseBody = StreamingResponseBody { outputStream ->
            tempFile.inputStream().use { input ->
                input.copyTo(outputStream)
            }

            val deleted = Files.deleteIfExists(tempFile.toPath())
            log.info("File ${if(deleted) "was deleted" else "WAS NOT DELETED"} after response sent: ${tempFile.name}")
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentLength(metadata.size)
            .contentType(contentType)
            .body(responseBody)
    }

    @DeleteMapping("/{id}")
    fun deleteFileById(@PathVariable("id") id: UUID) {
        largeFileService.deleteFile(id)
    }

    @PatchMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateFileById(@PathVariable("id") id: UUID, @RequestBody file: MultipartFile) {
        largeFileService.updateFile(id, file)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StreamLargeFileController::class.java)
    }
}