package ru.gearbase.test.large.db.obj.all.`in`.memory.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.dto.FileMetadataDTO
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.dto.FileNameView
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.entity.FileMetadata
import ru.gearbase.test.large.db.obj.all.`in`.memory.repository.FileContentRepo
import ru.gearbase.test.large.db.obj.all.`in`.memory.repository.FileRepo
import java.sql.Blob


@Service
class FileService(
    private val fileRepo: FileRepo,
    private val contentRepo: FileContentRepo,
) {
    fun getAllWithFullSelect(): List<FileMetadataDTO> {
        val findAll = fileRepo.findAll()
        return findAll.map { it.toDto() }
    }

    fun getNamesSelectOnly(): List<FileNameView> {
        return fileRepo.findAllViewBy()
    }


    @Transactional
    fun getFile(id: Long): Blob {
        val file = fileRepo.findById(id).orElseThrow { RuntimeException("Файл не найден") }
        return file.content.content
    }

//    @Transactional
    fun saveFile(file: MultipartFile, name: String?): Long {
        return fileRepo.save(FileMetadata(file, name)).id!!
    }

}