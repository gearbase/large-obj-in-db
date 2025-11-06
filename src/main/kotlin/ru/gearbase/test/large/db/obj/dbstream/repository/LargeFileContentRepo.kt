package ru.gearbase.test.large.db.obj.dbstream.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.gearbase.test.large.db.obj.dbstream.domain.entity.LargeFile
import java.util.UUID

@Repository
interface LargeFileContentRepo: JpaRepository<LargeFile, UUID>, LargePgApi {
}