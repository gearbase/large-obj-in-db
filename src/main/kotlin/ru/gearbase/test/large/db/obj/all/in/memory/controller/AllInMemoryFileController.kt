package ru.gearbase.test.large.db.obj.all.`in`.memory.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.dto.FileMetadataDTO
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.dto.FileNameView
import ru.gearbase.test.large.db.obj.all.`in`.memory.service.FileService

@RestController
class AllInMemoryFileController(
    val fileService: FileService
) {

    @GetMapping("/file/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
//    @Transactional
    fun getFileById(@PathVariable("id") id: Long): InputStreamResource {
        val file = fileService.getFile(id)
        return InputStreamResource {
            file.binaryStream
        }
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
//    @Transactional
    fun saveFile(@RequestBody file: MultipartFile, @RequestParam(required = false) name: String?) {
        fileService.saveFile(file, name)
    }
}