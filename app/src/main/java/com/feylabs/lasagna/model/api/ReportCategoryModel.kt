package com.feylabs.lasagna.model.api

data class ReportCategoryModel(
    val data: MutableList<Data>,
    val http_response: Int,
    val message: String,
    val message_id: String,
    val size: Int,
    val status_code: Int
){
    data class Data(
        val category_name: String,
        val created_at: String,
        val deleted_at: Any,
        val deleted_by: Any,
        val id: Int,
        val photo_path: String,
        val updated_at: String,
        var isSelected : Boolean= false
    )
}