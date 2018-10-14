package com.example.ui

interface ProgressListener {
    fun setState(progress: Progress)
}

sealed class Progress {
    abstract val rate: Int
}

data class Running(override val rate: Int) : Progress()

class ProcessFinish : Progress() {
    override val rate = 100
}

class DownloadFinish : Progress() {
    override val rate = 100
}