package com.todolist

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoAdapter (private val todos: MutableList<Todo>
    ) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private lateinit var dbHelper: DbHelper
    private lateinit var context: Context

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        context = parent.context
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_todo,
                parent,
                false
            )
        )
    }

    fun addTodo(todo: Todo) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }

    fun deleteTodos() {
        todos.removeAll { todo -> todo.checked }
        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough (item: TextView, isChecked: Boolean) {
        if(isChecked) {
            item.paintFlags = item.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            item.paintFlags = item.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currTodo = todos[position]
        dbHelper = DbHelper(context,"UserData", null, 1 )
        holder.itemView.apply {
            tvTodoItem.text = currTodo.item
            cbTodoCheck.isChecked = currTodo.checked
            toggleStrikeThrough(tvTodoItem, cbTodoCheck.isChecked)
            cbTodoCheck.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tvTodoItem, isChecked)
                currTodo.checked = !currTodo.checked
                dbHelper.updateData(currTodo.item, currTodo.checked)
            }

        }
    }

    override fun getItemCount(): Int {
      return todos.size
    }
}