package com.example

import com.example.calc.TaxCalculator
import com.example.serde.PersonModelSerde
import com.example.ui.DownloadFinish
import com.example.ui.ProcessFinish
import com.example.ui.ProgressListener
import com.sun.corba.se.impl.presentation.rmi.ExceptionHandler
import javafx.application.Platform
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

class TaxService {
    private var outputWorkBook: Optional<Workbook> = Optional.empty()

    private val LOGGER = LoggerFactory.getLogger(TaxService::javaClass.name)

    private val worker = Executors.newSingleThreadExecutor { r ->
        val thread = Thread(r)
        thread.isDaemon = true
        thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, throwable ->
            LOGGER.error("Worker, ", throwable)
        }

        thread
    }

    fun process(file: File, panel: ProgressListener) {
        worker.execute {
            Files.newInputStream(file.toPath(), StandardOpenOption.READ).use {
                val workbook = WorkbookFactory.create(it)

                val peopleModel = PersonModelSerde.deserialize(workbook)

                val calculatedModel = TaxCalculator.calculate(peopleModel)
                try {
                    outputWorkBook = Optional.of(PersonModelSerde.serialize(workbook, calculatedModel))
                } catch (e: Exception) {
                    LOGGER.error("", e)
                }
                Platform.runLater {
                    panel.setState(ProcessFinish())
                }
            }
        }
    }

    fun writeTo(file: File, panel: ProgressListener) {
        worker.submit {
            Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { out ->
                outputWorkBook.ifPresent {
                    it.write(out)

                    Platform.runLater {
                        panel.setState(DownloadFinish())
                    }
                }
            }
        }
    }

    fun stop() {
        worker.shutdownNow()
    }
}