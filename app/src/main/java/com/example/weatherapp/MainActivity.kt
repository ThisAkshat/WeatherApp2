package com.example.weatherapp

import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//56126f7bc4b329b6d88adc517fb7ecd5
class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /* enableEdgeToEdge()*/
        setContentView(binding.root)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets */

        fetchWeatherData("jaipur");
        SearchCity()
        }
    private fun SearchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
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
    // val searchView = binding.searchView




    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"56126f7bc4b329b6d88adc517fb7ecd5","metric")

        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min



                    binding.temp.text="$temperature °C"
                    binding.Weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.Humidity.text = "$humidity %"
                    binding.windSpeed.text="$windSpeed m/s"
                    binding.sunRise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.Sea.text="$seaLevel hpa"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"

                    changeImagesAccordingToWeathercondition(condition)


                //Log.d("TAG", "onResponse: $temperature")
                }
                else{
                    // Handle unsuccessful response (e.g., city not found)
                    binding.cityName.text = "City not found"
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })
        
    }

    private fun changeImagesAccordingToWeathercondition(conditions: String) {
        when(conditions)
        {
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunnybk)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "light Rain","Rain","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.rainn)
                binding.lottieAnimationView.setAnimation(R.raw.rainy)
            }

            "light Snow","Moderate Snow","Heavy Snow","Blizzard","Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            "Haze","partly Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.haze)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Thunderstorm"->{
                binding.root.setBackgroundResource(R.drawable.thunde)
                binding.lottieAnimationView.setAnimation(R.raw.thunder)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunnybk)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}




/* } */