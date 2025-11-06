package ru.gearbase.test.large.db.obj.all.`in`.memory.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.entity.FileMetadata
import ru.gearbase.test.large.db.obj.all.`in`.memory.model.dto.FileNameView
import java.util.Optional

@Repository
interface FileRepo: JpaRepository<FileMetadata, Long> {
    fun findAllViewBy(): List<FileNameView>

    @EntityGraph(attributePaths = ["content.tags"]) //TODO Лучше так не делать, чтобы избежать декартового произведения из-за 2х left join. Лучше один вытаскивать через join, а лист вторых сущностей подтягивать через @BatchSize (отдельным select'ом с where in) в транзакции
    fun findWithTagsById(id: Long): Optional<FileMetadata>

    @EntityGraph(attributePaths = ["content"])
    fun findWithContentById(id: Long): Optional<FileMetadata>
}