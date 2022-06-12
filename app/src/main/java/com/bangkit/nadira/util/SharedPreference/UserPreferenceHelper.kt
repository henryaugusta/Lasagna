package com.bangkit.nadira.util.SharedPreference

import android.content.Context
import com.bangkit.nadira.util.SharedPreference.const.USER_CONTACT
import com.bangkit.nadira.util.SharedPreference.const.USER_EMAIL
import com.bangkit.nadira.util.SharedPreference.const.USER_GENDER
import com.bangkit.nadira.util.SharedPreference.const.USER_NAME
import com.bangkit.nadira.util.SharedPreference.const.USER_NIK
import com.bangkit.nadira.util.SharedPreference.const.USER_PHOTO
import com.bangkit.nadira.util.SharedPreference.const.USER_USERNAME

object UserPreferenceHelper {

    fun updateUserPreference(
        context: Context,
        name: String,
        username: String,
        email: String,
        contact: String,
        gender: String,
        photo: String,
        nik: String
    ) {
        Preference(context).save(USER_NAME, name)
        Preference(context).save(USER_USERNAME, username.toString())
        Preference(context).save(USER_EMAIL, email.toString())
        Preference(context).save(USER_CONTACT, contact.toString())
        Preference(context).save(USER_GENDER, contact.toString())
        Preference(context).save(USER_PHOTO, photo.toString())
        Preference(context).save(USER_NIK, nik.toString())
    }

}