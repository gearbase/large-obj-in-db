package ru.gearbase.test.large.db.obj.dbstream.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "large_file_content")
class LargeFile(
    @Column(nullable = false)
    val oid: Long,
    val fileName: String? = null,
    val mimeType: String? = null,
    val size: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? =null,
)