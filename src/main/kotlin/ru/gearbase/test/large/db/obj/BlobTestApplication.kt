package ru.gearbase.test.large.db.obj

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
class BlobTestApplication

fun main(args: Array<String>) {
    runApplication<BlobTestApplication>(*args)
}
