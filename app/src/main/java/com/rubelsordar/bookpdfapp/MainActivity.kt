package com.rubelsordar.bookpdfapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.rubelsordar.bookpdfapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), OnPageChangeListener {
    private lateinit var binding: ActivityMainBinding
    private var pdfSearcher: PDFSearcher? = null
    private var currentHighlightIndex = -1
    private var highlightPositions: List<PDFSearcher.HighlightPosition> = emptyList()
    private var currentPage = 0
    private var totalPages = 0
    private var lastSearchQuery = ""

    companion object {
        private const val TAG = "MainActivity"
        private const val PDF_FILE_NAME = "document.pdf"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPDFView()
        setupSearchListener()
        setupNavigationButtons()
        setupToolbar()
    }

    private fun setupPDFView() {
        try {
            val pdfFile = getPDFFile()
            
            if (!pdfFile.exists()) {
                Log.e(TAG, "PDF file not found at: ${pdfFile.absolutePath}")
                binding.tvResultCount.text = "PDF ফাইল পাওয়া যাচ্ছে না"
                binding.tvResultCount.visibility = View.VISIBLE
                return
            }

            Log.d(TAG, "Loading PDF from: ${pdfFile.absolutePath}")
            binding.pdfView.fromFile(pdfFile)
                .defaultPage(0)
                .onPageChange(this)
                .spacing(10)
                .load()

            pdfSearcher = PDFSearcher(pdfFile)
            Log.d(TAG, "PDF loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading PDF: ${e.message}", e)
            binding.tvResultCount.text = "PDF লোড করতে সমস্যা হয়েছে"
            binding.tvResultCount.visibility = View.VISIBLE
            Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getPDFFile(): File {
        // প্রথমে assets থেকে কপি করার চেষ্টা করো
        val assetsDir = File(filesDir, "assets")
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        val pdfFile = File(assetsDir, PDF_FILE_NAME)
        
        // যদি ফাইল না থাকে তো assets থেকে কপি করো
        if (!pdfFile.exists()) {
            try {
                val inputStream = assets.open(PDF_FILE_NAME)
                pdfFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                Log.d(TAG, "PDF copied from assets successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Could not copy PDF from assets: ${e.message}")
            }
        }
        
        return pdfFile
    }

    private fun setupToolbar() {
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        binding.toolbar.elevation = 8f
    }

    private fun setupSearchListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty() && query != lastSearchQuery) {
                    lastSearchQuery = query
                    performSearch(query)
                } else if (query.isEmpty()) {
                    clearSearch()
                }
            }
        })
    }

    private fun setupNavigationButtons() {
        binding.btnPrevious.setOnClickListener {
            if (highlightPositions.isEmpty()) {
                Toast.makeText(this, "প্রথমে কিছু খুঁজুন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (currentHighlightIndex > 0) {
                currentHighlightIndex--
                navigateToHighlight()
            } else {
                Toast.makeText(this, "এটি প্রথম ফলাফল", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNext.setOnClickListener {
            if (highlightPositions.isEmpty()) {
                Toast.makeText(this, "প্রথমে কিছু খুঁজুন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (currentHighlightIndex < highlightPositions.size - 1) {
                currentHighlightIndex++
                navigateToHighlight()
            } else {
                Toast.makeText(this, "এটি শেষ ফলাফল", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(query: String) {
        try {
            val results = pdfSearcher?.search(query) ?: emptyList()
            
            if (results.isEmpty()) {
                Log.d(TAG, "No search results found for: $query")
                binding.tvResultCount.text = "কোনো ফলাফল পাওয়া যাচ্ছে না"
                binding.tvResultCount.visibility = View.VISIBLE
                highlightPositions = emptyList()
                currentHighlightIndex = -1
                return
            }
            
            Log.d(TAG, "Found ${results.size} results for: $query")
            highlightPositions = results
            currentHighlightIndex = 0
            navigateToHighlight()
        } catch (e: Exception) {
            Log.e(TAG, "Error performing search: ${e.message}", e)
            Toast.makeText(this, "সার্চে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHighlight() {
        if (highlightPositions.isEmpty()) return
        
        val position = highlightPositions[currentHighlightIndex]
        
        if (currentPage != position.pageNumber) {
            currentPage = position.pageNumber
            binding.pdfView.jumpTo(position.pageNumber)
        }
        
        updateResultCount()
    }

    private fun updateResultCount() {
        if (highlightPositions.isEmpty()) {
            binding.tvResultCount.visibility = View.GONE
        } else {
            val count = "${currentHighlightIndex + 1} / ${highlightPositions.size}"
            binding.tvResultCount.text = count
            binding.tvResultCount.visibility = View.VISIBLE
        }
    }

    private fun clearSearch() {
        highlightPositions = emptyList()
        currentHighlightIndex = -1
        lastSearchQuery = ""
        binding.tvResultCount.visibility = View.GONE
    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        currentPage = page
        totalPages = pageCount
        binding.tvPageNumber.text = "পৃষ্ঠা ${page + 1} / $pageCount"
    }
}
