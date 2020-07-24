package org.codepond.client

import android.content.ContentUris
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.lib.Book
import com.lib.format
//import org.codepond.roomprovider.bean.RedditPost
//import org.codepond.roomprovider.bean.RedditUser
//import org.codepond.roomprovider.converter.Converters
//import org.codepond.roomprovider.db.RedditContract
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable {
//            queryFromContentProvider2()
//            Log.d(TAG, "Converters().dateToTimestamp(Date()) 1")
//            Log.d(TAG, "Converters().dateToTimestamp(Date()) ${Converters().dateToTimestamp(Date())}")
//            Log.d(TAG, "Converters().dateToTimestamp(Date()) 2")
            Log.d(TAG, "Book(\"lhc\").format() = ${Book("lhc").format()}")

        }).start()
    }

//    private fun queryFromContentProvider(){
//        ContentUris.withAppendedId(RedditContract.Users.CONTENT_URI,1)
//        val cursor = contentResolver.query(RedditContract.Users.CONTENT_URI,
//                arrayOf(RedditContract.Users._ID,
//                        RedditContract.Users.USERNAME,
//                        RedditContract.Users.PASSWORD),
//                "${RedditContract.Users.USERNAME} LIKE ? AND ${RedditContract.Users.PASSWORD} = ?",
//                arrayOf("lhc1%","123456"),
//                "${RedditContract.Users._ID} DESC", null)
//        if(cursor!=null){
//            if(cursor.moveToFirst()){
//                Log.d(TAG, "testFromContentProvider ${cursor.format()}")
//                while (cursor.moveToNext()){
//                    Log.d(TAG, "testFromContentProvider ${cursor.format()}")
//                }
//            }
//        }
//    }
//
//    private fun queryFromContentProvider2(){
//        ContentUris.withAppendedId(RedditContract.Users.CONTENT_URI,1)
//        val cursor = contentResolver.query(RedditContract.Users.CONTENT_URI,
//                null,
//                "${RedditContract.Users.USERNAME} LIKE ? AND ${RedditContract.Users.PASSWORD} = ?",
//                arrayOf("lhc1%","123456"),
//                "${RedditContract.Users._ID} DESC", null)
//        if(cursor!=null){
//            if(cursor.moveToFirst()){
//                Log.d(TAG, "testFromContentProvider ${cursor.toRedditUser()}")
//                while (cursor.moveToNext()){
//                    Log.d(TAG, "testFromContentProvider ${cursor.toRedditUser()}")
//                }
//            }
//        }
//    }

}
