package com.rezwan_cs.androidassignment
import android.app.Dialog
import android.content.ContentValues
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
class MainActivity:AppCompatActivity() {
    internal lateinit var mPlaceSearch:EditText
    internal lateinit var mRecylerView:RecyclerView
    internal lateinit var placeAdapter:PlaceAdapter
    internal lateinit var linearLayoutManager:LinearLayoutManager
    internal lateinit var dialog:Dialog
    internal lateinit var editor:SharedPreferences.Editor
    internal lateinit var sharedPreferences:SharedPreferences
    internal var placeUrl = "https://www.noforeignland.com/home/api/v1/places"
    //Database variable
    internal lateinit var mPlaceDatabase:SQLiteDatabase
    internal lateinit var mDbHelper:DBOpenHelper
    private// +" WHERE "+DBOpenHelper.COL_SUBSCRIPTION+" = ?" + new String[]{String.valueOf(1)};
    val placeListFromDB:ArrayList<Place>
        get() {
            val arrayList = ArrayList<Place>()
            mPlaceDatabase = mDbHelper.getWritableDatabase()
            val sql = "SELECT * FROM " + DBOpenHelper.PLACE_TABLE_NAME
            val cursor = mPlaceDatabase.rawQuery(sql,
                null)
            if (cursor.moveToFirst())
            {
                do
                {
                    val idx = cursor.getColumnIndex(DBOpenHelper.COL_ID_NUMBER)
                    val name_idx = cursor.getColumnIndex(DBOpenHelper.COL_NAME)
                    val latitude_idx = cursor.getColumnIndex(DBOpenHelper.COL_LATITUDE)
                    val longitude_idx = cursor.getColumnIndex(DBOpenHelper.COL_LONGITUDE)
                    arrayList.add(Place(
                        cursor.getString(name_idx), cursor.getString(idx),
                        cursor.getDouble(latitude_idx), cursor.getDouble(longitude_idx)
                    ))
                }
                while (cursor.moveToNext())
            }
            mPlaceDatabase.close()
            cursor.close()
            return arrayList
        }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPlaceSearch = findViewById(R.id.et_screen1_search_place)
        mRecylerView = findViewById(R.id.recylerview_placelist)
        sharedPreferences = getSharedPreferences("ASSIGNMENT", MODE_PRIVATE)
        mDbHelper = DBOpenHelper(this)
        editor = sharedPreferences.edit()
        dialog = Dialog(this@MainActivity)
        if (sharedPreferences.getBoolean("first_time", true))
        {
            editor.putBoolean("first_time", false)
            editor.commit()
            JsonTask().execute(placeUrl)
        }
        else
        {
            placeAdapter = PlaceAdapter(this@MainActivity, placeListFromDB)
            mRecylerView.setAdapter(placeAdapter)
            linearLayoutManager = LinearLayoutManager(this@MainActivity)
            mRecylerView.setLayoutManager(linearLayoutManager)
        }
        mPlaceSearch.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
            }
            override fun afterTextChanged(s:Editable) {
                placeAdapter.filter(s.toString())
            }
        })
        mPlaceSearch.removeTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {
            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
            }
            override fun afterTextChanged(s:Editable) {
                placeAdapter.filter(s.toString())
            }
        })
    }
    inner class JsonTask:AsyncTask<String, Void, ArrayList<Place>>() {

        override fun doInBackground(vararg params:String):ArrayList<Place> {
            //var connection:HttpURLConnection = HttpURLConnection()
            val connection = URL(params[0]).openConnection() as HttpURLConnection
            var reader:BufferedReader
            var placeArrayList = ArrayList<Place>()
            val any = try {
                var url = URL(params[0])
                //connection = url.openConnection() as HttpURLConnection
                Log.d("TAG Json:", "before auth")
                //Log.d("TAG Json:", "just before connection");
                connection.connect()
                //Log.d("TAG Json:", "after connection");
                var stream = connection.getInputStream()
                reader = BufferedReader(InputStreamReader(stream))
                var builder = StringBuilder()
                var line:String?
                do {

                    line = reader.readLine()
                    if (line == null)
                        break

                    builder.append(line)

                } while (true)

                val finalJson = builder.toString()
                val parentObject = JSONObject(finalJson)
                val parentArray = parentObject.getJSONArray("features")
                // to log the response code of your request
                Log.d("TAG1", "MyHttpRequestTask doInBackground : " + connection.getResponseCode())
                // to log the response message from your server after you have tried the request.
                Log.d(
                    "TAG2",
                    "MyHttpRequestTask doInBackground : " + connection.getResponseMessage()
                )
                // looping through All Contacts
                for (i in 0 until parentArray.length()) {
                    var singleObject = parentArray.getJSONObject(i)
                    var propertiesObject = singleObject.getJSONObject("properties")
                    var geometryObject = singleObject.getJSONObject("geometry")
                    var name = propertiesObject.getString("name")
                    //var id = String.valueOf(propertiesObject.getLong("id"))
                    var id = propertiesObject.getLong("id").toBigDecimal().toPlainString()
                    //Log.d("VAL: ", name + " "+id);
                    var coordinate = geometryObject.getJSONArray("coordinates")
                    var latitude = java.lang.Double.valueOf(coordinate.get(0).toString())
                    var longitude = java.lang.Double.valueOf(coordinate.get(1).toString())
                    //Log.d("LAV: ", i+" " +latitude+ " "+longitude);
                    placeArrayList.add(Place(name, id, latitude, longitude))
                }
            } catch (e: MalformedURLException) {
                Log.d("ERROR1", e.message)
            } catch (e: IOException) {
                Log.d("ERROR2", e.message)
            } catch (e: JSONException) {
                Log.d("ERROR3", e.message)
            } finally {
                Log.d("ERROR4", "Finally")
                connection.disconnect()
            }
            return placeArrayList
        }
        protected override fun onPostExecute(result:ArrayList<Place>) {
            super.onPostExecute(result)
            //TODO: store the data to SQLite database
            Log.d("RESULT: ", result.get(0).name)
            placeAdapter = PlaceAdapter(this@MainActivity, result)
            mRecylerView.setAdapter(placeAdapter)
            linearLayoutManager = LinearLayoutManager(this@MainActivity)
            mRecylerView.setLayoutManager(linearLayoutManager)
            //mRecylerView.scrollToPosition(placeAdapter.getItemCount()-1);
            savePlaceListToDB(result)
        }
    }
    private fun savePlaceListToDB(arrayList:ArrayList<Place>) {
        mPlaceDatabase = mDbHelper.getWritableDatabase()
        for (i in arrayList.indices)
        {
            var contentValues = ContentValues()
            contentValues.put(DBOpenHelper.COL_ID_NUMBER, arrayList.get(i).id)
            contentValues.put(DBOpenHelper.COL_NAME, arrayList.get(i).name)
            contentValues.put(DBOpenHelper.COL_LATITUDE, arrayList.get(i).latitude)
            contentValues.put(DBOpenHelper.COL_LONGITUDE, arrayList.get(i).longitude)
            mPlaceDatabase.insert(DBOpenHelper.PLACE_TABLE_NAME, null, contentValues)
        }
        mPlaceDatabase.close() // close the db after writing on it
    }
}

