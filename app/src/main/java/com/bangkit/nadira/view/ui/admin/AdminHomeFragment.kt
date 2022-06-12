package com.bangkit.nadira.view.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.FragmentAdminHomeBinding
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const

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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Admin Lapor Satgas"

        vbind.labelName.text = Preference(requireContext()).getPrefString(const.USER_NAME).toString()
        vbind.labelEmail.text = Preference(requireContext()).getPrefString(const.USER_EMAIL).toString()

    }


}