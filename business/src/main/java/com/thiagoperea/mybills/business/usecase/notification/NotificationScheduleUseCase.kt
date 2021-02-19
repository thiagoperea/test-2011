package com.thiagoperea.mybills.business.usecase.notification

import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.thiagoperea.mybills.datasource.repository.UserRepository
import com.thiagoperea.mybills.datasource.worker.DailyNotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationScheduleUseCase(
    private val userRepository: UserRepository,
    private val workManager: WorkManager,
) {

    fun schedule(hour: Int, minute: Int) {
        removePreviousSchedules()

        val userId = userRepository.getCurrentUser()?.uid

        val nextNotification = getNextNotificationTimeInMillis(hour, minute)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<DailyNotificationWorker>(1L, TimeUnit.DAYS)
            .setInitialDelay(nextNotification, TimeUnit.MILLISECONDS)
            .addTag(DailyNotificationWorker.WORKER_TAG)
            .setConstraints(constraints)
            .setInputData(workDataOf(DailyNotificationWorker.KEY_USER_ID to userId))
            .build()

        workManager.enqueue(request)
    }

    fun removePreviousSchedules() {
        workManager.cancelAllWorkByTag(DailyNotificationWorker.WORKER_TAG)
    }

    fun getNextNotificationTimeInMillis(hour: Int, minute: Int): Long {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        dueDate.set(Calendar.HOUR_OF_DAY, hour)
        dueDate.set(Calendar.MINUTE, minute)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.set(Calendar.MILLISECOND, 0)

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        return dueDate.timeInMillis - currentDate.timeInMillis
    }
}
