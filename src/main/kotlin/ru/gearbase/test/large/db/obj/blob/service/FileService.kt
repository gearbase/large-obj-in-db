package ru.gearbase.test.large.db.obj.blob.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.blob.model.dto.FileMetadataDTO
import ru.gearbase.test.large.db.obj.blob.model.dto.FileNameView
import ru.gearbase.test.large.db.obj.blob.model.entity.FileContent
import ru.gearbase.test.large.db.obj.blob.model.entity.FileMetadata
import ru.gearbase.test.large.db.obj.blob.repository.FileRepo
import java.io.File
import java.nio.file.Files
import kotlin.io.path.outputStream


@Service
class FileService(
    private val fileRepo: FileRepo
) {
    fun getAllWithFullSelect(): List<FileMetadataDTO> {
        val findAll = fileRepo.findAll()
        return findAll.map { it.toDto() }
    }

    fun getNamesSelectOnly(): List<FileNameView> {
        return fileRepo.findAllViewBy()
    }


    @Transactional
    fun getFile(id: Long): Pair<FileContent, File> {
        val fileInfo = fileRepo.findById(id).orElseThrow { RuntimeException("File not found") }.content
        val largeObj = fileInfo.oid
        val tmpFile = Files.createTempFile(fileInfo.name, null)
        tmpFile.outputStream().use { out ->
            largeObj.binaryStream.use {
                it.copyTo(out)
            }
        }
        return fileInfo to tmpFile.toFile()
    }

    fun saveFile(file: MultipartFile): Long {
        return fileRepo.save(FileMetadata(file)).id!!
    }

}