package com.kg.inline.ui.fileexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kg.inline.R

class FileExplorerFragment : Fragment() {

    companion object {
        fun newInstance() = FileExplorerFragment()
    }

    private lateinit var viewModel: FileExplorerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_file_explorer, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FileExplorerViewModel::class.java)


    }

}