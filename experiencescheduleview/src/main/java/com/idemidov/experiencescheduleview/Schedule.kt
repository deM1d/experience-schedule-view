package com.idemidov.experiencescheduleview

interface Schedule {

    fun getType(): String

    fun getTimeStart(): String?

    fun getTimeEnd(): String?

    fun getTime(): String?

    fun getBookedPersons(): Int?
}