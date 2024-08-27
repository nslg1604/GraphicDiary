package org.diary.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.diary.R
import org.diary.common.MyApplication
import org.diary.common.MyCommon
import org.diary.databinding.ItemHomeBinding
import org.diary.model.Book
import org.diary.utils.MyLogger
import org.diary.viewmodel.BookViewModel

class HomeAdapter constructor(
    bookList: MutableList<Book>?
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private lateinit var books: MutableList<Book>
    private var myApplication = MyApplication.instance

    init {
        MyLogger.d("HomeAdapter - init size=" + bookList?.size)
        this.books = bookList!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val binding: ItemHomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_home, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return if (::books.isInitialized) books.size else 0
    }

    class ViewHolder(private val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        val bookViewModel = BookViewModel(MyApplication.instance?.homeFragment!!)

        fun bind(book: Book) {
            bookViewModel.bind(book)
            binding.xmlItemViewModel = bookViewModel

            if (book.level == Book.LEVEL_CHILD){
//                binding.itemHomeAddLayout.visibility = View.INVISIBLE
//                binding.itemHomeAddText.visibility = View.INVISIBLE
            }
        }
    }

    fun update() {
        MyLogger.d("HomeAdapter - update")
        this!!.notifyDataSetChanged()
    }

    fun updateAll() {
        MyLogger.d("HomeAdapter - updateAll")
        this.books = myApplication?.myDatabase?.prepareAll()!!
        if (books.size >= MyCommon.SIZE_ADD_EMPTY){
            books.add(Book("", 0, "", "", "", 0, 0f, 0f))
        }
        this!!.notifyDataSetChanged()
    }
}