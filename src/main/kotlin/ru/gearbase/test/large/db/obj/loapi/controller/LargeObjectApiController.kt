package ru.gearbase.test.large.db.obj.loapi.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.hibernate.engine.jdbc.BlobProxy
import org.slf4j.LoggerFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
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
import ru.gearbase.test.large.db.obj.loapi.service.LargeFileService
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Arrays
import java.util.UUID


@RestController
@RequestMapping("/large/file")
class LargeObjectApiController(
    private val largeFileService: LargeFileService,
    private val jdbcTemplate: JdbcTemplate,
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
            log.info("File ${if (deleted) "was deleted" else "WAS NOT DELETED"} after response sent: ${tempFile.name}")
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

    @PostMapping("/withJdbcTemplate", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Transactional
    fun test() {
        val bytes = ByteArray(1000)
        Arrays.fill(bytes, 'a'.code.toByte())

        val tempFile: Path = Files.createTempFile("test_blob", ".bin")
        Files.newOutputStream(tempFile).use { os ->
            os.write(bytes)
        }

        var blobId: Long?
        Files.newInputStream(tempFile).use { `is` ->
            blobId = jdbcTemplate.query("select ?",
                { ps: PreparedStatement ->
                    ps.setBlob(1, BlobProxy.generateProxy(`is`, tempFile.toFile().length()))
                },
                { rs: ResultSet, rowNum: Int ->
                    rs.getLong(1)
                })[0]
        }


        val blob = jdbcTemplate.query("select ?", { ps: PreparedStatement ->
            ps.setLong(1, blobId!!)
        }, { rs: ResultSet, rowNum: Int ->
            rs.getBlob(1)
        })[0]


        val binaryStream = blob.binaryStream
        binaryStream.use { `is` ->
            val nextByte = `is`.read()
            println(nextByte)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LargeObjectApiController::class.java)
    }
}