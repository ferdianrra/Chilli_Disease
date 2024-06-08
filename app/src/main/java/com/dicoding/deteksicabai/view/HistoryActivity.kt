package com.dicoding.deteksicabai.view

import Chi.R
import Chi.databinding.ActivityHistoryBinding
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.deteksicabai.MappingHelper
import com.dicoding.deteksicabai.adapter.HistoryAdapter
import com.dicoding.deteksicabai.db.HistoryHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        this.enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = HistoryAdapter(this)
        binding.historyRv.layoutManager = LinearLayoutManager(this)
        binding.historyRv.setHasFixedSize(true)
        binding.historyRv.adapter = adapter
        loadNotesAsync()
    }


    private fun loadNotesAsync() { // Mengambil data secara asynchronus menggunakan background proses
        lifecycleScope.launch {
            val historyHelper = HistoryHelper.getInstance(applicationContext)
            historyHelper.open()
            val deferredNotes = async(Dispatchers.IO) {//asnc karna kita ingin nilai kembalian dari fungsi yang kita panggil
                val cursor = historyHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }

            val history = deferredNotes.await()
            if (history.size > 0) {
                adapter.listHistory = history
            } else {
                adapter.listHistory = ArrayList()
                showSnackbarMessage("Tidak ada data saat ini")
            }
            historyHelper.close()
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.historyRv, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(BluetoothAdapter.EXTRA_STATE, adapter.listHistory)
    }


}