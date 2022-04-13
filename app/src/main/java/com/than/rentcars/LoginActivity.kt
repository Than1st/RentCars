package com.than.rentcars

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.than.rentcars.databinding.ActivityLoginBinding
import com.than.rentcars.model.LoginRequest
import com.than.rentcars.model.PostLoginResponse
import com.than.rentcars.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    companion object{
        const val FILENAME = "kotlinsharedreferences"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = this.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "default_id")
        if (id != "default_id"){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.toString().isNotEmpty() && binding.etPassword.toString().isNotEmpty()){
                binding.pbLoading.visibility = View.VISIBLE
                actionLogin(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            } else {
                Toast.makeText(this, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actionLogin(email: String, pass: String) {
        val sharedPreferences = this.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
        val request = LoginRequest(email, pass)
        ApiClient.instance.postLogin(request).enqueue(object : Callback<PostLoginResponse> {
            override fun onResponse(
                call: Call<PostLoginResponse>,
                response: Response<PostLoginResponse>
            ) {
                val body = response.body()
                when (response.code()) {
                    201 -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        if (body != null) {
                            editor.putString("id", body.id.toString())
                            editor.putString("email", body.email)
                            editor.putString("role", body.role)
                            editor.apply()
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    400 -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Password Salah!", Toast.LENGTH_SHORT).show()
                    }
                    404 -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Akun belum terdaftar!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.pbLoading.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Login Gagal!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PostLoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}