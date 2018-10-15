package com.example.serde

import com.example.model.PersonModel
import org.apache.poi.ss.usermodel.*
import java.util.*
import java.util.stream.Collectors.toList
import java.util.stream.IntStream
import java.util.stream.Stream

class PersonModelSerde {
    companion object {
        fun deserialize(workbook: Workbook): Stream<PersonModel> {
            val sheet = workbook.getSheetAt(0)

            val lastRowNum = sheet.lastRowNum

            return IntStream.range(0, lastRowNum + 1)
                    .skip(SerdeConfig.headNumber.toLong())
                    .mapToObj { sheet.getRow(it) }
                    .filter { it != null }
                    .map { mapRow(it) }
                    .filter { it.isPresent }
                    .map { it.get() }
        }

        private fun mapRow(row: Row): Optional<PersonModel> {
            val person = PersonModel()
            for (index in 0..20) {
                val cell = row.getCell(index)
                if (cell == null || cell.cellType == CellType.BLANK) {
                    continue
                }
                when (index + 1) {
                    1 -> person.name = cell.stringCellValue
                    2 -> person.dept = cell.stringCellValue
                    3 -> person.taxModel.salary = cell.numericCellValue
                    4 -> person.taxModel.insurance = cell.numericCellValue
                    12 -> person.taxWithWelfare.welfare = cell.numericCellValue
                }

                SerdeConfig.columnStyle[index + 1] = cell.cellStyle
            }

            if (person.name == ""
                    && person.dept == ""
                    && person.taxModel.salary < 0.01
                    && person.taxModel.insurance < 0.01) {
                return Optional.empty()
            }

            return Optional.of(person)
        }

        fun serialize(workbook: Workbook, peopleModel: Stream<PersonModel>): Workbook {
            val dataFormat = workbook.createDataFormat()
            val moneyFormat = dataFormat.getFormat("#,##0.00_);[Red](#,##0.00)")
            val taxRateFormat = dataFormat.getFormat("0%")

            SerdeConfig.generalStyle = workbook.createCellStyle()
            SerdeConfig.generalStyle?.alignment = HorizontalAlignment.CENTER
            SerdeConfig.generalStyle?.borderLeft = BorderStyle.THIN
            SerdeConfig.generalStyle?.borderRight = BorderStyle.THIN
            SerdeConfig.generalStyle?.borderTop = BorderStyle.THIN
            SerdeConfig.generalStyle?.borderBottom = BorderStyle.THIN

            SerdeConfig.moneyStyle = workbook.createCellStyle()
            SerdeConfig.moneyStyle?.alignment = HorizontalAlignment.CENTER
            SerdeConfig.moneyStyle?.dataFormat = moneyFormat
            SerdeConfig.moneyStyle?.borderLeft = BorderStyle.THIN
            SerdeConfig.moneyStyle?.borderRight = BorderStyle.THIN
            SerdeConfig.moneyStyle?.borderTop = BorderStyle.THIN
            SerdeConfig.moneyStyle?.borderBottom = BorderStyle.THIN

            SerdeConfig.percentStyle = workbook.createCellStyle()
            SerdeConfig.percentStyle?.alignment = HorizontalAlignment.CENTER
            SerdeConfig.percentStyle?.dataFormat = taxRateFormat
            SerdeConfig.percentStyle?.borderLeft = BorderStyle.THIN
            SerdeConfig.percentStyle?.borderRight = BorderStyle.THIN
            SerdeConfig.percentStyle?.borderTop = BorderStyle.THIN
            SerdeConfig.percentStyle?.borderBottom = BorderStyle.THIN

            val people = peopleModel.collect(toList())

            val sheet = workbook.getSheetAt(0)

            people.forEachIndexed { index, person ->
                val sheetIndex = index + SerdeConfig.headNumber

                val row = sheet.createRow(sheetIndex)

                for (cellIndex in 0..19) {
                    when (cellIndex + 1) {
                        1 -> createStringCell(row, cellIndex, person.name)
                        2 -> createStringCell(row, cellIndex, person.dept)
                        3 -> createMoneyCell(row, cellIndex, person.taxModel.salary)
                        4 -> createMoneyCell(row, cellIndex, person.taxModel.insurance)
                        5 -> createGeneralNumCell(row, cellIndex, person.taxModel.taxStandard)
                        6 -> createMoneyCell(row, cellIndex, person.taxModel.taxSalary)
                        7 -> createPercentCell(row, cellIndex, person.taxModel.taxRate)
                        8 -> createGeneralNumCell(row, cellIndex, person.taxModel.taxDeduction)
                        9 -> createMoneyCell(row, cellIndex, person.taxModel.tax)
                        10 -> createMoneyCell(row, cellIndex, person.taxModel.salaryAfterTax)


                        11 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.salary)
                        12 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.welfare)
                        13 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.insurance)
                        14 -> createGeneralNumCell(row, cellIndex, person.taxWithWelfare.taxStandard)
                        15 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.taxSalary)
                        16 -> createPercentCell(row, cellIndex, person.taxWithWelfare.taxRate)
                        17 -> createGeneralNumCell(row, cellIndex, person.taxWithWelfare.taxDeduction)
                        18 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.tax)
                        19 -> createMoneyCell(row, cellIndex, person.taxWithWelfare.salaryAfterTax)
                    }
                }
            }

            for (cellIndex in 0..19) {
                sheet.autoSizeColumn(cellIndex)
            }

            return workbook
        }

        private fun createGeneralNumCell(row: Row, index: Int, value: Double): Cell {
            val cell = row.createCell(index, CellType.NUMERIC)
            cell.cellStyle = SerdeConfig.generalStyle
            cell.setCellValue(value)

            return cell
        }

        private fun createPercentCell(row: Row, index: Int, value: Double): Cell {
            val cell = row.createCell(index, CellType.NUMERIC)
            cell.cellStyle = SerdeConfig.percentStyle
            cell.setCellValue(value)

            return cell
        }

        private fun createMoneyCell(row: Row, index: Int, value: Double): Cell {
            val cell = row.createCell(index, CellType.NUMERIC)
            cell.cellStyle = SerdeConfig.moneyStyle
            cell.setCellValue(value)

            return cell
        }

        private fun createStringCell(row: Row, index: Int, value: String): Cell {
            val cell = row.createCell(index, CellType.STRING)
            cell.cellStyle = SerdeConfig.generalStyle
            cell.setCellValue(value)

            return cell
        }
    }
}