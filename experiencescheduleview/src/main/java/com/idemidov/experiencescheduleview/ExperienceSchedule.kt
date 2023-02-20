package com.idemidov.experiencescheduleview

interface ExperienceSchedule<T: Schedule> {

    fun getBegin(): Long

    fun getEnd(): Long

    fun getSchedule(): HashMap<Long, List<T>>
}