package com.kg.module_file_explorer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.view.GestureDetectorCompat
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_navigation_title.*
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment(private val mode: Mode) : Fragment(), MainFragmentImpl {
    companion object {
        fun newInstance(mode: Mode) = MainFragment(mode)

        enum class Mode {
            Explorer,
            SelectFolder
        }

        interface FileExplorerDelegate {
            fun onClickDone(path: String)
        }
    }

    private val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 111

    private lateinit var viewModel: MainViewModel
    private var screenWidth: Int = 0
    var delegate: FileExplorerDelegate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view =  inflater.inflate(R.layout.main_fragment, container, false)
        val doneButton = view.findViewById<Button>(R.id.done)
        doneButton.visibility = if (mode == Mode.SelectFolder) View.VISIBLE else View.GONE
        doneButton.setOnClickListener {
            val path = viewModel.cursors.lastOrNull()?.currentPath
            if (path != null) {
                delegate?.onClickDone(path)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.mainFragmentDelegate = this

        initTitles()
        initList()

        val dm = resources.displayMetrics
        screenWidth = dm.widthPixels

        checkPermission()
    }

    private fun initTitles() {
        val lm = LinearLayoutManager(activity)
        lm.orientation = LinearLayoutManager.HORIZONTAL
        titles.layoutManager = lm
        titles.adapter = MainTitleAdapter(viewModel.cursors)
        titles.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            var mGestureDetector: GestureDetectorCompat

            init {
                mGestureDetector =
                    GestureDetectorCompat(titles.context, object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent?): Boolean {
                            if (e == null) return false

                            val childView = titles.findChildViewUnder(e.x, e.y)
                            if (childView != null) {
                                val position = titles.getChildAdapterPosition(childView)
                                onClickTitle(position)
                            }
                            return true
                        }

                    })
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                mGestureDetector.onTouchEvent(e)
                return false
            }
        })
    }

    private fun initList() {
        listview.layoutManager = LinearLayoutManager(context)
        listview.adapter = MainAdapter(viewModel.data)
        listview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                getPositionAndOffset(recyclerView)
            }

            private fun getPositionAndOffset(recyclerView: RecyclerView?) {
                val view = recyclerView?.layoutManager?.getChildAt(0)
                if (view != null) {
                    viewModel.updateIndex(view.top, recyclerView.layoutManager?.getPosition(view) ?: 0)
                }
            }
        })
        addItemClickListener()
    }

    fun update() {
        viewModel.updateList()
    }

    fun onBackPressed() {
        viewModel.onBackUp()
    }

    override fun dismissProgressDialog() {
        this.activity?.runOnUiThread {
            myprogress.visibility = View.GONE
            this.activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun showProgressDialog() {
        myprogress.visibility = View.VISIBLE
        this.activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun updateList() {
        listview.adapter?.notifyDataSetChanged()
    }

    override fun scrollTo(offset: Int, pos: Int) {
        val mag = listview.layoutManager as LinearLayoutManager
        mag.scrollToPositionWithOffset(pos, offset)
    }

    override fun finishActivity() {
        activity?.finish()
    }

    override fun updateTitle() {
        titles.post {
            titles.adapter?.notifyDataSetChanged()
        }
    }

    fun onClickTitle(position: Int) {
        viewModel.onClickTitleItem(position)
    }

    private fun addItemClickListener() {
        listview.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            var mGestureDetector: GestureDetectorCompat

            init {
                mGestureDetector =
                    GestureDetectorCompat(listview.context, object : GestureDetector.SimpleOnGestureListener() {

                        override fun onLongPress(e: MotionEvent?) {
                            //long press
                            if (e == null) return

                            //快速滚动的时候会触发long press
                            if (screenWidth - e.x <= 50) return

                            val childView = listview.findChildViewUnder(e.x, e.y)
                            if (childView != null) {
                                val position = listview.getChildAdapterPosition(childView)
                                viewModel.onLongClickItem(position)

                                Toast.makeText(listview.context, "long press:${position}", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }

                        override fun onSingleTapUp(e: MotionEvent?): Boolean {
                            if (e == null) return false

                            val childView = listview.findChildViewUnder(e.x, e.y)
                            if (childView != null) {
                                val position = listview.getChildAdapterPosition(childView)
                                viewModel.onClickItem(position)
                            }

                            return true
                        }

                    })
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                mGestureDetector.onTouchEvent(e)
                return false
            }

        })

    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity!!, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EXTERNAL_STORAGE)

        } else {
            Toast.makeText(activity!!, "授权成功！", Toast.LENGTH_SHORT).show();
            update()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            for (ret: Int in grantResults) {
                if (ret == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity!!, "权限申请成功", Toast.LENGTH_SHORT).show()
                    update()
                } else {
                    Toast.makeText(activity!!, "权限申请失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
