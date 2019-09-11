package com.tushar.bitkoin.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tushar.bitkoin.R
import com.tushar.bitkoin.base.BaseActivity
import com.tushar.module.presentation.GraphViewModel
import javax.inject.Inject

class GraphActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var graphViewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        graphViewModel = ViewModelProviders.of(this, viewModelFactory).get(GraphViewModel::class.java)
        graphViewModel.onResponse()
    }
}
