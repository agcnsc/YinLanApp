package com.kg.inline.ui.selectfolder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kg.inline.R
import com.kg.module_file_explorer.MainFragment

class SelectFolderActivity : AppCompatActivity(), MainFragment.Companion.FileExplorerDelegate {
    private val fragment = MainFragment.newInstance(MainFragment.Companion.Mode.SelectFolder)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_folder)
        fragment.delegate = this
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    override fun onClickDone(path: String) {
        val intent = Intent()
        intent.putExtra("folder", path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}

