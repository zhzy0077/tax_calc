package com.example.model


data class PersonModel (
        var name: String = "",
        var dept: String = "",
        val taxModel: TaxModel = TaxModel(),
        val taxWithWelfare: TaxModel = TaxModel()
)