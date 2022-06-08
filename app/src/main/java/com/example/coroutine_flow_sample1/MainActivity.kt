package com.example.coroutine_flow_sample1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val booksRemoteDataSource = BooksRemoteDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runBlocking {
            booksRemoteDataSource.latestBooks
                .collect { news ->
                    Log.d("Tatsuya🐲", "onCreate: $news")
                }
        }

    }
}

data class Book(
    val title: String,
)

interface BookApi {
    suspend fun fetchLatestBooks(): List<Book>
}

class BookApiApiImpl : BookApi {
    private var count: Int = 0
    override suspend fun fetchLatestBooks(): List<Book> {
        count++
        return listOf(Book("タイトル" + "$count"))
    }
}

class BooksRemoteDataSource(
    private val bookApi: BookApi = BookApiApiImpl(),
    private val refreshIntervalMs: Long = 1000L
) {
    val latestBooks: Flow<List<Book>> = flow {
        while (true) {
            val latestBooks = bookApi.fetchLatestBooks()
            emit(latestBooks)
            delay(refreshIntervalMs)
        }
    }
}