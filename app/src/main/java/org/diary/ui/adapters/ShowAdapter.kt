package org.diary.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.diary.R
import org.diary.common.MyApplication
import org.diary.databinding.ItemShowBinding
import org.diary.model.Book
import org.diary.model.Note
import org.diary.utils.MyLogger
import org.diary.viewmodel.ShowItemViewModel

class ShowAdapter constructor(
    book: Book?,
//    notesList: MutableList<Note>
) : RecyclerView.Adapter<ShowAdapter.ViewHolder>() {

    private var book = book
    private lateinit var notes: MutableList<Note>
    private var myApplication = MyApplication.instance

    init {
        MyLogger.d("ShowAdapter - init book=" + book?.name)
//        if (myApplication?.notes == null) {
            this.notes = myApplication?.myDatabase?.readNotes(book!!)!!
            myApplication?.notes = notes
//        }
//        else {
//            this.notes = myApplication?.notes!!
//        }
        MyLogger.d("ShowAdapter - init notes=" + notes?.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowAdapter.ViewHolder {
        MyLogger.d("ShowAdapter - onCreateViewHolder")
        val binding: ItemShowBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_show, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowAdapter.ViewHolder, position: Int) {
        MyLogger.d("ShowAdapter - onBindViewHolder position=" + position)
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        return if (::notes.isInitialized) notes.size else 0
    }

    class ViewHolder(private val binding: ItemShowBinding) : RecyclerView.ViewHolder(binding.root) {
        private val viewModel = ShowItemViewModel(MyApplication.instance?.showFragment!!)

        fun bind(note: Note) {
            MyLogger.d("ShowAdapter - ViewHolder - bind note=" + note.day)
            viewModel.bind(note)
            binding.xmlShowItemViewModel = viewModel
        }
    }

    fun update() {
        MyLogger.d("ShowAdapter - update notes.size=" + notes?.size)
        if (myApplication?.notes!! != null) {
            this.notes = myApplication?.notes!!
            this!!.notifyDataSetChanged()
        }
    }
}