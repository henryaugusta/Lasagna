package com.bangkit.nadira.viewmodel

import org.junit.Before
import org.junit.Test

class UserReportViewModelTest {


    lateinit var viewModelUserReportViewModel: UserReportViewModel

    @Before
    fun set() {
        viewModelUserReportViewModel = UserReportViewModel()
    }

    @Test
    fun getData() {
        val xx = viewModelUserReportViewModel.getReportByUser("1")
    }

}