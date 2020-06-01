package com.rezwan_cs.androidassignment
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
class Screen3:AppCompatActivity() {
    internal var url = "https://www.noforeignland.com/home/api/v1/place?id="
    internal lateinit var mPlaceName:TextView
    internal lateinit var mComments:TextView
    lateinit internal var mBanner:ImageView
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen3)
        mPlaceName = findViewById(R.id.tv_screen3_place_name)
        mComments = findViewById(R.id.tv_screen3_comment)
        mBanner = findViewById(R.id.iv_screen3_banner)
        if (getIntent().getStringExtra("PlaceId") != null)
        {
            url += getIntent().getStringExtra("PlaceId")
            Log.d("ID: ", getIntent().getStringExtra("PlaceId"))
            //url+= "4776700298657792";
            JsonTask().execute(url)
        }
        else
        {
            Toast.makeText(this@Screen3, "Sorry ! Place description retrivation failed", Toast.LENGTH_LONG).show()
        }
    }
    inner class JsonTask:AsyncTask<String, Void, PlaceDescription>() {
        override fun doInBackground(vararg params:String):PlaceDescription {
            val connection = URL(params[0]).openConnection() as HttpURLConnection
            val reader:BufferedReader
            lateinit var placeDescription:PlaceDescription
            try
            {
                //val url = URL(params[0])
                //connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val stream = connection.getInputStream()
                reader = BufferedReader(InputStreamReader(stream))
                val builder = StringBuilder()
                var line:String?
                do {
                    line = reader.readLine()
                    if (line == null)
                        break
                    builder.append(line)
                } while (true)

                val finalJson = builder.toString()
                val parentObject = JSONObject(finalJson).getJSONObject("place")
                val name = parentObject.getString("name")
                val banner = parentObject.getString("banner")
                val comment = parentObject.getString("comments")
                placeDescription = PlaceDescription(name, banner, comment)
            }
            catch (e:MalformedURLException) {
                Log.d("ERROR1", e.message)
            }
            catch (e:IOException) {
                Log.d("ERROR2", e.message)
            }
            catch (e:JSONException) {
                Log.d("ERROR3", e.message)
            }
            finally
            {
                Log.d("ERROR4", "Finally")
                if (connection != null)
                {
                    connection.disconnect()
                }

            }
            return placeDescription
        }
        override fun onPostExecute(result:PlaceDescription) {
            super.onPostExecute(result)
            mPlaceName.setText(result.name)
            mComments.setText(result.comment)
            if (!result.banner.isEmpty())
            {
                Glide.with(this@Screen3)
                    .load(result.banner)
                    .into(mBanner)
            }
        }
    }
}