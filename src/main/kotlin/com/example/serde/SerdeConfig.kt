package com.example.serde

import org.apache.poi.ss.usermodel.CellStyle

object SerdeConfig {
    var textStyle: CellStyle? = null

    var moneyStyle: CellStyle? = null

    var percentStyle: CellStyle? = null

    var headNumber: Int = 3

    var columnMapping = mutableMapOf<Int, Int>().also {
        for (i in 0..21) {
            it[i] = i
        }
    }
}