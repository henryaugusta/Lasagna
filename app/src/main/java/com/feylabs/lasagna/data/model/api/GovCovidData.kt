package com.feylabs.lasagna.data.model.api


import com.google.gson.annotations.SerializedName

data class GovCovidData(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("update")
    val update: Update
) {
    data class Data(
        @SerializedName("id")
        val id: Int,
        @SerializedName("jumlah_odp")
        val jumlahOdp: Int,
        @SerializedName("jumlah_pdp")
        val jumlahPdp: Int,
        @SerializedName("total_spesimen")
        val totalSpesimen: Int,
        @SerializedName("total_spesimen_negatif")
        val totalSpesimenNegatif: Int
    )

    data class Update(
        @SerializedName("harian")
        val harian: List<Harian>,
        @SerializedName("penambahan")
        val penambahan: Penambahan,
        @SerializedName("total")
        val total: Total
    ) {
        data class Harian(
            @SerializedName("doc_count")
            val docCount: Int,
            @SerializedName("jumlah_dirawat")
            val jumlahDirawat: JumlahDirawat,
            @SerializedName("jumlah_dirawat_kum")
            val jumlahDirawatKum: JumlahDirawatKum,
            @SerializedName("jumlah_meninggal")
            val jumlahMeninggal: JumlahMeninggal,
            @SerializedName("jumlah_meninggal_kum")
            val jumlahMeninggalKum: JumlahMeninggalKum,
            @SerializedName("jumlah_positif")
            val jumlahPositif: JumlahPositif,
            @SerializedName("jumlah_positif_kum")
            val jumlahPositifKum: JumlahPositifKum,
            @SerializedName("jumlah_sembuh")
            val jumlahSembuh: JumlahSembuh,
            @SerializedName("jumlah_sembuh_kum")
            val jumlahSembuhKum: JumlahSembuhKum,
            @SerializedName("key")
            val key: Long,
            @SerializedName("key_as_string")
            val keyAsString: String
        ) {
            data class JumlahDirawat(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahDirawatKum(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahMeninggal(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahMeninggalKum(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahPositif(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahPositifKum(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahSembuh(
                @SerializedName("value")
                val value: Int
            )

            data class JumlahSembuhKum(
                @SerializedName("value")
                val value: Int
            )
        }

        data class Penambahan(
            @SerializedName("created")
            val created: String,
            @SerializedName("jumlah_dirawat")
            val jumlahDirawat: Int,
            @SerializedName("jumlah_meninggal")
            val jumlahMeninggal: Int,
            @SerializedName("jumlah_positif")
            val jumlahPositif: Int,
            @SerializedName("jumlah_sembuh")
            val jumlahSembuh: Int,
            @SerializedName("tanggal")
            val tanggal: String
        )

        data class Total(
            @SerializedName("jumlah_dirawat")
            val jumlahDirawat: Int,
            @SerializedName("jumlah_meninggal")
            val jumlahMeninggal: Int,
            @SerializedName("jumlah_positif")
            val jumlahPositif: Int,
            @SerializedName("jumlah_sembuh")
            val jumlahSembuh: Int
        )
    }
}