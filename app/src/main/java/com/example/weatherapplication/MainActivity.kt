package com.example.weatherapplication

import APIInterface
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//Api key c7381eda00f942e10441ba1db8bcb5e9
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)  //learn
        setContentView(binding.root)

        fetchWeatherData("Jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city:String) {
        val retrofit=Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)

        val retrofitData=retrofit.getWeatherData(city,"c7381eda00f942e10441ba1db8bcb5e9","metric")  //fun that is created in interface
        retrofitData.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val maxTemp=responseBody.main.temp_max.toString()
                        val minTemp=responseBody.main.temp_min.toString()
                        val humidity=responseBody.main.humidity.toString()
                        val seaLvl=responseBody.main.sea_level
                        val windSpeed=responseBody.wind.speed
                        val sunrise=responseBody.sys.sunrise.toLong()
                        val sunset=responseBody.sys.sunset.toLong()
                        val condition=responseBody.weather.firstOrNull()?.main?:"Unknown"  //null check

                        //  Setting the variables
                        binding.tVCondition.text="$condition"
                        binding.maxTemp.text="Max Temp: $maxTemp °C"
                        binding.minTemp.text="Min Temp: $minTemp °C"
                        binding.humidity.text="$humidity%"
                        binding.sunset.text="${currentTime(sunset)}"
                        binding.sunrise.text="${currentTime(sunrise)}"
                        binding.seaLevel.text="$seaLvl hPa"
                        binding.windSpeed.text="$windSpeed m/s"
                        binding.currentTemp.text = "$temperature °C"
                        binding.condition.text="$condition"
                        binding.currentDay.text=currentDay(System.currentTimeMillis())
                        binding.currentLocation.text="$city"
                        binding.currentDate.text=currentDate(System.currentTimeMillis())

                        //Log.d("xyz", "onResponse: $temperature")
                        
                        
                        //Changing images
                        changeImageAccToCondition(condition)
                    } else {
                        Log.d("xyz", "Response body is null")
                    }
                } else {
                    Log.d("xyz", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.d("xyz", "Error: from on failure",t)
            }


        })
    }

    private fun changeImageAccToCondition(condition: String) {
        val background=binding.main
        val lottie=binding.lottieAnimationView3
        if(condition=="Mist"){
            background.setBackgroundResource(R.drawable.colud_background)
            lottie.setAnimation(R.raw.cloud)

        }
        else if(condition=="Clouds"){
            background.setBackgroundResource(R.drawable.colud_background)
            lottie.setAnimation(R.raw.cloud)
        }
        else if(condition=="Rain"){
            background.setBackgroundResource(R.drawable.rain_background)
            lottie.setAnimation(R.raw.rain)
        }
        else if(condition=="Snow"){
            background.setBackgroundResource(R.drawable.snow_background)
            lottie.setAnimation(R.raw.snow)
        }
        else{
            background.setBackgroundResource(R.drawable.sunny_background)
            lottie.setAnimation(R.raw.sun)
        }
        lottie.playAnimation()
    }

    //to get current day
    fun currentDay(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format((Date()))
    }
    //to get current date
    fun currentDate(timestamp:Long):String{
        val sdf=SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format((Date()))
    }
    //to get current time
    fun currentTime(timestamp:Long):String{
        val sdf=SimpleDateFormat("HH mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}