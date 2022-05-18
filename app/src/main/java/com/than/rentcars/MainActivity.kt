package com.than.rentcars

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.than.rentcars.adapter.MainAdapter
import com.than.rentcars.databinding.ActivityMainBinding
import com.than.rentcars.model.GetAllCarResponseItem
import com.than.rentcars.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val sharedPreferences = this.getSharedPreferences(LoginActivity.FILENAME, Context.MODE_PRIVATE)
        binding.tvEmail.text = sharedPreferences.getString("email", "default_email")

        binding.tvLogout.setOnClickListener{
            AlertDialog.Builder(this).setPositiveButton("Logout"){ _, _ ->
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                Toast.makeText(this, "Anda Telah Logout!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }.setNegativeButton("Batal"){dialog,_->
                dialog.dismiss()
            }
                .setMessage("Anda ingin Logout?")
                .setTitle("Konfirmasi Logout")
                .create()
                .show()
        }

        fetchAllGetAllRentCars()
    }

    private fun fetchAllGetAllRentCars() {
        ApiClient.instance.getAllCar()
            .enqueue(object : Callback<List<GetAllCarResponseItem>> {
                override fun onResponse(
                    call: Call<List<GetAllCarResponseItem>>,
                    response: Response<List<GetAllCarResponseItem>>
                ) {
                    val body = response.body()
                    val code = response.code()
                    if (code == 200) {
                        if (body != null) {
                            showList(body)
                        }
                        binding.pbMain.visibility = View.GONE
                    } else {
                        binding.pbMain.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<List<GetAllCarResponseItem>>, t: Throwable) {
                    binding.pbMain.visibility = View.GONE
                }

            })
    }
    private fun showList(data: List<GetAllCarResponseItem>) {
        val adapter = MainAdapter(object : MainAdapter.OnClickListener {
            override fun onClickItem(data: GetAllCarResponseItem) {
                Toast.makeText(this@MainActivity, "${data.name} dipilih", Toast.LENGTH_SHORT).show()
            }
        })
        adapter.submitData(data)
        val layoutManager = LinearLayoutManager(this)
        binding.rvMain.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMain.addItemDecoration(itemDecoration)
        binding.rvMain.adapter = adapter
    }
}