[![Maven Central][img version shield]][maven]

ExperienceScheduleView
=======================
Custom view to display Tripster expereinces schedule.

![preview](readmeAssets/demo.gif)

## Getting started
Add dependency to Gradle script:
```Groovy
implementation "ru.idemidov:experiencescheduleview:x.x.x"
```
1. Add view to layout:
```xml
    <com.idemidov.experiencescheduleview.ExperienceScheduleView
        android:id="@+id/experienceScheduleView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```
2. View works with ExperienceSchedule interface. I model have to implement it or you should write some converter fot it:
```kotlin
    interface ExperienceSchedule<T: Schedule> {

        fun getBegin(): Long

        fun getEnd(): Long

        fun getSchedule(): HashMap<Long, List<T>>
    }

    interface Schedule {

        fun getType(): String

        fun getTimeStart(): String?

        fun getTimeEnd(): String?

        fun getTime(): String?

        fun getBookedPersons(): Int?
    }
```
3. When data is ready to draw, just call setSchedule() method:
```kotlin
    val experienceScheduleView = findViewById<ExperienceScheduleView>(R.id.experienceScheduleView)
    experienceScheduleView.setSchedule(scheduleData)
```
4. You also can set click listener. It passes list of availble schedules for clicked date:
```kotlin
    val experienceScheduleView = findViewById<ExperienceScheduleView>(R.id.experienceScheduleView)
    experienceScheduleView.setOnSlotClickListener(object:ExperienceScheduleClickListener {
        override fun onSlotClick(slots: List<Schedule>) {
            ...
        }
    })
```
## Customization
| Attribute               | Explanation                                                                                                                                                                                                                                   | Default Value                                                                                       |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| background_color| The color of view background | ```#FFFFFF``` |
| year_text_color| The color of year text | ```#495662``` |
| month_text_color | The color of month text | ```#495662``` |
| days_text_color | The color of days text | ```#CABCC7``` |
| slot_background_available_color | The color of available date background | ```#00BE8B``` |
| slot_background_booked_color | The color of booked date background | ```#FFA4B3``` |
| slot_background_inactive_color | The color of inactive date background | ```#EFEFEF```|
| slot_text_available_color | The color of available date text | ```#FFFFFF``` |
| slot_text_booked_color | The color of booked date text | ```#2B3A49``` |
| slot_text_inactive_color | The color of inactive date text | ```#BBBCBF``` |
| year_text_size | The size of year text | `18dp` |
| month_text_size | The size of month text | `18dp` |
| days_text_size | The size of days text | `16dp` |
| slot_text_size | The size of date text | `16dp` |
| buttons_size | The size of "left" and "right" buttons | `36dp` | 
| slots_height | The heigth of date slots | `36dp` |

## License
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[img version shield]: https://img.shields.io/maven-central/v/com.idemidov/experiencescheduleview.svg?maxAge=3600
[maven]: https://central.sonatype.com/namespace/com.idemidov
