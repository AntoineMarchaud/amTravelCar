package com.amarchaud.travelcar.utils.extensions

fun List<String>.toReadableString() = this.toString().removePrefix("[").removeSuffix("]")