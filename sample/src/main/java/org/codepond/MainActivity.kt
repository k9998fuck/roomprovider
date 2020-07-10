package org.codepond

import android.app.Activity
import android.content.ContentUris
import android.os.Bundle
import android.util.Log
import org.codepond.roomprovider.*
import org.codepond.roomprovider.bean.RedditUser
import org.codepond.roomprovider.bean.toRedditUser
import org.codepond.roomprovider.db.RedditContract
import org.codepond.roomprovider.db.RedditDb
import java.util.*

class MainActivity : Activity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Thread(Runnable {
            var cursor = RedditDb.db.users().queryAll()
            if(cursor.count==0){
                val date = Date()
                for(i in 1..1000){
                    RedditDb.db.users().insert(
                            RedditUser(0,
                                    "lhc$i", "123456",
                                    remember = true,
                                    autoLogin = true,
                                    createDate = date,
                                    updateDate = date
                            ))
                }
            }
//            if(cursor.moveToFirst()){
//                Log.d(TAG, cursor.format())
//                while (cursor.moveToNext()){
//                    Log.d(TAG, cursor.format())
//                }
//            }
            cursor.close()


            queryFromContentProvider2()


        }).start()

    }


    private fun queryFromContentProvider(){
        ContentUris.withAppendedId(RedditContract.Users.CONTENT_URI,1)
        val cursor = contentResolver.query(RedditContract.Users.CONTENT_URI,
                arrayOf(RedditContract.Users._ID,
                        RedditContract.Users.USERNAME,
                        RedditContract.Users.PASSWORD),
                "${RedditContract.Users.USERNAME} LIKE ? AND ${RedditContract.Users.PASSWORD} = ?",
                arrayOf("lhc1%","123456"),
                "${RedditContract.Users._ID} DESC", null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                Log.d(TAG, "testFromContentProvider ${cursor.format()}")
                while (cursor.moveToNext()){
                    Log.d(TAG, "testFromContentProvider ${cursor.format()}")
                }
            }
        }
    }

    private fun queryFromContentProvider2(){
        ContentUris.withAppendedId(RedditContract.Users.CONTENT_URI,1)
        val cursor = contentResolver.query(RedditContract.Users.CONTENT_URI,
                null,
                "${RedditContract.Users.USERNAME} LIKE ? AND ${RedditContract.Users.PASSWORD} = ?",
                arrayOf("lhc1%","123456"),
                "${RedditContract.Users._ID} DESC", null)
        if(cursor!=null){
            if(cursor.moveToFirst()){
                Log.d(TAG, "testFromContentProvider ${cursor.toRedditUser()}")
                while (cursor.moveToNext()){
                    Log.d(TAG, "testFromContentProvider ${cursor.toRedditUser()}")
                }
            }
        }
    }

}