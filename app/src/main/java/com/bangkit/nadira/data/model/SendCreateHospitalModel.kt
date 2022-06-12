package com.bangkit.nadira.data.model

import java.io.File

data class SendCreateHospitalModel(
    val id : String = "",
    val alamat : String,
    val name : String,
    val deskripsi : String,
    val photo:File,
    val operasional : String,
    val kontak_rs : String,
    val kontak_ambulance : String,
    val lat : String,
    val long : String,
    val fasilitas : String
)