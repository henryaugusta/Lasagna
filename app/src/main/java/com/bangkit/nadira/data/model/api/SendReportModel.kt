package com.bangkit.nadira.data.model.api

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
    var status  : String,

    var waktu_kejadian  : String,
    var penyebab_bencana  : String,
    var kerusakan_bangunan  : String,
    var kerusakan_lain  : String,
    var korban_jiwa  : String,
    var kondisi_korban  : String
)