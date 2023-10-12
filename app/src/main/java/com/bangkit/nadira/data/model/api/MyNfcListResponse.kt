package com.bangkit.nadira.data.model.api


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class MyNfcListResponse : ArrayList<MyNfcListResponse.MyNfcListResponseItem>(){
    data class MyNfcListResponseItem(
        @SerializedName("added_by")
        @Expose
        val addedBy: String? = null,
        @SerializedName("card_id")
        @Expose
        val cardId: String? = null,
        @SerializedName("created_at")
        @Expose
        val createdAt: String? = null,
        @SerializedName("id")
        @Expose
        val id: Int? = null,
        @SerializedName("label")
        @Expose
        val label: String? = null,
        @SerializedName("updated_at")
        @Expose
        val updatedAt: String? = null
    )
}