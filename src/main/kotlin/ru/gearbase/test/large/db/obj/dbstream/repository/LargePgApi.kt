package ru.gearbase.test.large.db.obj.dbstream.repository

import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.dbstream.domain.entity.LargeFile
import java.io.File
import java.util.UUID

interface LargePgApi {
    fun saveFile(file: MultipartFile)

    fun getFile(id: UUID): Pair<LargeFile, File>

    fun deleteFile(id: UUID)

    fun updateFile(id: UUID, newFile: MultipartFile)
}