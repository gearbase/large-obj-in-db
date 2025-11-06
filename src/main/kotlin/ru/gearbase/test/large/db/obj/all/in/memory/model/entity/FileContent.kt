package ru.gearbase.test.large.db.obj.all.`in`.memory.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.engine.jdbc.BlobProxy
import org.postgresql.core.BaseConnection
import org.postgresql.jdbc.PgBlob
import org.springframework.web.multipart.MultipartFile
import java.sql.Blob
import kotlin.random.Random

@Entity
@Table(name = "file_content")
class FileContent (
    @Lob
    @JdbcTypeCode(java.sql.Types.BINARY)
    val content: Blob,


    @Column(nullable = false)
    val name: String,

    val mimeType: String,

    val size: Long,

    @OneToMany(cascade = [CascadeType.ALL], /*fetch = FetchType.LAZY,*/ mappedBy = "file")
//    @JoinColumn(name = "file_id", nullable = false)
//    @Fetch(FetchMode.JOIN)
    @BatchSize(size = 20)
    val tags: List<FileTag>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,
) {
    constructor(file: MultipartFile) : this(
        content = BlobProxy.generateProxy(file.inputStream, file.size),
        name = file.originalFilename!!,
        mimeType = file.contentType!!,
        size = file.size,
        tags = listOf()
    )
}