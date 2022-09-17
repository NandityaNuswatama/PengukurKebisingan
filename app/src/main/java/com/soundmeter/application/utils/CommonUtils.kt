package com.soundmeter.application.utils

fun String.toDoubleReplaceComma(): Double = this.replace(",", ".").toDouble()