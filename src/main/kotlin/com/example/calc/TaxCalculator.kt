package com.example.calc

import com.example.model.PersonModel
import java.util.stream.Stream

class TaxCalculator {
    companion object {
        fun calculate(people: Stream<PersonModel>): Stream<PersonModel> {
            return people.map { person ->

                person.taxModel.taxStandard = TaxStandard.taxStandard
                person.taxModel.taxSalary = person.taxModel.salary - person.taxModel.insurance - TaxStandard.taxStandard

                person
            }.map { person ->
                val taxResult = TaxStandard.calcTax(person.taxModel.taxSalary)

                if (person.taxModel.taxSalary <= 0.0) {
                    person.taxModel.taxSalary = 0.0
                }

                person.taxModel.taxRate = taxResult.taxRate
                person.taxModel.taxDeduction = taxResult.taxDeduction
                person.taxModel.tax = taxResult.tax
                person.taxModel.salaryAfterTax = taxResult.salaryAfterTax

                person
            }.map { person ->
                person.taxWithWelfare.insurance = person.taxModel.insurance
                person.taxWithWelfare.taxStandard = person.taxModel.taxStandard
                person.taxWithWelfare.salaryAfterTax = person.taxModel.salaryAfterTax

                person
            }.map { person ->
                val taxResult = TaxStandard.calcSalary(
                        person.taxWithWelfare.salaryAfterTax,
                        person.taxWithWelfare.welfare,
                        person.taxWithWelfare.insurance
                )

                person.taxWithWelfare.taxRate = taxResult.taxRate
                person.taxWithWelfare.taxDeduction = taxResult.taxDeduction
                person.taxWithWelfare.tax = taxResult.tax
                person.taxWithWelfare.taxSalary = taxResult.taxSalary
                person.taxWithWelfare.salary = taxResult.salary

                person
            }
        }

    }
}