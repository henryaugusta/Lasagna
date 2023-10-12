package com.bangkit.nadira.view.ui.admin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.FragmentAdminHomeBinding
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const
import com.bangkit.nadira.util.networking.Endpoint
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject


class AdminHomeFragment : Fragment() {

    lateinit var vbind: FragmentAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        vbind = FragmentAdminHomeBinding.bind(inflater.inflate(R.layout.fragment_admin_home, container, false))
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionState =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
        // If the permission is not granted, request it.
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        vbind.containerRS.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_home_to_listHospitalActivity)
        }

        vbind.containerCategory.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_home_to_categoryManageFragment)
        }

        vbind.containerNews.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_home_to_manageNewsFragment)
        }

        vbind.containerKontak.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_home_to_nav_contact)
        }

        vbind.containerReport.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_home_to_listReportActivity)
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"

        vbind.labelName.text = Preference(requireContext()).getPrefString(const.USER_NAME).toString()
        vbind.labelEmail.text = Preference(requireContext()).getPrefString(const.USER_EMAIL).toString()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val name = Preference(requireContext()).getPrefString(const.USER_NAME)
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val endpoint = Endpoint.BASE_URL +"/store-fcm";
                        val requestUrl = endpoint
                        AndroidNetworking.post(requestUrl)
                            .setPriority(Priority.MEDIUM)
                            .addBodyParameter("username",name)
                            .addBodyParameter("token",task.result.toString())
                            .addBodyParameter("device","admin")
                            .build()
                            .getAsJSONObject(object : JSONObjectRequestListener {
                                override fun onResponse(response: JSONObject) {
                                }

                                override fun onError(anError: ANError) {

                                }
                            })
                    }
                }
            }


    }


}