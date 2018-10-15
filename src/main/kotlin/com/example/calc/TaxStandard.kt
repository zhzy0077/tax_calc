package com.example.calc

object TaxStandard {
    const val taxStandard = 5000.0

//    const val taxStandard = 3500.0

    private val standards = listOf(
            TaxResult(0.0, 0.03, 0.0),
            TaxResult(3000.0, 0.10, 210.0),
            TaxResult(12000.0, 0.20, 1410.0),
            TaxResult(25000.0, 0.25, 2660.0),
            TaxResult(35000.0, 0.30, 4410.0),
            TaxResult(55000.0, 0.35, 7160.0),
            TaxResult(80000.0, 0.45, 15160.0)
    )

//    private val standards = listOf(
//            TaxResult(0.0, 0.03, 0.0),
//            TaxResult(1500.0, 0.10, 105.0),
//            TaxResult(4500.0, 0.20, 555.0),
//            TaxResult(9000.0, 0.25, 1005.0),
//            TaxResult(35000.0, 0.30, 2755.0),
//            TaxResult(55000.0, 0.35, 5505.0),
//            TaxResult(80000.0, 0.45, 13505.0)
//    )


    fun calcTax(taxSalary: Double): TaxResult {
        if (taxSalary <= 0.0) {
            val current = standards.first()
            val tax = 0.0
            val salaryAfterTax = taxSalary - tax + taxStandard
            return current.copy(tax = tax, salaryAfterTax = salaryAfterTax, taxSalary = taxSalary)
        }

        for ((current, next) in standards.zipWithNext()) {
            if (taxSalary > current.start && taxSalary <= next.start) {
                val tax = taxSalary * current.taxRate - current.taxDeduction
                val salaryAfterTax = taxSalary - tax + taxStandard
                return current.copy(tax = tax, salaryAfterTax = salaryAfterTax, taxSalary = taxSalary)
            }
        }

        val current = standards.last()
        val tax = taxSalary * current.taxRate - current.taxDeduction
        val salaryAfterTax = taxSalary - tax + taxStandard
        return current.copy(tax = tax, salaryAfterTax = salaryAfterTax, taxSalary = taxSalary)
    }

    fun calcSalary(salaryAfterTax: Double, welfare: Double, insurance: Double): TaxResult {
        for ((current, next) in standards.zipWithNext()) {
            val salary =
                    (salaryAfterTax + welfare * current.taxRate - taxStandard * current.taxRate - current.taxDeduction) /
                            (1 - current.taxRate) + insurance
            val taxSalary = salary + welfare - insurance - taxStandard
            if (taxSalary > current.start && taxSalary <= next.start) {
                val tax = salary - salaryAfterTax
                return current.copy(tax = tax, salaryAfterTax = salaryAfterTax, taxSalary = taxSalary, salary = salary)
            }
        }
        val current = standards.last()
        val salary =
                (salaryAfterTax + welfare * current.taxRate - taxStandard * current.taxRate - current.taxDeduction) /
                        (1 - current.taxRate) + insurance
        val taxSalary = salary + welfare - insurance - taxStandard
        val tax = taxSalary - salaryAfterTax
        return current.copy(tax = tax, salaryAfterTax = salaryAfterTax, taxSalary = taxSalary, salary = salary)
    }
}

data class TaxResult(
        val start: Double,
        val taxRate: Double,
        val taxDeduction: Double,
        val taxSalary: Double = 0.0,
        val tax: Double = 0.0,
        val salaryAfterTax: Double = 0.0,
        val salary: Double = 0.0
)