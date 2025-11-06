package ru.gearbase.test.large.db.obj.dbstream.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
//import org.apache.commons.io.IOUtils
import org.postgresql.largeobject.LargeObjectManager
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import ru.gearbase.test.large.db.obj.dbstream.domain.entity.LargeFile
import ru.gearbase.test.large.db.obj.dbstream.repository.LargePgApi
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.util.UUID
import javax.sql.DataSource
import kotlin.io.path.outputStream

@Repository
class LargePgApiImpl(
    private val dataSource: DataSource,
    @field:PersistenceContext private val em: EntityManager,
) : LargePgApi {

    @Transactional
    override fun saveFile(file: MultipartFile) {
        val conn = DataSourceUtils.getConnection(dataSource)
        try {
            val pgConn = conn.unwrap(org.postgresql.PGConnection::class.java)
            val lobj = pgConn.largeObjectAPI

            val oid = lobj.createLO(LargeObjectManager.WRITE)
            val obj = lobj.open(oid, LargeObjectManager.WRITE)

            file.inputStream.use { input ->
                input.copyTo(obj.outputStream)
            }

            obj.close()

            val entity = LargeFile(
                oid = oid,
                fileName = file.originalFilename,
                size = file.size,
                mimeType = file.contentType
            )
            em.persist(entity)
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource)
        }
    }

    @Transactional
    override fun getFile(id: UUID): Pair<LargeFile, File> {
        val file = em.find(LargeFile::class.java, id)
        val tempFile = Files.createTempFile(UUID.randomUUID().toString(), file.fileName!!)
        val conn: Connection = DataSourceUtils.getConnection(dataSource)
        return try {
            val pgConn = conn.unwrap(org.postgresql.PGConnection::class.java)
            val lobj = pgConn.largeObjectAPI

            val obj = lobj.open(file.oid, LargeObjectManager.READ)
            val inputStream = obj.inputStream

            inputStream.use { lois ->
                tempFile.outputStream().use { fileOutputStream ->
//                    IOUtils.copy(lois, fileOutputStream)
                    inputStream.copyTo(fileOutputStream)
                }
            }

            obj.close()
            file to tempFile.toFile()

        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource)
        }

    }

    @Transactional
    override fun deleteFile(id: UUID) {
        val file = em.find(LargeFile::class.java, id)
        val conn = DataSourceUtils.getConnection(dataSource)
        try {
            val pgConn = conn.unwrap(org.postgresql.PGConnection::class.java)
            val lobj = pgConn.largeObjectAPI
            lobj.delete(file.oid)
            em.remove(file)
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource)
        }
    }

    @Transactional
    override fun updateFile(id: UUID, newFile: MultipartFile) {
        val file = em.find(LargeFile::class.java, id)
        val conn = DataSourceUtils.getConnection(dataSource)
        try {
            val pgConn = conn.unwrap(org.postgresql.PGConnection::class.java)
            val lobj = pgConn.largeObjectAPI
            lobj.delete(file.oid)

            val oid = lobj.createLO(LargeObjectManager.WRITE)
            val newObj = lobj.open(oid, LargeObjectManager.WRITE)

            newFile.inputStream.use { input ->
                input.copyTo(newObj.outputStream)
            }

            newObj.close()

            val entity = LargeFile(
                id = file.id,
                oid = oid,
                fileName = newFile.originalFilename,
                size = file.size,
                mimeType = newFile.contentType
            )
            em.merge(entity)
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LargePgApiImpl::class.java)
    }

}