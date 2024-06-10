package com.mss.mssproject.extension

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class ToFormattedStringKtTest : FunSpec({

    val testList = listOf(
        BigDecimal.valueOf(100) to "100",
        BigDecimal.valueOf(1_000) to "1,000",
        BigDecimal.valueOf(10_000) to "10,000",
        BigDecimal.valueOf(100_000) to "100,000",
        BigDecimal.valueOf(1_000_000) to "1,000,000",
    )

    test("first.toFormattedString shouldBe second") {
        testList.forEach {
            it.first.toFormattedString() shouldBe it.second
        }
    }
})
