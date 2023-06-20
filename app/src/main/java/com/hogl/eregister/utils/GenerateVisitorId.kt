package com.hogl.eregister.utils

import java.util.*

internal object GenerateVisitorId {

    fun getId(): Int {
        val rand = Random()
        val upperbound = 25
        return rand.nextInt(upperbound)

    }
}

object GenerateQRCode {

    fun getId(): Int {
        val rand = Random()
        val upperbound = 999999999
        return rand.nextInt(upperbound)
    }
}