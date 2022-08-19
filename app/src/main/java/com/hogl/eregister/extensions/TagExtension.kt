package com.hogl.eregister.extensions

import android.nfc.Tag
import java.lang.StringBuilder

object TagExtension {

	fun Tag.getTagId(): String {
		val tagBuilder = StringBuilder()
		for (hex in id) {
			tagBuilder.append(String.format("%02X", hex))
		}
		return tagBuilder.toString()
	}

}