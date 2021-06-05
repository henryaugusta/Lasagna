package com.feylabs.lasagna.view.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.model.api.ReportCategoryModel
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.BottomsheetManageCategoryBinding
import com.feylabs.lasagna.databinding.FragmentCategoryManageBinding
import com.feylabs.lasagna.databinding.FragmentContactListBinding
import com.feylabs.lasagna.databinding.ItemCategoryForAdminBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.view.MainMenuUserViewModel
import com.feylabs.lasagna.view.ui.category.CategoryEditFragment.Companion.CATEGORY_ID
import com.feylabs.lasagna.view.ui.category.CategoryEditFragment.Companion.CATEGORY_NAME
import com.feylabs.lasagna.view.ui.category.CategoryEditFragment.Companion.CATEGORY_PHOTO_PATH
import com.feylabs.lasagna.view.ui.contact.ContactViewModelFactory
import com.feylabs.lasagna.viewmodel.CategoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class CategoryManageFragment : BaseFragment() {

    lateinit var vbind: FragmentCategoryManageBinding
    lateinit var viewmodel: CategoryViewModel

    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }


    val adapterCategory by lazy { CategoryAdminAdapter() }

    val tempCategory = mutableListOf<ReportCategoryModel.Data>()

    private val bsBinding by lazy {
        BottomsheetManageCategoryBinding.bind(
            LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.bottomsheet_manage_category,
                    requireActivity().findViewById(R.id.bottom_sheet_container)
                )
        )
    }

    val bottomSheetDialog by lazy {
        BottomSheetDialog(
            requireActivity(),
            R.style.Theme_MaterialComponents_BottomSheetDialog
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        vbind = FragmentCategoryManageBinding.bind(
            inflater.inflate(
                R.layout.fragment_category_manage,
                container,
                false
            )
        )
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Manage Kategori Laporan"
        menuViewModel.title.value = "Manage Kategori"

        vbind.rvCategory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterCategory
        }

        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing = false
            viewmodel.getCategory()
        }

        vbind.btnAdd.setOnClickListener {
            bottomSheetDialog.dismiss()
            Navigation.findNavController(vbind.root)
                .navigate(
                    R.id.action_nav_category_to_editCategory,
                    bundleOf(
                        CATEGORY_ID to "add"
                    )
                )
        }

        viewmodel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)

        bottomSheetDialog.setContentView(bsBinding.root)

        adapterCategory.setInterface(object : CategoryAdminAdapter.MyCategoryInterface {
            override fun onclick(
                model: ReportCategoryModel.Data,
                adaptVbind: ItemCategoryForAdminBinding
            ) {
                bottomSheetDialog.show()
                bsBinding.tvName.text = model.category_name

                bsBinding.btnEdit.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    Navigation.findNavController(vbind.root)
                        .navigate(
                            R.id.action_nav_category_to_editCategory,
                            bundleOf(
                                CATEGORY_ID to model.id.toString(),
                                CATEGORY_PHOTO_PATH to model.photo_path,
                                CATEGORY_NAME to model.category_name
                            )
                        )
                }

                bsBinding.btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Peringatan")
                        .setMessage("Anda Yakin Ingin Menghapus Kategori Ini ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Hapus") { dialog, wb ->
                            viewmodel.deleteCategory(model.id.toString())
                            dialog.dismiss()
                            bottomSheetDialog.dismiss()
                        }
                        .setNegativeButton("Batal") { dialog, which ->
                            // Do Nothing ??
                        }.show()
                }

                bsBinding.ivClose.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
            }

        })

        viewmodel.getCategory()
        observeCategory()
        observeDelete()

    }

    private fun observeDelete() {
        viewmodel.deleteCategoryLiveData.observe(viewLifecycleOwner, observerDelete)
    }

    val observerDelete = Observer<Resource<String>> {
        when (it) {
            is Resource.Success -> {
                tempCategory.clear()
                viewmodel.getCategory()
                showSweetAlert("Berhasil", "Berhasil Menghapus Kategori", R.color.xdGreen)
                viewmodel.deleteCategoryLiveData.value = null
                vbind.includeLoading.loadingRoot.setGone()
            }
            is Resource.Loading -> {
                vbind.includeLoading.loadingRoot.setVisible()
            }
            is Resource.Error -> {
                viewmodel.deleteCategoryLiveData.value = null
                showSweetAlert("Gagal", it.message.toString(), R.color.xdRed)
                vbind.includeLoading.loadingRoot.setGone()
            }
        }
    }

    private fun observeCategory() {
        viewmodel.categoryLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    tempCategory.clear()
                    vbind.includeLoading.loadingRoot.setGone()
                    it.data?.let { it1 ->
                        tempCategory.addAll(it1.data)
                        adapterCategory.setData(tempCategory)
                        adapterCategory.notifyDataSetChanged()
                    }
                }
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                    it.data?.let { it1 -> tempCategory.addAll(it1.data) }
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.setGone()
                    it.data?.let { it1 -> tempCategory.addAll(it1.data) }
                }
            }
        })
    }


}