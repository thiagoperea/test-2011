package com.thiagoperea.mybills.datasource.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.thiagoperea.mybills.core.Constants
import com.thiagoperea.mybills.core.NotificationDispatcher
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.datasource.R
import com.thiagoperea.mybills.datasource.model.Bill

class DailyNotificationWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    companion object {
        const val WORKER_TAG = "DailyNotificationWorker_TAG"
        const val KEY_USER_ID = "key.user.id"
    }

    override fun doWork(): Result {
        val userId = inputData.getString(KEY_USER_ID) ?: ""

        val task = FirebaseFirestore.getInstance()
            .collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .whereEqualTo("isDone", false)
            .get()

        return try {
            val result = Tasks.await(task)
            val billList = result.toObjects<Bill>()
            prepareNotification(billList)

            Result.success()
        } catch (error: Exception) {
            Result.failure()
        }
    }

    private fun prepareNotification(billList: List<Bill>) {
        val today = DateConverter.getTodayAsCalendar().time

        if (billList.isNotEmpty()) {
            val hasExpiredBills = billList.any { it.dueDate?.before(today) == true }
            val hasBillsToExpire = billList.any { it.dueDate?.equals(today) == true || it.dueDate?.after(today) == true }

            if (hasExpiredBills) {
                sendExpiredNotification()
            } else if (hasBillsToExpire) {
                sendToExpireNotification()
            }
        }
    }

    private fun sendToExpireNotification() {
        NotificationDispatcher.makeStatusNotification(
            context.getString(R.string.notification_bill_to_expire_title),
            context.getString(R.string.notification_bill_to_expire_message),
            R.drawable.ic_money,
            true,
            context
        )
    }

    private fun sendExpiredNotification() {
        NotificationDispatcher.makeStatusNotification(
            context.getString(R.string.notification_bill_expired_title),
            context.getString(R.string.notification_bill_expired_title),
            R.drawable.ic_money,
            true,
            context
        )
    }

}