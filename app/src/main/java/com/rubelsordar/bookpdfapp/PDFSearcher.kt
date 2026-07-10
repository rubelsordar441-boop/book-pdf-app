package com.rubelsordar.bookpdfapp

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

    private fun extractTextFromPDF(): Map<Int, String> {
        val pages = mutableMapOf<Int, String>()
        try {
            val inputStream = FileInputStream(pdfFile)
            val parser = PDFParser(inputStream)
            val document = parser.parse()
            val stripper = PDFTextStripper()
            
            val numberOfPages = document.numberOfPages
            
            for (pageNum in 0 until numberOfPages) {
                stripper.startPage = pageNum + 1
                stripper.endPage = pageNum + 1
                val text = stripper.getText(document)
                pages[pageNum] = text
            }
            
            document.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pages
    }

    fun search(query: String): List<HighlightPosition> {
        if (query.isEmpty()) return emptyList()
        
        val results = mutableListOf<HighlightPosition>()
        val lowerQuery = query.lowercase()

        pageTexts.forEach { (pageNum, text) ->
            val lowerText = text.lowercase()
            var startIndex = 0
            
            while (true) {
                val foundIndex = lowerText.indexOf(lowerQuery, startIndex)
                if (foundIndex == -1) break
                
                // Extract the actual text from original (not lowercase)
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
            }
        }

        return results.sortedWith(compareBy({ it.pageNumber }, { it.startIndex }))
    }
}
