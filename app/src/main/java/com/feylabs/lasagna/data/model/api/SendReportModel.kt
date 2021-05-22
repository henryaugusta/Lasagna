package com.feylabs.lasagna.data.model.api

import java.io.File

data class SendReportModel(
    var id_people  : String,
    var id_category  : String,
    var is_public : String,
    var detail_kejadian  : String,
    var detail_alamat  : String,
    var lat  : Double,
    var long  : Double,
    var photo  : File,
    var status  : String
)