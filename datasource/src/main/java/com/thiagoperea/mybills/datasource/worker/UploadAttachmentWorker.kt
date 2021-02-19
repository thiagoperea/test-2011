package com.thiagoperea.mybills.datasource.worker

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.thiagoperea.mybills.core.Constants
import com.thiagoperea.mybills.core.NotificationDispatcher
import com.thiagoperea.mybills.datasource.R

class UploadAttachmentWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters,
) : Worker(appContext, workerParameters) {

    object InputData {
        const val KEY_USER_ID = "key.user.id"
        const val KEY_ATTACHMENT_URI = "key.attachment.uri"
    }

    object OutputData {
        const val KEY_UPLOAD_RESULT = "key.upload.result"
    }

    override fun doWork(): Result {
        return try {
            sendNotification(R.string.notification_upload_starting, false)

            val userId = inputData.getString(InputData.KEY_USER_ID) ?: ""
            val attachmentUri = Uri.parse(inputData.getString(InputData.KEY_ATTACHMENT_URI))

            val fileRef = FirebaseStorage.getInstance()
                .reference
                .child(Constants.STORAGE_USERS_ATTACHMENT_PATH)
                .child(userId)
                .child(attachmentUri.lastPathSegment ?: "unnamed_${System.currentTimeMillis()}")

            val uploadTask = fileRef.putFile(attachmentUri).continueWithTask { fileRef.downloadUrl }
            val uploadResult = Tasks.await(uploadTask)

            sendNotification(R.string.notification_upload_success, true)
            Result.success(workDataOf(OutputData.KEY_UPLOAD_RESULT to uploadResult.toString()))
        } catch (error: Exception) {
            sendNotification(R.string.notification_upload_error, true)
            Result.failure()
        }
    }

    private fun sendNotification(@StringRes messageId: Int, isCancellable: Boolean) {
        NotificationDispatcher.makeStatusNotification(
            appContext.getString(R.string.notification_upload_title),
            appContext.getString(messageId),
            R.drawable.ic_upload,
            isCancellable,
            appContext
        )
    }
}