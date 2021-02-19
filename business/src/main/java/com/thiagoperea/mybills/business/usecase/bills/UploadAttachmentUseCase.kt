package com.thiagoperea.mybills.business.usecase.bills

import android.net.Uri
import androidx.lifecycle.Observer
import androidx.work.*
import com.thiagoperea.mybills.datasource.repository.BillsRepository
import com.thiagoperea.mybills.datasource.repository.UserRepository
import com.thiagoperea.mybills.datasource.worker.UploadAttachmentWorker

class UploadAttachmentUseCase(
    private val workManager: WorkManager,
    private val userRepository: UserRepository,
    private val billsRepository: BillsRepository,
) {

    fun execute(
        attachmentUri: Uri?,
        billId: String,
    ) {
        val userId = userRepository.getCurrentUser()?.uid ?: ""

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = createUploadRequest(userId, attachmentUri, constraints)

        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observeForever(object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo) {
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val downloadUrl = workInfo.outputData.getString(UploadAttachmentWorker.OutputData.KEY_UPLOAD_RESULT) ?: ""
                        billsRepository.setupAttachmentUrl(billId, userId, downloadUrl)

                        // clear observer
                        workManager.getWorkInfoByIdLiveData(uploadRequest.id).removeObserver(this)
                    }
                }
            })
        workManager.enqueue(uploadRequest)
    }

    /**
     * Create Upload Request Worker
     */
    private fun createUploadRequest(
        userId: String,
        attachmentUri: Uri?,
        constraints: Constraints,
    ): WorkRequest {

        val inputData = workDataOf(
            UploadAttachmentWorker.InputData.KEY_USER_ID to userId,
            UploadAttachmentWorker.InputData.KEY_ATTACHMENT_URI to attachmentUri.toString()
        )

        return OneTimeWorkRequest.Builder(UploadAttachmentWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
    }
}
