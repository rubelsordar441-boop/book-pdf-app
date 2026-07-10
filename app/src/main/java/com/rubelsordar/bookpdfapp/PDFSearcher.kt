package com.rubelsordar.bookpdfapp

import android.util.Log
import com.tom_roush.pdfbox.pdfparser.PDFParser
import com.tom_roush.pdfbox.pdfdocument.PDFDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.FileInputStream

class PDFSearcher(private val pdfFile: File) {

    data class HighlightPosition(
        val pageNumber: Int,
        val text: String,
        val startIndex: Int,
        val endIndex: Int
    )

    private val pageTexts: Map<Int, String> = extractTextFromPDF()

    companion object {
        private const val TAG = "PDFSearcher"
    }

    private fun extractTextFromPDF(): Map<Int, String> {
        val pages = mutableMapOf<Int, String>()
        var inputStream: FileInputStream? = null
        var document: PDFDocument? = null

        try {
            inputStream = FileInputStream(pdfFile)
            val parser = PDFParser(inputStream)
            document = parser.parse()
            val stripper = PDFTextStripper()
            
            val numberOfPages = document.numberOfPages
            Log.d(TAG, "Extracting text from $numberOfPages pages")
            
            for (pageNum in 0 until numberOfPages) {
                try {
                    stripper.startPage = pageNum + 1
                    stripper.endPage = pageNum + 1
                    val text = stripper.getText(document)
                    pages[pageNum] = text
                    Log.d(TAG, "Page $pageNum extracted successfully (${text.length} chars)")
                } catch (e: Exception) {
                    Log.w(TAG, "Error extracting page $pageNum: ${e.message}", e)
                    pages[pageNum] = ""
                }
            }
            
            Log.d(TAG, "Successfully extracted text from ${pages.size} pages")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing PDF: ${e.message}", e)
        } finally {
            try {
                document?.close()
                inputStream?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing resources: ${e.message}", e)
            }
        }
        
        return pages
    }

    fun search(query: String): List<HighlightPosition> {
        if (query.isEmpty()) {
            Log.w(TAG, "Search query is empty")
            return emptyList()
        }
        
        try {
            val results = mutableListOf<HighlightPosition>()
            val lowerQuery = query.lowercase()
            var totalMatches = 0

            pageTexts.forEach { (pageNum, text) ->
                if (text.isEmpty()) {
                    Log.d(TAG, "Page $pageNum is empty, skipping")
                    return@forEach
                }

                val lowerText = text.lowercase()
                var startIndex = 0
                var pageMatches = 0
                
                while (true) {
                    val foundIndex = lowerText.indexOf(lowerQuery, startIndex)
                    if (foundIndex == -1) break
                    
                    val matchedText = if (foundIndex + query.length <= text.length) {
                        text.substring(foundIndex, foundIndex + query.length)
                    } else {
                        query
                    }
                    
                    results.add(
                        HighlightPosition(
                            pageNumber = pageNum,
                            text = matchedText,
                            startIndex = foundIndex,
                            endIndex = foundIndex + query.length
                        )
                    )
                    
                    startIndex = foundIndex + 1
                    pageMatches++
                }
                
                if (pageMatches > 0) {
                    totalMatches += pageMatches
                    Log.d(TAG, "Page $pageNum: found $pageMatches matches")
                }
            }

            Log.d(TAG, "Total matches found for '$query': $totalMatches")
            return results.sortedWith(compareBy({ it.pageNumber }, { it.startIndex }))
        } catch (e: Exception) {
            Log.e(TAG, "Error during search: ${e.message}", e)
            return emptyList()
        }
    }
}
