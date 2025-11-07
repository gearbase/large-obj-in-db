package ru.gearbase.test.large.db.obj.blob.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.blob.model.dto.FileMetadataDTO

@Entity
@Table(name = "file_info")
class FileMetadata(
    @Column(nullable = false)
    val name: String,

//    @Fetch(FetchMode.SELECT)
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "file_id", referencedColumnName = "id")
//    @NamedEntityGraph
    val content: FileContent,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,
) {
    constructor(file: MultipartFile, name: String? = null) : this(
        name = name ?: file.originalFilename!!,
        content = FileContent(file)
    )

    fun toDto(): FileMetadataDTO = FileMetadataDTO(name = name, id = id!!)
}