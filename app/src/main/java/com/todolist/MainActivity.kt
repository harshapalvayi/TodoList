package com.todolist

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var todoAdapter: TodoAdapter
    private lateinit var dbHelper: DbHelper
    private lateinit var listItems: Cursor
    private var checkInsertData: Boolean = false
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoAdapter = TodoAdapter(mutableListOf())

        rvTodoItems.adapter = todoAdapter
        rvTodoItems.layoutManager = LinearLayoutManager(this)

        dbHelper = DbHelper(this, "UserData", null, 1 )
        loadData()

        btnAddTodo.setOnClickListener {
            val todoItem = etTodoItem.text.toString()
            if(todoItem.isNotEmpty()) {
                val todo = Todo(todoItem, false)
                val checkIfExists = dbHelper.checkIfExists(todoItem)
                if (checkIfExists) {
                    Toast.makeText(this, "Item already exists", Toast.LENGTH_SHORT).show()
                } else {
                    checkInsertData = dbHelper.insertData(todo.item, false)
                    todoAdapter.addTodo(todo)
                    if (checkInsertData)
                        Toast.makeText(this, "New Entry Inserted", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this, "New Entry not Inserted", Toast.LENGTH_SHORT).show()
                }
                etTodoItem.text.clear()
            }
        }

        btnVoiceCmd.setOnClickListener {
            speak()
        }

        btnDeleteTodo.setOnClickListener {
           delete()
        }

    }

    private fun loadData() {
        listItems = dbHelper.getData()
        if (listItems.count == 0) {
            Toast.makeText(this, "No Entries to Display", Toast.LENGTH_SHORT).show()
            return
        }
        while (listItems.moveToNext()) {
            val text = listItems.getString(1)
            println(text)
            val listItem: Todo = Todo(text, false)
            todoAdapter.addTodo(listItem)
        }
    }
    private fun speak() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak something")
        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun delete() {
        listItems = dbHelper.getData()
        if (listItems.count == 0) {
            Toast.makeText(this, "No Entries to Display", Toast.LENGTH_SHORT).show()
            return
        }
        while (listItems.moveToNext()) {
            val id = listItems.getString(0)
            println(id)
            val text = listItems.getString(1)
            println(text)
            dbHelper.deleteData(text)
        }
        todoAdapter.deleteTodos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    var text = ""
                    if (result != null) {
                        for (res in result) text = """ $res """.trimIndent()
                    }
                    etTodoItem.setText(text)
                }
            }
        }
    }
}

