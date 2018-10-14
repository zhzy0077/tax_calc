package com.example.model

data class TaxModel(
        var salary: Double = 0.0,
        var welfare: Double = 0.0,
        var insurance: Double = 0.0,
        var taxStandard: Double = 0.0,
        var taxSalary: Double = 0.0,
        var taxRate: Double = 0.0,
        var taxDeduction: Double = 0.0,
        var tax: Double = 0.0,
        var salaryAfterTax: Double = 0.0
)