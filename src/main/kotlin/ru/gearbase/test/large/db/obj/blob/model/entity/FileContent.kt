package ru.gearbase.test.large.db.obj.blob.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import org.hibernate.engine.jdbc.BlobProxy
import org.springframework.web.multipart.MultipartFile
import java.sql.Blob

@Entity
@Table(name = "file_content")
class FileContent (
    //The field type in the database must be bigint, not bytea. This field will store the OID of the created file.
    @Lob
//    @JdbcTypeCode(java.sql.Types.BINARY)
    val oid: Blob,

    val name: String?,

    val mimeType: String?,

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
        oid = BlobProxy.generateProxy(file.inputStream, file.size),
        name = file.originalFilename!!,
        mimeType = file.contentType!!,
        size = file.size,
        tags = listOf()
    )
}