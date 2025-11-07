package ru.gearbase.test.large.db.obj.blob.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "file_tag")
//@BatchSize(size = 20)
class FileTag (
    val tagName: String,
    @ManyToOne
    @JoinColumn(name = "file_id")
    val file: FileContent,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,
)


