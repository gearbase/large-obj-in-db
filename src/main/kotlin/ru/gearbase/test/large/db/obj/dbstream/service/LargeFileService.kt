package ru.gearbase.test.large.db.obj.dbstream.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.dbstream.domain.entity.LargeFile
import ru.gearbase.test.large.db.obj.dbstream.repository.LargeFileContentRepo
import java.io.File
import java.util.UUID

@Service
class LargeFileService(
    private val largeFileContentRepo: LargeFileContentRepo
) {
    fun saveFile(file: MultipartFile) = largeFileContentRepo.saveFile(file)

    fun getFile(id: UUID): Pair<LargeFile, File> = largeFileContentRepo.getFile(id)

    fun deleteFile(id: UUID) = largeFileContentRepo.deleteFile(id)

    fun updateFile(id: UUID, newFile: MultipartFile) = largeFileContentRepo.updateFile(id, newFile)
}