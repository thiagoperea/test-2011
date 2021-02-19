package com.thiagoperea.mybills.datasource.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Bill(
    var id: String? = null,
    val totalValue: Double? = null,
    val description: String? = null,
    val dueDate: Date? = null,
    @field:JvmField val isReceivable: Boolean? = null,
    @field:JvmField val isDone: Boolean? = null,
    val attachmentUrl: String? = null,
    @field:JvmField var isOverdue: Boolean? = null, //TODO: remover esse campo, calcular usando dueDate
) : Parcelable
