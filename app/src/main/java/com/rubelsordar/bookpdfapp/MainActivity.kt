package com.rubelsordar.bookpdfapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        val pdfFile = File(filesDir, "document.pdf")
        
        if (!pdfFile.exists()) {
            binding.tvResultCount.text = "PDF ফাইল পাওয়া যাচ্ছে না"
            binding.tvResultCount.visibility = View.VISIBLE
            return
        }

        binding.pdfView.fromFile(pdfFile)
            .defaultPage(0)
            .onPageChange(this)
            .spacing(10)
            .load()

        pdfSearcher = PDFSearcher(pdfFile)
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
        val results = pdfSearcher?.search(query) ?: emptyList()
        
        if (results.isEmpty()) {
            binding.tvResultCount.text = "কোনো ফলাফল পাওয়া যাচ্ছে না"
            binding.tvResultCount.visibility = View.VISIBLE
            highlightPositions = emptyList()
            currentHighlightIndex = -1
            return
        }
        
        highlightPositions = results
        currentHighlightIndex = 0
        navigateToHighlight()
    }

    private fun navigateToHighlight() {
        if (highlightPositions.isEmpty()) return
        
        val position = highlightPositions[currentHighlightIndex]
        
        // Jump to the page if different
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
