package com.idemidov.experiencescheduleview.demo

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.idemidov.experiencescheduleview.ExperienceSchedule
import com.idemidov.experiencescheduleview.ExperienceScheduleClickListener
import com.idemidov.experiencescheduleview.ExperienceScheduleView
import com.idemidov.experiencescheduleview.Schedule

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val experienceScheduleView = findViewById<ExperienceScheduleView>(R.id.experienceScheduleView)
        experienceScheduleView.setOnSlotClickListener(object:ExperienceScheduleClickListener {
            override fun onSlotClick(slots: List<Schedule>) {
                val type = slots.first().getType()
                Toast.makeText(this@MainActivity, type, Toast.LENGTH_SHORT).show()
            }
        })
        experienceScheduleView.setSchedule(object : ExperienceSchedule<Schedule> {
            override fun getBegin() = 1671066000L // Thursday, December 15, 2022 1:00:00 AM

            override fun getEnd() = 1676422800L // Wednesday, February 15, 2023 1:00:00 AM

            override fun getSchedule(): HashMap<Long, List<Schedule>> {
                val map = HashMap<Long, List<Schedule>>()
                val schedule = arrayListOf<Schedule>()
                schedule.add(object : Schedule {
                    override fun getType() = "slot"

                    override fun getTimeStart() = null

                    override fun getTimeEnd() = null

                    override fun getTime() = "15:00"

                    override fun getBookedPersons() = 0

                })
                map[1673096400L] = schedule
                map[1673571600L] = schedule
                map[1674090000L] = schedule
                return map
            }
        })
    }
}