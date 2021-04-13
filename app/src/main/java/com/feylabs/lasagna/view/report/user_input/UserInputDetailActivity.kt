package com.feylabs.lasagna.view.report.user_input

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.feylabs.lasagna.databinding.ActivityUserInputDetailBinding

class UserInputDetailActivity : AppCompatActivity() {

    lateinit var viewBinding: ActivityUserInputDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityUserInputDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val extras = intent.extras
        val myUri = Uri.parse(extras!!.getString("imageUri"))
        viewBinding.roundedImageView.setImageURI(myUri)

    }
}