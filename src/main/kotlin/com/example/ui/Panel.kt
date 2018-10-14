package com.example.ui

import com.example.TaxService
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Popup
import javafx.stage.Stage
import javafx.stage.Window
import java.io.File


class Panel : Application(), ProgressListener {

    private val choose = Button()
    private val download = Button()
    private val progressBar = ProgressBar()
    private val taxService = TaxService()

    override fun stop() {
        taxService.stop()
    }

    override fun setState(progress: Progress) {
        when (progress) {
            is ProcessFinish -> {
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.headerText = "处理完成"
                alert.showAndWait()

                choose.isDisable = false
                download.isDisable = false
                progressBar.progress = 100.0
            }
            is DownloadFinish -> {
                val alert = Alert(Alert.AlertType.INFORMATION)
                alert.headerText = "导出完成"
                alert.showAndWait()

                choose.isDisable = false
                download.isDisable = true
                progressBar.progress = 100.0
            }
        }
    }

    override fun start(primaryStage: Stage) {

        choose.text = "导入文件"
        choose.onAction = EventHandler<ActionEvent> { _ ->
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Excel 文件", "*.xls", "*.xlsx"))

            val file = fileChooser.showOpenDialog(primaryStage)?.takeIf { it.exists() } ?: return@EventHandler

            taxService.process(file, this)
            choose.isDisable = true
            progressBar.progress = -1.0
        }

        progressBar.progress = 0.0

        download.text = "导出文件"
        download.isDisable = true
        download.onAction = EventHandler<ActionEvent> {
            val fileChooser = FileChooser()
            val xlsxFilter = FileChooser.ExtensionFilter("Excel 2007+", "*.xlsx")
            val xlsFilter = FileChooser.ExtensionFilter("Excel 2003", "*.xls")
            fileChooser.extensionFilters.addAll(xlsxFilter, xlsFilter)
            fileChooser.initialFileName = System.currentTimeMillis().toString()

            var file = fileChooser.showSaveDialog(primaryStage) ?: return@EventHandler

            val selectedExtensionFilter = fileChooser.selectedExtensionFilter
            if (file.extension == "") {
                when (selectedExtensionFilter.description) {
                    xlsxFilter.description -> {
                        file = File(file.parent, file.name + ".xlsx")
                    }
                    xlsFilter.description -> {
                        file = File(file.parent, file.name + ".xls")
                    }
                }
            }

            taxService.writeTo(file, this)
            progressBar.progress = -1.0
        }

        val root = VBox(choose, progressBar, download)
        root.alignment = Pos.CENTER
        root.spacing = 20.0

        val scene = Scene(root, 300.0, 250.0)

        primaryStage.title = "税率计算"
        primaryStage.scene = scene
        primaryStage.show()
    }

}