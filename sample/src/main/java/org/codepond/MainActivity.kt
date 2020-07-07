package org.codepond

import android.app.Activity
import android.content.ContentProvider
import android.os.Bundle
import android.util.Log
import org.codepond.roomprovider.*
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
                    RedditDb.db.users().insert(RedditUser(0,
                            "lhc$i", "123456",
                            remember = true,
                            autoLogin = true,
                            createDate = date,
                            updateDate = date
                    ))
                }
            }
            if(cursor.moveToFirst()){
                Log.d(TAG, cursor.format())
                while (cursor.moveToNext()){
                    Log.d(TAG, cursor.format())
                }
            }
            cursor.close()

//            val cursor =
//                contentResolver.query(UserContentProvider.URI_USER, null, null, null, null, null)
//            if(cursor!=null){
//                if(cursor.moveToFirst()){
//                    Log.d(TAG, cursor.format())
//                    while (cursor.moveToNext()){
//                        Log.d(TAG, cursor.format())
//                    }
//                }
//            }

        }).start()

    }
}