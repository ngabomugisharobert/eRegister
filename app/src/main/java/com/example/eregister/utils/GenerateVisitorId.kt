package com.example.eregister.utils

import java.util.*

internal object GenerateVisitorId {

    fun getId(): Int {
        val rand = Random()
        val upperbound = 25
        return rand.nextInt(upperbound)

    }
}
