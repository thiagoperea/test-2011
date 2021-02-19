package com.thiagoperea.mybills.datasource.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.thiagoperea.mybills.core.Constants
import com.thiagoperea.mybills.core.converter.DateConverter
import com.thiagoperea.mybills.datasource.model.Bill
import java.util.*

class BillsRepository(
    private val firestore: FirebaseFirestore,
) {

    /**
     *  Get all bills from current user
     */
    fun getBills(
        isReceivable: Boolean,
        isDone: Boolean,
        userId: String,
        onSuccess: (List<Bill>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .whereEqualTo("isReceivable", isReceivable)
            .whereEqualTo("isDone", isDone)
            .get()
            .addOnSuccessListener { query ->
                val billList = mutableListOf<Bill>()

                query.documents.forEach { bill ->
                    val current = bill.toObject<Bill>()
                    current?.id = bill.id
                    billList.add(current ?: Bill())
                }

                onSuccess(billList)
            }
            .addOnFailureListener {
                onError(RuntimeException())
            }
    }

    /**
     * Save [bill] to current user
     */
    fun saveBill(
        bill: Bill,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .add(bill)
            .addOnSuccessListener { ref ->
                val billId = ref.id
                onSuccess(billId)
            }
            .addOnFailureListener { onError(RuntimeException()) }
    }

    /**
     * Update Bill with [billData]
     */
    fun updateBill(
        billId: String,
        billData: Map<String, Any?>,
        userId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit,
    ) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .document(billId)
            .update(billData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(RuntimeException()) }
    }

    /**
     *  Set [attachmentUrl] to Bill with [billId]
     */
    fun setupAttachmentUrl(
        billId: String,
        userId: String,
        attachmentUrl: String,
    ) {
        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .document(billId)
            .update(mapOf("attachmentUrl" to attachmentUrl))
    }

    /**
     * get all expired Bills from user
     */
    fun getExpiredBillsList(
        userId: String,
        onSuccess: (List<Bill>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val today = DateConverter.getTodayAsCalendar().time

        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .whereEqualTo("isDone", false)
            .get()
            .addOnSuccessListener { result ->
                val billList = mutableListOf<Bill>()

                result.documents.forEach { bill ->
                    val current = bill.toObject<Bill>()

                    if (current == null || current.dueDate?.after(today) == true || current.dueDate?.equals(today) == true) {
                        return@forEach
                    }

                    current.id = bill.id
                    billList.add(current)
                }

                onSuccess(billList)
            }
            .addOnFailureListener { error -> onError(error) }
    }

    /**
     * Get all bills not expired from user
     */
    fun getNotExpiredBillsList(
        userId: String,
        isReceivable: Boolean,
        onSuccess: (List<Bill>) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        val today = DateConverter.getTodayAsCalendar().time

        firestore.collection(Constants.FIRESTORE_COLLECTION_USER)
            .document(userId)
            .collection(Constants.FIRESTORE_SUBCOLLECTION_BILLS)
            .whereEqualTo("isDone", false)
            .whereEqualTo("isReceivable", isReceivable)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                val billList = mutableListOf<Bill>()

                result.documents.forEach { bill ->
                    val current = bill.toObject<Bill>()

                    if (current == null || current.dueDate?.before(today) == true) {
                        return@forEach
                    }

                    current.id = bill.id
                    billList.add(current)
                }

                onSuccess(billList)
            }
            .addOnFailureListener { error -> onError(error) }
    }
}