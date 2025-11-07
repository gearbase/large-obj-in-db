package ru.gearbase.test.large.db.obj.blob.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.gearbase.test.large.db.obj.blob.model.entity.FileContent

@Repository
interface FileContentRepo: JpaRepository<FileContent, Long> {
}