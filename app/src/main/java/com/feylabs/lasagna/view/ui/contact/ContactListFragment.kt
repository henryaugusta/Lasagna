package com.feylabs.lasagna.view.ui.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.local.ContactItemModel
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.FragmentContactListBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.util.networking.Endpoint
import com.feylabs.lasagna.viewmodel.MainMenuUserViewModel
import com.feylabs.lasagna.view.bottom_sheet.OptionContactBottomSheet


class ContactListFragment : BaseFragment() {

    lateinit var vbind: FragmentContactListBinding
    lateinit var viewmodel: ContactViewModel

    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }


    val adapterContact by lazy { ContactAdapter() }

    val bottomSheet by lazy { OptionContactBottomSheet(requireActivity()) }

    val tempContactList = mutableListOf<ContactItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        vbind = FragmentContactListBinding.bind(
            inflater.inflate(
                R.layout.fragment_contact_list,
                container,
                false
            )
        )
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Kontak Instansi"
        menuViewModel.title.value = "Kontak Instansi Penting"

        if (Preference(requireContext()).getPrefString(const.USER_TYPE) != "admin") {
            vbind.btnAdd.visibility=View.GONE
        }

        vbind.rvContact.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter= adapterContact
        }

        vbind.btnAdd.setOnClickListener {
            Navigation.findNavController(vbind.root).navigate(R.id.action_nav_contact_to_createContactFragment)
        }

        val factory = ContactViewModelFactory(LasagnaRepository(RemoteDataSource()))
        viewmodel = ViewModelProvider(requireActivity(), factory).get(ContactViewModel::class.java)

        val observerDelete = Observer<Resource<String>> {
            when (it) {
                is Resource.Success -> {
                    tempContactList.clear()
                    observeContact()
                    showSweetAlert("Berhasil","Berhasil Menghapus Kontak",R.color.xdGreen)
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert("Gagal",it.message.toString(),R.color.xdRed)
                    vbind.includeLoading.loadingRoot.setGone()
                }
            }
        }


        adapterContact.setInterface(object : ContactAdapter.ContactAdapterInterface {
            override fun onclick(model: ContactItemModel) {
                bottomSheet.show()
                bottomSheet.myVbind.labelDesc.text = " ${model.deskripsi}"
                bottomSheet.myVbind.labelTitle.text =  "Nama Instansi : ${model.name}"
                bottomSheet.myVbind.btnSeeCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.deskripsi))
                    startActivity(intent)
                }

                if (Preference(requireContext()).getPrefString(const.USER_TYPE) != "admin") {
                    bottomSheet.myVbind.btnDelete.visibility=View.GONE
                }

                bottomSheet.myVbind.btnDelete.setOnClickListener {
                    bottomSheet.dismiss()
                    viewmodel.deleteContact(model.id.toString()).observe(viewLifecycleOwner,observerDelete)
                }

                Glide
                    .with(vbind.root)
                    .load("${Endpoint.REAL_URL}${model.photoPath}")
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .centerCrop()
                    .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                    .placeholder(R.drawable.ic_loading_small_1)
                    .into(bottomSheet.myVbind.ivCover)

            }
        })


        observeContact()

    }

    private fun observeContact() {
        viewmodel.getContact().observe(viewLifecycleOwner, Observer {
            Log.d("contact_observer", it.toString())
            when (it) {
                is Resource.Success -> {
                    tempContactList.clear()
                    vbind.includeLoading.loadingRoot.setGone()
                    it.data?.let { it1 ->
                        tempContactList.addAll(it1)
                        adapterContact.setData(tempContactList)
                        adapterContact.notifyDataSetChanged()
                    }
                }
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                    it.data?.let { it1 -> tempContactList.addAll(it1) }
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.setGone()
                    it.data?.let { it1 -> tempContactList.addAll(it1) }
                }
            }
        })
    }


}