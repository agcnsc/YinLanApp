package com.kg.inline.ui.fileextract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kg.inline.R
import com.kg.inline.ui.selectfolder.SelectFolderActivity
import kotlinx.android.synthetic.main.fragment_file_extract.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FileExtractFragment : Fragment() {
    companion object {
        const val requestSelectFolderCode = 1234
        fun newInstance() = FileExtractFragment()
    }

    private lateinit var viewModel: FileExtractViewModel
    private var isSelectTargetFolder = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_extract, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FileExtractViewModel::class.java)

        setupView()
    }

    private fun setupView() {
        select_target_folder_button.setOnClickListener {
            isSelectTargetFolder = true
            startSelectFolder()
        }

        select_save_folder_button.setOnClickListener {
            isSelectTargetFolder = false
            startSelectFolder()
        }

        extract_button.setOnClickListener {
            startFileExtract()
        }

        extract_button.isEnabled = false
    }


    private fun startSelectFolder() {
        context.let { c ->
            val intent = Intent(c, SelectFolderActivity::class.java)
            this.startActivityForResult(intent, requestSelectFolderCode)
        }
    }

    private fun startFileExtract() {
        enableButtons(false)
        val target = target_folder_path.text.toString()
        val save = save_folder_path.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val task = GlobalScope.async {
                viewModel.extract(target, save)
            }

            task.await()
            GlobalScope.launch(Dispatchers.Main) {
                enableButtons(true)
            }
        }
    }

    private fun enableButtons(isEnable: Boolean) {
        extract_button.isEnabled = isEnable
        select_save_folder_button.isEnabled = isEnable
        select_target_folder_button.isEnabled = isEnable
        progress.visibility = if(isEnable) View.GONE else View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestSelectFolderCode && resultCode == Activity.RESULT_OK) {
            if (isSelectTargetFolder) {
                target_folder_path.text = data?.getStringExtra("folder")
            } else {
                save_folder_path.text = data?.getStringExtra("folder")
            }

            extract_button.isEnabled = !target_folder_path.text.isNullOrEmpty() && !save_folder_path.text.isNullOrEmpty()
        }

    }
}