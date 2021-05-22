package com.feylabs.lasagna.viewmodel

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import timber.log.Timber

class CategoryViewModelTest {

    lateinit var categoryViewModel : CategoryViewModel
    @Before
    fun setUp() {
        categoryViewModel= CategoryViewModel()
    }

    @Test
    fun getCategory(){
        val e = categoryViewModel.getCategory()
        Timber.d("category : ->test ${e.toString()}" )
    }
}