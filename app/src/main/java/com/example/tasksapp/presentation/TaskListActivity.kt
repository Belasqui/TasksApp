package com.example.tasksapp.presentation

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tasksapp.R
import com.example.tasksapp.TaskAppAplication
import com.example.tasksapp.data.AppDataBase
import com.example.tasksapp.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {


    private lateinit var imgContent: ImageView

    private val adapter = TaskListAdapter(::onListItemClicked)

    private val viewModel: TaskListViewModel by lazy {
        TaskListViewModel.create(application)
    }

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction

            viewModel.execute(taskAction)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        imgContent = findViewById(R.id.ctn_content)


        val rvTask: RecyclerView = findViewById(R.id.rv_task_list)
        rvTask.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)
        }
    }

    override fun onStart() {
        super.onStart()

        listFromDatabase()

    }

    private fun deleteAll() {
        val taskAction = TaskAction(null, ActionType.DELETE_ALL.name)
        viewModel.execute(taskAction)

    }


    private fun listFromDatabase() {
        //observer
        val listObserver = Observer<List<Task>> { listTasks ->
            if(listTasks.isEmpty()) {
                imgContent.visibility = View.VISIBLE
            }else{
                imgContent.visibility = View.GONE
            }
            adapter.submitList(listTasks)
        }

        //livedata
        viewModel.taskListLiveData.observe(this@MainActivity, listObserver)

    }


    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task? = null) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_task -> {
                deleteAll()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }
}

enum class ActionType {
    DELETE,
    DELETE_ALL,
    CREATE,
    UPDATE
}

data class TaskAction(
    val task: Task?,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"
