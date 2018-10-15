package com.example.serde

import org.apache.poi.ss.usermodel.CellStyle
import java.util.stream.Collectors.toList
import java.util.stream.IntStream

object SerdeConfig {
    var moneyStyle: CellStyle? = null

    var percentStyle: CellStyle? = null

    var generalStyle: CellStyle? = null

    var headNumber: Int = 4

    val columnStyle = mutableMapOf<Int, CellStyle>()

    var columnSize = 20

//    var columnMapping: List<Int> = IntStream.range(0, 21).boxed().collect(toList())
}