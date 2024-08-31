package jp.ac.it_college.std.s23028.mykadai

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
import jp.ac.it_college.std.s23028.mykadai.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ボタンのクリックイベントを設定
        binding.buttonFetchCat.setOnClickListener {
            fetchCatImage()
        }
    }

    private fun fetchCatImage() {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // 接続タイムアウト
            .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // 読み込みタイムアウト
            .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)   // 書き込みタイムアウト
            .build()

        val request = Request.Builder()
            .url("https://api.thecatapi.com/v1/images/search") // 代替の猫画像API
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MainActivity", "Failed to fetch cat image", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let { responseBody ->
                    try {
                        val json = JSONArray(responseBody)
                        val imageUrl = json.getJSONObject(0).getString("url") // 画像URLを取得
                        runOnUiThread {
                            Picasso.get().load(imageUrl).into(binding.imageView)
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to parse JSON", e)
                    }
                }
            }
        })
    }
}
