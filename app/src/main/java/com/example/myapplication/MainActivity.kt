package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Country {
    @SerializedName("name")
    @Expose
    var name:String? = null

    @SerializedName("capital")
    @Expose
    var capital:String? = null

    @SerializedName("region")
    @Expose
    var region: String? = null
}

interface Api {
    @GET("all")
    fun all():Call<List<Country>>
}

class ListAdapter(context: Context, resource:Int, array: List<Country>):
    ArrayAdapter<Country>(context,resource,array) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val country = getItem(position)

        if (country == null) {
            throw Exception()
        }

        val view = if (convertView!=null) {
            convertView
        } else {
            LayoutInflater.from(context).inflate(R.layout.country_item,null)
        }

        val nameView = view.findViewById<TextView>(R.id.name)
        val capitalView = view.findViewById<TextView>(R.id.capital)
        val region = view.findViewById<TextView>(R.id.region)

        nameView.text = country.name
        capitalView.text = country.capital
        region.text = country.region
        return view
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.eu/rest/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val list = mainList

        val api = retrofit.create(Api::class.java)

        Thread(Runnable {
            api.all().enqueue(object : Callback<List<Country>> {
                override fun onFailure(call: Call<List<Country>>, t: Throwable) {

                }

                override fun onResponse(call: Call<List<Country>>,
                                        response: Response<List<Country>>) {
                    list.post {
                        list.adapter = ListAdapter(
                            this@MainActivity,
                            R.layout.country_item,
                            response.body()!!)
                    }
                }
            })
        }).start()
    }
}
