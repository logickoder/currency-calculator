package dev.logickoder.currencycalculator.data.api

import dev.logickoder.currencycalculator.data.api.RatesApi.Companion.BASE_URL
import dev.logickoder.currencycalculator.di.Retrofit

const val DAYS_CAP = 90

val RatesApiImpl: RatesApi = Retrofit
    .baseUrl(BASE_URL)
    .build()
    .create(RatesApi::class.java)