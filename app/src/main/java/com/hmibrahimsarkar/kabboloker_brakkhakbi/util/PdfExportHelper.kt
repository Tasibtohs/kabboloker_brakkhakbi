package com.hmibrahimsarkar.kabboloker_brakkhakbi.util

import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {

    private const val TAG = "PDF_DEBUG"

    /**
     * Checks whether active network connection is available.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val net = cm?.activeNetwork ?: return false
                val caps = cm.getNetworkCapabilities(net) ?: return false
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                @Suppress("DEPRECATION")
                val activeNetInfo = cm?.activeNetworkInfo
                activeNetInfo != null && activeNetInfo.isConnected
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Ensures text color has sufficient contrast against the ivory/cream background (#FAF9F4).
     * If the color is too light (or unparseable), returns dark charcoal (#2C2C3A).
     */
    private fun ensureReadableTextColor(textColorHex: String, defaultColorHex: String = "#2C2C3A"): String {
        return try {
            if (textColorHex.isBlank()) return defaultColorHex
            val cleanHex = textColorHex.trim()
            val normalizedHex = when {
                cleanHex.startsWith("#") -> cleanHex
                cleanHex.length == 6 || cleanHex.length == 8 -> "#$cleanHex"
                else -> return defaultColorHex
            }
            val color = android.graphics.Color.parseColor(normalizedHex)
            val r = android.graphics.Color.red(color) / 255.0
            val g = android.graphics.Color.green(color) / 255.0
            val b = android.graphics.Color.blue(color) / 255.0
            // Calculate relative luminance
            val luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b
            // Cream background luminance is ~0.97. If text luminance is > 0.82, it's too light against cream background.
            if (luminance > 0.82) {
                defaultColorHex
            } else {
                normalizedHex
            }
        } catch (e: Exception) {
            defaultColorHex
        }
    }

    fun savePdfToPublicDownloads(context: Context, sourceFile: File, displayName: String): Uri? {
        val resolver = context.contentResolver
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Files.getContentUri("external")
            }

            val itemUri = resolver.insert(collection, contentValues)
            if (itemUri != null) {
                resolver.openOutputStream(itemUri)?.use { out ->
                    sourceFile.inputStream().use { input ->
                        input.copyTo(out)
                    }
                    out.flush()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(itemUri, contentValues, null, null)
                }
                itemUri
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, displayName)
                sourceFile.copyTo(targetFile, overwrite = true)
                Uri.fromFile(targetFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to Public Downloads", e)
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, displayName)
                sourceFile.copyTo(targetFile, overwrite = true)
                Uri.fromFile(targetFile)
            } catch (ex: Exception) {
                Log.e(TAG, "Fallback download save failed", ex)
                null
            }
        }
    }

    /**
     * Centralized formatter for author signature line.
     * 1. Default: "— লেখক: এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি"
     * 2. Custom author name (e.g. "কাজী নজরুল ইসলাম"): "— লেখক: কাজী নজরুল ইসলাম – কাব্যলোকের ব্রক্ষকবি"
     * 3. Empty author name: "" (Completely hidden)
     */
    fun formatAuthorSignature(authorName: String): String {
        val trimmed = authorName.trim()
        if (trimmed.isBlank()) return ""
        return when {
            trimmed == "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি" ||
            trimmed == "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রহ্মকবি" ||
            trimmed == "এইচ. এম. ইব্রাহীম ত্বহা সরকার" -> "— লেখক: এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি"
            trimmed.contains("কাব্যলোকের") -> "— লেখক: $trimmed"
            else -> "— লেখক: $trimmed – কাব্যলোকের ব্রক্ষকবি"
        }
    }

    fun toBengaliNumerals(number: Int): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        return number.toString().map { if (it in '0'..'9') bnDigits[it - '0'] else it }.joinToString("")
    }

    private sealed class PdfPageItem {
        /**
         * Two distinct poems on left and right columns (each <= 15 lines)
         */
        data class TwoPoemSpread(
            val leftNote: NoteEntity,
            val leftPageNum: Int,
            val rightNote: NoteEntity?,
            val rightPageNum: Int?
        ) : PdfPageItem()

        /**
         * A single poem spanning both columns (> 15 lines for multi-note booklet)
         * leftContent: First column text
         * rightContent: Second column text (remaining lines)
         */
        data class SpanningPoemSpread(
            val note: NoteEntity,
            val leftContent: String,
            val rightContent: String,
            val leftPageNum: Int,
            val rightPageNum: Int
        ) : PdfPageItem()

        /**
         * Standalone single note export:
         * If <= 35 lines: fits completely in Column 1, right column is empty/unused. Title in Column 1.
         * If > 35 lines: exactly first 35 lines in Column 1, line 36+ in Column 2. Title still in Column 1.
         */
        data class SingleNoteStandalone(
            val note: NoteEntity,
            val isOverflowing: Boolean,
            val leftLines: String,
            val rightLines: String
        ) : PdfPageItem()
    }

    /**
     * Determines whether a poem is long (> 35 lines) for multi-note booklet export.
     */
    fun isLongPoem(note: NoteEntity): Boolean {
        val lines = note.content.lines()
        return lines.size > 35
    }

    /**
     * Determines whether a poem exceeds 35 lines for single note export.
     */
    fun isSingleNoteOverflowing(note: NoteEntity): Boolean {
        val lines = note.content.lines()
        return lines.size > 35
    }

    /**
     * Splits a long poem's lines sequentially for multi-note collection so column 1 is filled with
     * exactly 35 lines before flowing to column 2.
     */
    private fun splitPoemIntoTwoColumns(content: String): Pair<String, String> {
        val lines = content.lines()
        if (lines.size <= 35) {
            return Pair(content, "")
        }
        val col1Lines = lines.take(35)
        val col2Lines = lines.drop(35)
        return Pair(col1Lines.joinToString("\n"), col2Lines.joinToString("\n"))
    }

    /**
     * Splits a single note for standalone export:
     * - Exactly first 35 lines in Column 1
     * - 36th line onwards in Column 2
     */
    private fun splitSingleNoteBy35Lines(content: String): Pair<String, String> {
        val lines = content.lines()
        if (lines.size <= 35) {
            return Pair(content, "")
        }
        val col1Lines = lines.take(35)
        val col2Lines = lines.drop(35)
        return Pair(col1Lines.joinToString("\n"), col2Lines.joinToString("\n"))
    }

    /**
     * Calculates optimal typography for a single column.
     */
    private fun calculateColumnTypography(
        content: String,
        userFontSizeSp: Float,
        userLineSpacingMultiplier: Float
    ): Pair<Float, Float> {
        val lines = content.lines()
        val lineCount = lines.size
        val charCount = content.length

        return when {
            lineCount > 26 || charCount > 550 -> Pair(11.5f, 1.25f)
            lineCount > 16 || charCount > 380 -> Pair(13.5f, 1.34f)
            lineCount > 12 || charCount > 260 -> Pair(14.5f, 1.40f)
            lineCount > 8  || charCount > 160 -> Pair(15.5f, 1.45f)
            else -> {
                val base = (userFontSizeSp * 0.95f).coerceIn(15.0f, 17.5f)
                val lRatio = userLineSpacingMultiplier.coerceIn(1.42f, 1.55f)
                Pair(base, lRatio)
            }
        }
    }

    /**
     * Calculates optimal typography for a long poem spanning 2 columns across the whole page.
     */
    private fun calculateSpanningTypography(
        content: String,
        userFontSizeSp: Float,
        userLineSpacingMultiplier: Float
    ): Pair<Float, Float> {
        val lines = content.lines()
        val lineCount = lines.size
        val charCount = content.length

        return when {
            lineCount > 50 || charCount > 1600 -> Pair(11.0f, 1.22f)
            lineCount > 35 || charCount > 1100 -> Pair(11.5f, 1.25f)
            lineCount > 20 || charCount > 550  -> Pair(13.5f, 1.32f)
            else -> {
                val base = (userFontSizeSp * 0.95f).coerceIn(14.0f, 16.5f)
                val lRatio = userLineSpacingMultiplier.coerceIn(1.35f, 1.50f)
                Pair(base, lRatio)
            }
        }
    }

    /**
     * Calculates optimal typography for a single note spanning the full landscape page.
     */
    private fun calculateSingleNoteTypography(
        content: String,
        userFontSizeSp: Float,
        userLineSpacingMultiplier: Float
    ): Pair<Float, Float> {
        val lines = content.lines()
        val lineCount = lines.size
        val charCount = content.length

        return when {
            lineCount > 35 || charCount > 1200 -> Pair(14.0f, 1.35f)
            lineCount > 24 || charCount > 800  -> Pair(16.0f, 1.40f)
            lineCount > 16 || charCount > 500  -> Pair(18.0f, 1.45f)
            else -> {
                val base = (userFontSizeSp * 1.1f).coerceIn(18.0f, 22.0f)
                val lRatio = userLineSpacingMultiplier.coerceIn(1.45f, 1.6f)
                Pair(base, lRatio)
            }
        }
    }

    /**
     * Builds HTML template styled in A4 Landscape orientation (1123px x 794px).
     * - Multi-note collection features an outer decorative border, individual column-cards, center alignment,
     *   full-width centered header/date, per-column page numbers ("— ১ —", "— ২ —"), sequential line filling,
     *   and author signature placed directly after actual poem ending.
     */
    fun buildHtmlForNotes(
        notes: List<NoteEntity>,
        isSingleNote: Boolean,
        authorName: String = "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি"
    ): String {
        Log.d(TAG, "Building HTML for notes (Landscape 2-Column Spread). Count: ${notes.size}, isSingleNote: $isSingleNote, author: $authorName")
        val exportDate = SimpleDateFormat("dd MMMM, yyyy", Locale("bn", "BD")).format(Date())
        val formattedAuthorText = formatAuthorSignature(authorName)

        // CSS @font-face definitions for all 20 local Bengali fonts from assets
        val fontFaceRules = BengaliFonts.fonts.joinToString("\n") { fontOption ->
            """
            @font-face {
                font-family: '${fontOption.key}';
                src: url('fonts/${fontOption.assetFileName}');
            }
            """.trimIndent()
        }

        // ================= 1. COVER PAGE (A4 LANDSCAPE - 1 COLUMN CONTENT + 1 COLUMN EMPTY) =================
        val coverPageHtml = if (!isSingleNote) {
            """
            <div class="landscape-page cover-page page-break">
                <div class="page-outer-frame">
                    <div class="page-inner-frame">
                        <span class="corner-ornament corner-tl">❖</span>
                        <span class="corner-ornament corner-tr">❖</span>
                        <span class="corner-ornament corner-bl">❖</span>
                        <span class="corner-ornament corner-br">❖</span>

                        <!-- Top Header -->
                        <div class="page-top-header">
                            <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                            <span class="header-center">✦ ❦ ✦</span>
                            <span class="header-right">$exportDate</span>
                        </div>
                        <div class="header-divider-line"></div>

                        <!-- 2-Column Container: Left has Cover Card, Right is Empty -->
                        <div class="two-column-container cover-columns-container">
                            <div class="column-slot cover-left-slot">
                                <div class="cover-card">
                                    <div class="cover-card-inner">
                                        <span class="corner-ornament corner-tl">❖</span>
                                        <span class="corner-ornament corner-tr">❖</span>
                                        <span class="corner-ornament corner-bl">❖</span>
                                        <span class="corner-ornament corner-br">❖</span>

                                        <div class="cover-top-ornament">❖ ❦ ❖</div>
                                        <div class="cover-badge">
                                            <span class="cover-icon">✒</span>
                                        </div>
                                        <h1 class="cover-title">কাব্যলোকের ব্রহ্মকবি</h1>
                                        <div class="cover-subtitle">~ কবি ও শব্দের আসর • কবিতা সংকলন ~</div>
                                        <div class="cover-divider">✦ ❦ ✦</div>
                                        <div class="cover-info">
                                            <div class="cover-info-label">কবিতা ও সাহিত্য সংকলন (বুকলেট সংস্করণ)</div>
                                            <div class="cover-date">সংকলনের তারিখ: $exportDate</div>
                                            <div class="cover-count">মোট কবিতা: ${toBengaliNumerals(notes.size)} টি</div>
                                            ${if (formattedAuthorText.isNotBlank()) "<div class=\"cover-author\">${escapeHtml(formattedAuthorText)}</div>" else ""}
                                        </div>
                                        <div class="cover-footer-text">একটি অনবদ্য সাহিত্য সৃষ্টি</div>
                                        <div class="cover-bottom-ornament">❖ ❦ ❖</div>
                                    </div>
                                </div>
                            </div>
                            <div class="column-slot cover-right-empty-slot"></div>
                        </div>

                        <!-- Page Bottom Footer -->
                        <div class="page-bottom-footer">
                            <div class="footer-divider"></div>
                            <div class="footer-row">
                                <span class="footer-app-name">কাব্যলোকের ব্রহ্মকবি</span>
                                <span class="footer-page-num">— কভার পাতা —</span>
                                <span class="footer-app-name">সাহিত্য সংকলন</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent()
        } else ""

        // ================= PAIRING & PER-COLUMN PAGE NUMBER CALCULATION =================
        val pageItems = mutableListOf<PdfPageItem>()
        val notePageMap = mutableMapOf<Long, Int>()
        var currentColumnLeafNumber = 1 // Every column counts as 1 leaf/page (১, ২, ৩, ...)

        if (isSingleNote) {
            if (notes.isNotEmpty()) {
                val note = notes.first()
                if (isSingleNoteOverflowing(note)) {
                    val (col1, col2) = splitSingleNoteBy35Lines(note.content)
                    pageItems.add(
                        PdfPageItem.SingleNoteStandalone(
                            note = note,
                            isOverflowing = true,
                            leftLines = col1,
                            rightLines = col2
                        )
                    )
                } else {
                    pageItems.add(
                        PdfPageItem.SingleNoteStandalone(
                            note = note,
                            isOverflowing = false,
                            leftLines = note.content,
                            rightLines = ""
                        )
                    )
                }
            }
        } else {
            var i = 0
            while (i < notes.size) {
                val current = notes[i]
                if (isLongPoem(current)) {
                    // Long poem (>35 lines): takes both columns of a physical page
                    val (col1, col2) = splitPoemIntoTwoColumns(current.content)
                    val p1 = currentColumnLeafNumber
                    val p2 = currentColumnLeafNumber + 1
                    notePageMap[current.id] = p1
                    pageItems.add(
                        PdfPageItem.SpanningPoemSpread(
                            note = current,
                            leftContent = col1,
                            rightContent = col2,
                            leftPageNum = p1,
                            rightPageNum = p2
                        )
                    )
                    currentColumnLeafNumber += 2
                    i++
                } else {
                    // Short poem (<=35 lines): takes left column
                    val p1 = currentColumnLeafNumber
                    notePageMap[current.id] = p1
                    currentColumnLeafNumber++

                    val next = notes.getOrNull(i + 1)
                    if (next != null) {
                        if (isLongPoem(next)) {
                            // Next is long (>35 lines), so right column stays empty on this physical page
                            pageItems.add(
                                PdfPageItem.TwoPoemSpread(
                                    leftNote = current,
                                    leftPageNum = p1,
                                    rightNote = null,
                                    rightPageNum = null
                                )
                            )
                            i++
                        } else {
                            // Both are short (<=35 lines): place side-by-side as two distinct page cards
                            val p2 = currentColumnLeafNumber
                            notePageMap[next.id] = p2
                            currentColumnLeafNumber++
                            pageItems.add(
                                PdfPageItem.TwoPoemSpread(
                                    leftNote = current,
                                    leftPageNum = p1,
                                    rightNote = next,
                                    rightPageNum = p2
                                )
                            )
                            i += 2
                        }
                    } else {
                        // Odd single poem remaining at the end
                        pageItems.add(
                            PdfPageItem.TwoPoemSpread(
                                leftNote = current,
                                leftPageNum = p1,
                                rightNote = null,
                                rightPageNum = null
                            )
                        )
                        i++
                    }
                }
            }
        }

        // ================= 2. TABLE OF CONTENTS (A4 LANDSCAPE - 2-COLUMN SERIAL CONTINUOUS FLOW) =================
        val tocItemsPerColumn = 14
        val tocItemsPerPage = tocItemsPerColumn * 2 // 28 items per landscape page

        val tocPageHtml = if (!isSingleNote && notes.size > 1) {
            val noteChunks = notes.chunked(tocItemsPerPage)

            noteChunks.mapIndexed { chunkIndex, chunkNotes ->
                val tocPart = chunkIndex + 1
                val baseIdx = chunkIndex * tocItemsPerPage

                // Split chunkNotes into left and right columns sequentially:
                // Left column: first up to 14 notes of this page
                // Right column: next up to 14 notes of this page
                val leftNotes = chunkNotes.take(tocItemsPerColumn)
                val rightNotes = chunkNotes.drop(tocItemsPerColumn)

                fun renderTocItems(subList: List<NoteEntity>, offset: Int): String {
                    return subList.mapIndexed { idx, note ->
                        val globalIndex = offset + idx + 1
                        val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
                        val pageNum = notePageMap[note.id] ?: globalIndex
                        val bnPageNum = toBengaliNumerals(pageNum)
                        """
                        <div class="toc-item">
                            <span class="toc-item-title">${toBengaliNumerals(globalIndex)}. ${escapeHtml(title)}</span>
                            <span class="toc-leader"></span>
                            <span class="toc-item-page">পাতা $bnPageNum</span>
                        </div>
                        """.trimIndent()
                    }.joinToString("\n")
                }

                val leftColumnHtml = renderTocItems(leftNotes, baseIdx)
                val rightColumnHtml = renderTocItems(rightNotes, baseIdx + leftNotes.size)

                val partSubtitle = if (noteChunks.size > 1) {
                    "কাব্য সংকলন সূচী • পর্ব ${toBengaliNumerals(tocPart)}"
                } else {
                    "কাব্য সংকলন সূচী • বুকলেট সংস্করণ"
                }

                """
                <div class="landscape-page toc-page page-break">
                    <div class="page-outer-frame">
                        <div class="page-inner-frame">
                            <span class="corner-ornament corner-tl">❖</span>
                            <span class="corner-ornament corner-tr">❖</span>
                            <span class="corner-ornament corner-bl">❖</span>
                            <span class="corner-ornament corner-br">❖</span>

                            <!-- Top Header -->
                            <div class="page-top-header">
                                <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                                <span class="header-center">✦ ❦ ✦</span>
                                <span class="header-right">$exportDate</span>
                            </div>
                            <div class="header-divider-line"></div>

                            <!-- TOC Header -->
                            <div class="toc-header">
                                <div class="toc-ornament">❖ ❦ ❖</div>
                                <h2 class="toc-title">সূচিপত্র</h2>
                                <div class="toc-subtitle">$partSubtitle</div>
                                <div class="toc-line"></div>
                            </div>

                            <!-- TOC Body (2 Columns with Continuous Serial Flow) -->
                            <div class="toc-columns-container">
                                <div class="toc-col toc-left-col">
                                    $leftColumnHtml
                                </div>
                                <div class="toc-gutter-divider"></div>
                                <div class="toc-col toc-right-col">
                                    $rightColumnHtml
                                </div>
                            </div>

                            <!-- Page Bottom Footer -->
                            <div class="page-bottom-footer">
                                <div class="footer-divider"></div>
                                <div class="footer-row">
                                    <span class="footer-app-name">কাব্যলোকের ব্রহ্মকবি</span>
                                    <span class="footer-page-num">— সূচিপত্র —</span>
                                    <span class="footer-app-name">সাহিত্য সংকলন</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            }.joinToString("\n")
        } else ""

        // ================= 3. RENDER HELPERS =================
        fun renderAuthorSignature(fontCssFamily: String): String {
            if (formattedAuthorText.isBlank()) return ""
            return """
            <div class="poem-author-section">
                <div class="author-divider-line"></div>
                <div class="author-signature-text" style="font-family: $fontCssFamily;">${escapeHtml(formattedAuthorText)}</div>
            </div>
            """.trimIndent()
        }

        fun renderFullWidthHeader(note: NoteEntity): String {
            val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
            val noteDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.createdAt))
            val updatedDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.updatedAt))

            val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)
            val fontCssFamily = "'${fontOption.key}', 'Tiro Bangla', serif, sans-serif"
            val readableTitleColor = ensureReadableTextColor(note.titleColorHex, defaultColorHex = "#B8860B")

            return """
            <div class="fullwidth-poem-header">
                <h2 class="fullwidth-poem-title" style="color: $readableTitleColor; font-family: $fontCssFamily;">${escapeHtml(title)}</h2>
                <div class="poem-divider">
                    <span class="poem-divider-line"></span>
                    <span class="poem-symbol">❦</span>
                    <span class="poem-divider-line"></span>
                </div>
                <div class="poem-meta">
                    <span>রচনাকাল: $noteDate</span>
                    ${if (note.updatedAt > note.createdAt + 60000) " • <span>সম্পাদিত: $updatedDate</span>" else ""}
                </div>
            </div>
            """.trimIndent()
        }

        fun renderSinglePoemCardContent(note: NoteEntity, leafPageNum: Int): String {
            val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
            val content = if (note.content.isNotBlank()) note.content else "(ফাঁকা কবিতা)"

            val noteDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.createdAt))
            val updatedDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.updatedAt))

            val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)
            val fontCssFamily = "'${fontOption.key}', 'Tiro Bangla', serif, sans-serif"

            val readableTitleColor = ensureReadableTextColor(note.titleColorHex, defaultColorHex = "#B8860B")
            val readableTextColor = ensureReadableTextColor(note.textColorHex, defaultColorHex = "#2C2C3A")

            val textDecorations = mutableListOf<String>()
            if (note.isUnderline) textDecorations.add("underline")
            if (note.isStrikethrough) textDecorations.add("line-through")
            val textDecorationCss = if (textDecorations.isNotEmpty()) textDecorations.joinToString(" ") else "none"

            val fontWeight = if (note.isBold) "bold" else "normal"
            val fontStyle = if (note.isItalic) "italic" else "normal"

            val (fontSizePx, lineHeightRatio) = calculateColumnTypography(content, note.fontSizeSp, note.lineSpacingMultiplier)

            val safeTitle = escapeHtml(title)
            val safeContent = escapeHtml(content)
            val leafPageNumBn = toBengaliNumerals(leafPageNum)
            val authorHtml = renderAuthorSignature(fontCssFamily)

            return """
            <div class="column-leaf-card">
                <div class="leaf-header">
                    <h3 class="leaf-poem-title" style="color: $readableTitleColor; font-family: $fontCssFamily;">$safeTitle</h3>
                    <div class="leaf-divider">
                        <span class="leaf-divider-line"></span>
                        <span class="leaf-symbol">❦</span>
                        <span class="leaf-divider-line"></span>
                    </div>
                    <div class="leaf-meta">
                        <span>রচনাকাল: $noteDate</span>
                        ${if (note.updatedAt > note.createdAt + 60000) " • <span>সম্পাদিত: $updatedDate</span>" else ""}
                    </div>
                </div>

                <div class="leaf-body" style="
                    color: $readableTextColor;
                    font-size: ${fontSizePx}px;
                    font-weight: $fontWeight;
                    font-style: $fontStyle;
                    text-decoration: $textDecorationCss;
                    line-height: $lineHeightRatio;
                    font-family: $fontCssFamily;
                ">$safeContent</div>

                $authorHtml

                <div class="leaf-footer">
                    <span class="leaf-page-num">— $leafPageNumBn —</span>
                </div>
            </div>
            """.trimIndent()
        }

        // ================= 4. BUILD PAGES HTML =================
        var pageIndex = 0
        val pagesHtml = pageItems.map { pageItem ->
            pageIndex++
            val isPageBreakNeeded = !(isSingleNote && pageIndex == 1)
            val pageBreakClass = if (isPageBreakNeeded) "page-break" else ""

            when (pageItem) {
                is PdfPageItem.TwoPoemSpread -> {
                    val leftCard = renderSinglePoemCardContent(pageItem.leftNote, pageItem.leftPageNum)
                    val rightCard = if (pageItem.rightNote != null && pageItem.rightPageNum != null) {
                        renderSinglePoemCardContent(pageItem.rightNote, pageItem.rightPageNum)
                    } else {
                        """
                        <div class="column-leaf-empty">
                            <div class="empty-column-motif">❖ ❦ ❖</div>
                        </div>
                        """.trimIndent()
                    }

                    """
                    <div class="landscape-page two-column-page $pageBreakClass">
                        <div class="page-outer-frame">
                            <div class="page-inner-frame">
                                <span class="corner-ornament corner-tl">❖</span>
                                <span class="corner-ornament corner-tr">❖</span>
                                <span class="corner-ornament corner-bl">❖</span>
                                <span class="corner-ornament corner-br">❖</span>

                                <!-- Top Header -->
                                <div class="page-top-header">
                                    <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                                    <span class="header-center">✦ ❦ ✦</span>
                                    <span class="header-right">$exportDate</span>
                                </div>
                                <div class="header-divider-line"></div>

                                <!-- Two Distinct Book Spread Columns -->
                                <div class="two-column-container">
                                    <div class="column-slot left-slot">
                                        $leftCard
                                    </div>
                                    <div class="column-slot right-slot">
                                        $rightCard
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    """.trimIndent()
                }

                is PdfPageItem.SpanningPoemSpread -> {
                    val note = pageItem.note
                    val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
                    val noteDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.createdAt))
                    val updatedDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.updatedAt))

                    val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)
                    val fontCssFamily = "'${fontOption.key}', 'Tiro Bangla', serif, sans-serif"

                    val readableTitleColor = ensureReadableTextColor(note.titleColorHex, defaultColorHex = "#B8860B")
                    val readableTextColor = ensureReadableTextColor(note.textColorHex, defaultColorHex = "#2C2C3A")

                    val textDecorations = mutableListOf<String>()
                    if (note.isUnderline) textDecorations.add("underline")
                    if (note.isStrikethrough) textDecorations.add("line-through")
                    val textDecorationCss = if (textDecorations.isNotEmpty()) textDecorations.joinToString(" ") else "none"

                    val fontWeight = if (note.isBold) "bold" else "normal"
                    val fontStyle = if (note.isItalic) "italic" else "normal"

                    val (fontSizePx, lineHeightRatio) = calculateSpanningTypography(note.content, note.fontSizeSp, note.lineSpacingMultiplier)

                    val safeTitle = escapeHtml(title)
                    val leftContentHtml = escapeHtml(pageItem.leftContent)
                    val rightContentHtml = escapeHtml(pageItem.rightContent)

                    val leftPageBn = toBengaliNumerals(pageItem.leftPageNum)
                    val rightPageBn = toBengaliNumerals(pageItem.rightPageNum)

                    val authorSignatureInRightColumn = renderAuthorSignature(fontCssFamily)

                    val leftColumnHeaderHtml = """
                    <div class="leaf-header">
                        <h3 class="leaf-poem-title" style="color: $readableTitleColor; font-family: $fontCssFamily;">$safeTitle</h3>
                        <div class="leaf-divider">
                            <span class="leaf-divider-line"></span>
                            <span class="leaf-symbol">❦</span>
                            <span class="leaf-divider-line"></span>
                        </div>
                        <div class="leaf-meta">
                            <span>রচনাকাল: $noteDate</span>
                            ${if (note.updatedAt > note.createdAt + 60000) " • <span>সম্পাদিত: $updatedDate</span>" else ""}
                        </div>
                    </div>
                    """.trimIndent()

                    """
                    <div class="landscape-page spanning-page $pageBreakClass">
                        <div class="page-outer-frame">
                            <div class="page-inner-frame">
                                <span class="corner-ornament corner-tl">❖</span>
                                <span class="corner-ornament corner-tr">❖</span>
                                <span class="corner-ornament corner-bl">❖</span>
                                <span class="corner-ornament corner-br">❖</span>

                                <!-- Top Header -->
                                <div class="page-top-header">
                                    <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                                    <span class="header-center">✦ ❦ ✦</span>
                                    <span class="header-right">$exportDate</span>
                                </div>
                                <div class="header-divider-line"></div>

                                <!-- Unified Outer Frame for Single Long Poem -->
                                <div class="spanning-poem-container">
                                    <div class="spanning-card">
                                        <!-- Two-Column Spread Content Area -->
                                        <div class="spanning-columns-wrapper">
                                            <!-- Column 1 (Left Part: Header + 35 Lines) -->
                                            <div class="spanning-col left-span-col">
                                                $leftColumnHeaderHtml
                                                <div class="spanning-col-body" style="
                                                    color: $readableTextColor;
                                                    font-size: ${fontSizePx}px;
                                                    font-weight: $fontWeight;
                                                    font-style: $fontStyle;
                                                    text-decoration: $textDecorationCss;
                                                    line-height: $lineHeightRatio;
                                                    font-family: $fontCssFamily;
                                                ">$leftContentHtml</div>

                                                <div class="spanning-leaf-footer">
                                                    <span class="leaf-page-num">— $leftPageBn —</span>
                                                </div>
                                            </div>

                                            <!-- Divider Line Between Columns -->
                                            <div class="spread-gutter-divider"></div>

                                            <!-- Column 2 (Right Part: 36th Line Onwards + Author Signature) -->
                                            <div class="spanning-col right-span-col">
                                                <div class="spanning-col-body" style="
                                                    color: $readableTextColor;
                                                    font-size: ${fontSizePx}px;
                                                    font-weight: $fontWeight;
                                                    font-style: $fontStyle;
                                                    text-decoration: $textDecorationCss;
                                                    line-height: $lineHeightRatio;
                                                    font-family: $fontCssFamily;
                                                ">$rightContentHtml</div>

                                                <!-- Author Signature placed immediately at end of poem in Column 2 -->
                                                $authorSignatureInRightColumn

                                                <div class="spanning-leaf-footer">
                                                    <span class="leaf-page-num">— $rightPageBn —</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    """.trimIndent()
                }

                is PdfPageItem.SingleNoteStandalone -> {
                    val note = pageItem.note
                    val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
                    val noteDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.createdAt))
                    val updatedDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.updatedAt))

                    val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)
                    val fontCssFamily = "'${fontOption.key}', 'Tiro Bangla', serif, sans-serif"

                    val readableTitleColor = ensureReadableTextColor(note.titleColorHex, defaultColorHex = "#B8860B")
                    val readableTextColor = ensureReadableTextColor(note.textColorHex, defaultColorHex = "#2C2C3A")

                    val textDecorations = mutableListOf<String>()
                    if (note.isUnderline) textDecorations.add("underline")
                    if (note.isStrikethrough) textDecorations.add("line-through")
                    val textDecorationCss = if (textDecorations.isNotEmpty()) textDecorations.joinToString(" ") else "none"

                    val fontWeight = if (note.isBold) "bold" else "normal"
                    val fontStyle = if (note.isItalic) "italic" else "normal"

                    val (fontSizePx, lineHeightRatio) = calculateColumnTypography(note.content, note.fontSizeSp, note.lineSpacingMultiplier)

                    val safeTitle = escapeHtml(title)
                    val leftLinesHtml = escapeHtml(pageItem.leftLines)
                    val rightLinesHtml = escapeHtml(pageItem.rightLines)
                    val authorHtml = renderAuthorSignature(fontCssFamily)

                    val headerHtml = """
                    <div class="single-export-header">
                        <h2 class="single-export-title" style="color: $readableTitleColor; font-family: $fontCssFamily;">$safeTitle</h2>
                        <div class="leaf-divider">
                            <span class="leaf-divider-line"></span>
                            <span class="leaf-symbol">❦</span>
                            <span class="leaf-divider-line"></span>
                        </div>
                        <div class="leaf-meta">
                            <span>রচনাকাল: $noteDate</span>
                            ${if (note.updatedAt > note.createdAt + 60000) " • <span>সম্পাদিত: $updatedDate</span>" else ""}
                        </div>
                    </div>
                    """.trimIndent()

                    val bodyContentHtml = if (pageItem.isOverflowing) {
                        """
                        <div class="single-note-columns-wrapper">
                            <!-- Column 1: Header + First 35 Lines -->
                            <div class="single-note-col single-note-left-col">
                                $headerHtml
                                <div class="single-note-col-body" style="
                                    color: $readableTextColor;
                                    font-size: ${fontSizePx}px;
                                    font-weight: $fontWeight;
                                    font-style: $fontStyle;
                                    text-decoration: $textDecorationCss;
                                    line-height: $lineHeightRatio;
                                    font-family: $fontCssFamily;
                                ">$leftLinesHtml</div>
                            </div>

                            <!-- Divider between Column 1 & Overflow Column 2 -->
                            <div class="spread-gutter-divider"></div>

                            <!-- Column 2: 36th Line Onwards + Author Signature -->
                            <div class="single-note-col single-note-right-col">
                                <div class="single-note-col-body" style="
                                    color: $readableTextColor;
                                    font-size: ${fontSizePx}px;
                                    font-weight: $fontWeight;
                                    font-style: $fontStyle;
                                    text-decoration: $textDecorationCss;
                                    line-height: $lineHeightRatio;
                                    font-family: $fontCssFamily;
                                ">$rightLinesHtml</div>
                                $authorHtml
                            </div>
                        </div>
                        """.trimIndent()
                    } else {
                        """
                        <div class="single-note-columns-wrapper">
                            <!-- Column 1: Header + Entire Poem (<= 35 lines) + Author Signature -->
                            <div class="single-note-col single-note-left-col">
                                $headerHtml
                                <div class="single-note-col-body" style="
                                    color: $readableTextColor;
                                    font-size: ${fontSizePx}px;
                                    font-weight: $fontWeight;
                                    font-style: $fontStyle;
                                    text-decoration: $textDecorationCss;
                                    line-height: $lineHeightRatio;
                                    font-family: $fontCssFamily;
                                ">$leftLinesHtml</div>
                                $authorHtml
                            </div>

                            <!-- Empty Column 2 slot to preserve layout balance -->
                            <div class="single-note-col single-note-empty-col"></div>
                        </div>
                        """.trimIndent()
                    }

                    """
                    <div class="landscape-page single-note-page $pageBreakClass">
                        <div class="page-outer-frame">
                            <div class="page-inner-frame">
                                <span class="corner-ornament corner-tl">❖</span>
                                <span class="corner-ornament corner-tr">❖</span>
                                <span class="corner-ornament corner-bl">❖</span>
                                <span class="corner-ornament corner-br">❖</span>

                                <!-- Top Header -->
                                <div class="page-top-header">
                                    <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                                    <span class="header-center">✦ ❦ ✦</span>
                                    <span class="header-right">$exportDate</span>
                                </div>
                                <div class="header-divider-line"></div>

                                <!-- Unified Outer Frame for Single Note -->
                                <div class="single-note-outer-container">
                                    <div class="single-note-unified-frame">
                                        $bodyContentHtml
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    """.trimIndent()
                }
            }
        }.joinToString("\n")

        // ================= 4. ENDING PAGE (A4 LANDSCAPE - 1 COLUMN CONTENT + 1 COLUMN EMPTY) =================
        val endPageHtml = if (!isSingleNote) {
            """
            <div class="landscape-page end-page page-break">
                <div class="page-outer-frame">
                    <div class="page-inner-frame">
                        <span class="corner-ornament corner-tl">❖</span>
                        <span class="corner-ornament corner-tr">❖</span>
                        <span class="corner-ornament corner-bl">❖</span>
                        <span class="corner-ornament corner-br">❖</span>

                        <!-- Top Header -->
                        <div class="page-top-header">
                            <span class="header-left">কাব্যলোকের ব্রহ্মকবি</span>
                            <span class="header-center">✦ ❦ ✦</span>
                            <span class="header-right">$exportDate</span>
                        </div>
                        <div class="header-divider-line"></div>

                        <!-- 2-Column Container: Left has End Card, Right is Empty -->
                        <div class="two-column-container end-columns-container">
                            <div class="column-slot end-left-slot">
                                <div class="end-card">
                                    <div class="end-card-inner">
                                        <span class="corner-ornament corner-tl">❖</span>
                                        <span class="corner-ornament corner-tr">❖</span>
                                        <span class="corner-ornament corner-bl">❖</span>
                                        <span class="corner-ornament corner-br">❖</span>

                                        <div class="end-symbol">❖ ❦ ❖</div>
                                        <h2 class="end-title">সমাপ্তি</h2>
                                        <div class="end-line"></div>
                                        <p class="end-text">কাব্যলোকের ব্রহ্মকবি দিয়ে তৈরি • সাহিত্য সংকলন</p>
                                        ${if (formattedAuthorText.isNotBlank()) "<p class=\"end-author\">${escapeHtml(formattedAuthorText)}</p>" else ""}
                                    </div>
                                </div>
                            </div>
                            <div class="column-slot end-right-empty-slot"></div>
                        </div>

                        <!-- Page Bottom Footer -->
                        <div class="page-bottom-footer">
                            <div class="footer-divider"></div>
                            <div class="footer-row">
                                <span class="footer-app-name">কাব্যলোকের ব্রহ্মকবি</span>
                                <span class="footer-page-num">— সমাপ্তি পাতা —</span>
                                <span class="footer-app-name">সাহিত্য সংকলন</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent()
        } else ""

        return """
        <!DOCTYPE html>
        <html lang="bn">
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=1123, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                $fontFaceRules

                @page {
                    size: A4 landscape;
                    margin: 0;
                }

                * {
                    box-sizing: border-box !important;
                    -webkit-print-color-adjust: exact !important;
                    print-color-adjust: exact !important;
                }

                html, body {
                    width: 1123px;
                    margin: 0;
                    padding: 0;
                    background-color: #FAF9F4; /* Warm ivory book paper */
                    font-family: 'anupam_mahdi', 'Tiro Bangla', serif, sans-serif;
                    color: #2C2C3A;
                    -webkit-font-smoothing: antialiased;
                }

                .page-break {
                    page-break-before: always !important;
                    break-before: page !important;
                }

                /* ================= OUTER DECORATIVE BORDER SYSTEM ================= */
                .landscape-page {
                    width: 1123px;
                    height: 794px;
                    min-height: 794px;
                    padding: 22px 28px;
                    position: relative;
                    background-color: #FAF9F4;
                    box-sizing: border-box;
                    display: flex;
                    flex-direction: column;
                }

                .page-outer-frame {
                    width: 100%;
                    height: 100%;
                    border: 2px solid #D4A017;
                    padding: 6px;
                    box-sizing: border-box;
                    background: transparent;
                }

                .page-inner-frame {
                    width: 100%;
                    height: 100%;
                    border: 1px dashed rgba(184, 134, 11, 0.45);
                    padding: 16px 22px;
                    position: relative;
                    box-sizing: border-box;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    background: radial-gradient(circle at center, #FFFDF8 0%, #FAF9F4 100%);
                }

                /* Corner Ornaments (❖) */
                .corner-ornament {
                    position: absolute;
                    color: #D4A017;
                    font-size: 13px;
                    line-height: 1;
                    user-select: none;
                }
                .corner-tl { top: 5px; left: 6px; }
                .corner-tr { top: 5px; right: 6px; }
                .corner-bl { bottom: 5px; left: 6px; }
                .corner-br { bottom: 5px; right: 6px; }

                /* Full-width Top Header */
                .page-top-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    font-size: 11px;
                    color: #8C8C9E;
                    width: 100%;
                    padding-bottom: 2px;
                    box-sizing: border-box;
                }
                .header-left, .header-right {
                    color: #7A7A8E;
                    font-weight: 500;
                }
                .header-center {
                    color: #B8860B;
                    font-size: 11px;
                    letter-spacing: 2px;
                }
                .header-divider-line {
                    height: 1px;
                    width: 100%;
                    background: linear-gradient(to right, transparent, #D4A017 15%, #D4A017 85%, transparent);
                    opacity: 0.5;
                    margin-bottom: 10px;
                }

                /* ================= 2-COLUMN CARDS CONTAINER ================= */
                .two-column-container {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;
                    align-items: stretch;
                    gap: 22px;
                    flex-grow: 1;
                    width: 100%;
                    min-height: 0;
                    margin-bottom: 4px;
                    box-sizing: border-box;
                }

                .column-slot {
                    flex: 1;
                    display: flex;
                    min-width: 0;
                    box-sizing: border-box;
                }

                /* Individual Poem Card Frame (Distinct Book Leaf Card) */
                .column-leaf-card {
                    width: 100%;
                    box-sizing: border-box;
                    border: 1px solid rgba(212, 160, 23, 0.45);
                    border-radius: 10px;
                    background: rgba(255, 255, 255, 0.65);
                    box-shadow: 0 1px 4px rgba(184, 134, 11, 0.04);
                    padding: 14px 18px 8px 18px;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    overflow: hidden;
                    text-align: center;
                }

                .column-leaf-empty {
                    width: 100%;
                    height: 100%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border: 1px dashed rgba(212, 160, 23, 0.3);
                    border-radius: 10px;
                    background: rgba(255, 255, 255, 0.2);
                }
                .empty-column-motif {
                    color: rgba(184, 134, 11, 0.3);
                    font-size: 20px;
                    letter-spacing: 4px;
                }

                /* Leaf Card Header, Body, Footer */
                .leaf-header {
                    width: 100%;
                    margin-bottom: 6px;
                    text-align: center !important;
                    box-sizing: border-box;
                }
                .leaf-poem-title {
                    width: 100%;
                    font-size: 19px;
                    margin: 0 0 4px 0;
                    font-weight: bold;
                    line-height: 1.3;
                    text-align: center !important;
                    box-sizing: border-box;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                .leaf-divider {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 3px 0 5px 0;
                    width: 100%;
                }
                .leaf-divider-line {
                    height: 1px;
                    width: 36px;
                    background-color: #D4A017;
                    opacity: 0.6;
                }
                .leaf-symbol {
                    color: #B8860B;
                    font-size: 11px;
                    margin: 0 6px;
                }
                .leaf-meta {
                    width: 100%;
                    font-size: 9.5px;
                    color: #7A7A8E;
                    font-style: italic;
                    text-align: center !important;
                    box-sizing: border-box;
                }

                .leaf-body {
                    width: 100%;
                    box-sizing: border-box;
                    display: block;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                    text-align: center !important;
                    padding-bottom: 4px;
                    margin: auto 0;
                    flex-grow: 1;
                }

                .leaf-footer, .spanning-leaf-footer {
                    width: 100%;
                    text-align: center;
                    margin-top: 6px;
                    padding-top: 4px;
                    border-top: 1px dotted rgba(212, 160, 23, 0.3);
                    box-sizing: border-box;
                }
                .leaf-page-num {
                    font-weight: bold;
                    color: #B8860B;
                    font-size: 11.5px;
                    letter-spacing: 1px;
                }

                /* ================= FULL-WIDTH POEM HEADER (FOR SPANNING POEMS) ================= */
                .fullwidth-poem-header {
                    width: 100%;
                    margin-bottom: 10px;
                    text-align: center !important;
                    box-sizing: border-box;
                }
                .fullwidth-poem-title {
                    width: 100%;
                    font-size: 23px;
                    margin: 0 0 4px 0;
                    font-weight: bold;
                    line-height: 1.3;
                    text-align: center !important;
                    box-sizing: border-box;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }

                /* ================= SPANNING POEM CONTAINER (2 DISTINCT COLUMNS UNDER 1 FULL-WIDTH HEADER) ================= */
                .spanning-poem-container {
                    width: 100%;
                    flex-grow: 1;
                    display: flex;
                    min-height: 0;
                    margin-bottom: 4px;
                    box-sizing: border-box;
                }
                .spanning-card {
                    width: 100%;
                    border: 1.5px solid rgba(212, 160, 23, 0.55);
                    border-radius: 12px;
                    background: rgba(255, 255, 255, 0.7);
                    padding: 14px 20px 8px 20px;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    box-sizing: border-box;
                }
                .spanning-columns-wrapper {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;
                    align-items: stretch;
                    width: 100%;
                    flex-grow: 1;
                    min-height: 0;
                    box-sizing: border-box;
                }
                .spanning-col {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    padding: 0 16px;
                    box-sizing: border-box;
                }
                .spread-gutter-divider {
                    width: 1px;
                    background: linear-gradient(to bottom, transparent, rgba(212, 160, 23, 0.4) 15%, rgba(212, 160, 23, 0.4) 85%, transparent);
                    margin: 0 4px;
                }
                .spanning-col-body {
                    width: 100%;
                    box-sizing: border-box;
                    display: block;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                    text-align: center !important;
                    margin: auto 0;
                    flex-grow: 1;
                }

                /* ================= SINGLE NOTE CONTAINER & STYLING ================= */
                .single-note-outer-container {
                    width: 100%;
                    flex-grow: 1;
                    display: flex;
                    min-height: 0;
                    margin-bottom: 4px;
                    box-sizing: border-box;
                }
                .single-note-unified-frame {
                    width: 100%;
                    border: 1.5px solid rgba(212, 160, 23, 0.55);
                    border-radius: 12px;
                    background: rgba(255, 255, 255, 0.7);
                    padding: 14px 20px 8px 20px;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    box-sizing: border-box;
                }
                .single-note-columns-wrapper {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;
                    align-items: stretch;
                    width: 100%;
                    flex-grow: 1;
                    min-height: 0;
                    box-sizing: border-box;
                }
                .single-note-col {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    padding: 0 16px;
                    box-sizing: border-box;
                    min-height: 0;
                }
                .single-note-left-col {
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                }
                .single-note-right-col {
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                }
                .single-note-empty-col {
                    /* Left intentionally blank when poem <= 24 lines */
                    visibility: hidden;
                }
                .single-export-header {
                    width: 100%;
                    margin-bottom: 8px;
                    text-align: center !important;
                    box-sizing: border-box;
                }
                .single-export-title {
                    width: 100%;
                    font-size: 20px;
                    margin: 0 0 4px 0;
                    font-weight: bold;
                    line-height: 1.3;
                    text-align: center !important;
                    box-sizing: border-box;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                .single-note-col-body {
                    width: 100%;
                    box-sizing: border-box;
                    display: block;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                    text-align: center !important;
                    margin: auto 0;
                    flex-grow: 1;
                }

                /* ================= POEM TYPOGRAPHY (ALL CENTER ALIGNED) ================= */
                .poem-header {
                    width: 100%;
                    margin-bottom: 8px;
                    text-align: center !important;
                    box-sizing: border-box;
                }
                .poem-title {
                    width: 100%;
                    font-size: 20px;
                    margin: 0 0 4px 0;
                    font-weight: bold;
                    line-height: 1.3;
                    text-align: center !important;
                    box-sizing: border-box;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                .spanning-card .poem-title, .single-card .poem-title {
                    font-size: 24px;
                }
                .poem-divider {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 4px 0 6px 0;
                    width: 100%;
                }
                .poem-divider-line {
                    height: 1px;
                    width: 40px;
                    background-color: #D4A017;
                    opacity: 0.6;
                }
                .poem-symbol {
                    color: #B8860B;
                    font-size: 11px;
                    margin: 0 6px;
                }
                .poem-meta {
                    width: 100%;
                    font-size: 10px;
                    color: #7A7A8E;
                    font-style: italic;
                    text-align: center !important;
                    box-sizing: border-box;
                }

                /* Poem Body (Center-Aligned) */
                .poem-body {
                    width: 100%;
                    box-sizing: border-box;
                    display: block;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                    text-align: center !important;
                    padding-bottom: 4px;
                    margin: auto 0;
                }

                /* Author Section (Center-Aligned) */
                .poem-author-section {
                    width: 100%;
                    margin-top: 8px;
                    margin-bottom: 4px;
                    text-align: center !important;
                    box-sizing: border-box;
                    page-break-inside: avoid !important;
                    break-inside: avoid !important;
                }
                .author-divider-line {
                    height: 1px;
                    width: 30%;
                    max-width: 140px;
                    min-width: 60px;
                    background-color: #D4A017;
                    opacity: 0.45;
                    margin: 0 auto 6px auto !important;
                }
                .author-signature-text {
                    font-size: 10.5pt;
                    font-style: italic;
                    font-weight: 500;
                    color: #6C6C7E;
                    line-height: 1.4;
                    text-align: center !important;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }

                /* Full-width Bottom Footer */
                .page-bottom-footer {
                    width: 100%;
                    margin-top: auto;
                    box-sizing: border-box;
                }
                .footer-divider {
                    height: 1px;
                    background: linear-gradient(to right, transparent, #E2DDD0 15%, #E2DDD0 85%, transparent);
                    margin-bottom: 6px;
                }
                .footer-row {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    font-size: 11px;
                    color: #8C8C9E;
                    width: 100%;
                }
                .footer-page-num {
                    font-weight: bold;
                    color: #B8860B;
                }

                /* ================= COVER PAGE STYLES (1-COLUMN CONTENT + 1-COLUMN EMPTY) ================= */
                .cover-columns-container {
                    flex-grow: 1;
                    width: 100%;
                    height: 100%;
                    min-height: 0;
                }
                .cover-left-slot {
                    flex: 1;
                    height: 100%;
                    display: flex;
                    flex-direction: column;
                }
                .cover-right-empty-slot {
                    flex: 1;
                    height: 100%;
                }
                .cover-card {
                    width: 100%;
                    height: 100%;
                    border: 1.5px solid #D4A017;
                    padding: 5px;
                    box-sizing: border-box;
                    background-color: transparent;
                }
                .cover-card-inner {
                    border: 1px dashed #B8860B;
                    padding: 16px 20px;
                    width: 100%;
                    height: 100%;
                    position: relative;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: space-between;
                    text-align: center;
                    background: radial-gradient(circle at center, #FFFDF8 0%, #FAF9F4 100%);
                    box-sizing: border-box;
                }
                .cover-top-ornament, .cover-bottom-ornament {
                    color: #B8860B;
                    font-size: 14px;
                    letter-spacing: 4px;
                    margin: 2px 0;
                }
                .cover-badge {
                    width: 42px;
                    height: 42px;
                    border-radius: 50%;
                    background-color: #F3ECE0;
                    border: 1.5px solid #D4A017;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 4px 0;
                }
                .cover-icon {
                    font-size: 20px;
                    color: #B8860B;
                }
                .cover-title {
                    font-size: 24px;
                    color: #B8860B;
                    font-family: 'anupam_mahdi', serif;
                    margin: 0 0 2px 0;
                    line-height: 1.25;
                }
                .cover-subtitle {
                    font-size: 13px;
                    color: #665A48;
                    font-style: italic;
                    margin-bottom: 6px;
                }
                .cover-divider {
                    color: #D4A017;
                    font-size: 13px;
                    margin-bottom: 8px;
                }
                .cover-info {
                    margin-bottom: 8px;
                }
                .cover-info-label {
                    font-size: 13px;
                    font-weight: bold;
                    color: #2C2C3A;
                    margin-bottom: 4px;
                    letter-spacing: 0.5px;
                }
                .cover-date {
                    font-size: 12px;
                    color: #6C6C7E;
                    margin-bottom: 2px;
                }
                .cover-count {
                    font-size: 12px;
                    color: #8A8A9B;
                    margin-bottom: 2px;
                }
                .cover-author {
                    font-size: 12px;
                    color: #7A7A8E;
                    font-style: italic;
                    margin-top: 3px;
                }
                .cover-footer-text {
                    font-size: 11px;
                    color: #B8860B;
                    font-style: italic;
                    margin-top: 3px;
                }

                /* ================= TABLE OF CONTENTS STYLES (2-COLUMN SERIAL FLOW) ================= */
                .toc-header {
                    text-align: center;
                    margin-bottom: 8px;
                    box-sizing: border-box;
                }
                .toc-ornament {
                    color: #B8860B;
                    font-size: 13px;
                    letter-spacing: 3px;
                    margin-bottom: 2px;
                }
                .toc-title {
                    font-size: 22px;
                    color: #B8860B;
                    margin: 0 0 2px 0;
                    font-family: 'anupam_mahdi', serif;
                }
                .toc-subtitle {
                    font-size: 11.5px;
                    color: #7A7A8E;
                    font-style: italic;
                    margin-bottom: 4px;
                }
                .toc-line {
                    height: 1.5px;
                    background: linear-gradient(to right, transparent, #D4A017, transparent);
                    width: 40%;
                    margin: 0 auto;
                }
                .toc-columns-container {
                    display: flex;
                    flex-direction: row;
                    justify-content: space-between;
                    align-items: stretch;
                    width: 100%;
                    flex-grow: 1;
                    gap: 16px;
                    box-sizing: border-box;
                    min-height: 0;
                    padding: 4px 6px;
                }
                .toc-col {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                    justify-content: flex-start;
                    box-sizing: border-box;
                }
                .toc-gutter-divider {
                    width: 1px;
                    background: linear-gradient(to bottom, transparent, rgba(212, 160, 23, 0.45) 15%, rgba(212, 160, 23, 0.45) 85%, transparent);
                    margin: 0 2px;
                }
                .toc-item {
                    display: flex;
                    align-items: baseline;
                    font-size: 12.5px;
                    width: 100%;
                    box-sizing: border-box;
                }
                .toc-item-title {
                    color: #2C2C3A;
                    font-weight: 500;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    max-width: 320px;
                }
                .toc-leader {
                    flex-grow: 1;
                    border-bottom: 1px dotted #C8C4B7;
                    margin: 0 6px;
                    height: 1em;
                }
                .toc-item-page {
                    color: #B8860B;
                    font-weight: bold;
                    white-space: nowrap;
                    font-size: 12px;
                }

                /* ================= ENDING PAGE STYLES (1-COLUMN CONTENT + 1-COLUMN EMPTY) ================= */
                .end-columns-container {
                    flex-grow: 1;
                    width: 100%;
                    height: 100%;
                    min-height: 0;
                }
                .end-left-slot {
                    flex: 1;
                    height: 100%;
                    display: flex;
                    flex-direction: column;
                }
                .end-right-empty-slot {
                    flex: 1;
                    height: 100%;
                }
                .end-card {
                    width: 100%;
                    height: 100%;
                    border: 1.5px solid #D4A017;
                    padding: 5px;
                    box-sizing: border-box;
                    background-color: transparent;
                }
                .end-card-inner {
                    border: 1px dashed #B8860B;
                    padding: 24px;
                    width: 100%;
                    height: 100%;
                    position: relative;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    text-align: center;
                    background: radial-gradient(circle at center, #FFFDF8 0%, #FAF9F4 100%);
                    box-sizing: border-box;
                }
                .end-symbol {
                    color: #B8860B;
                    font-size: 18px;
                    margin-bottom: 10px;
                    letter-spacing: 4px;
                }
                .end-title {
                    font-size: 26px;
                    color: #B8860B;
                    font-family: 'anupam_mahdi', serif;
                    margin: 0 0 8px 0;
                }
                .end-line {
                    height: 1.5px;
                    width: 80px;
                    background-color: #D4A017;
                    margin: 0 auto 14px auto;
                }
                .end-text {
                    font-size: 13px;
                    color: #7A7A8E;
                    font-style: italic;
                    margin-bottom: 6px;
                }
                .end-author {
                    font-size: 12px;
                    color: #8C8C9E;
                    font-style: italic;
                }
            </style>
        </head>
        <body>
            $coverPageHtml
            $tocPageHtml
            $pagesHtml
            $endPageHtml
        </body>
        </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    /**
     * Renders HTML to PDF using Android WebView and PdfDocument.
     * Guaranteed to enable slow whole document draw, wait for onPageFinished(), and finalize streams safely.
     */
    fun exportToPdf(
        context: Context,
        htmlContent: String,
        outputFile: File,
        onSuccess: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Handler(Looper.getMainLooper()).post {
            try {
                Log.d(TAG, "Starting PDF generation via PrintDocumentAdapter to temp file: ${outputFile.absolutePath}")

                val webView = WebView(context)
                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    defaultTextEncodingName = "utf-8"
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }

                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d(TAG, "WebView onPageFinished triggered")

                        // 600ms delay to allow asset fonts and CSS to fully render
                        Handler(Looper.getMainLooper()).postDelayed({
                            try {
                                val printAdapter = webView.createPrintDocumentAdapter("Kabboloker_Brakkhakbi_Document")
                                android.print.PdfPrintHelper.generatePdfFromAdapter(
                                    printAdapter,
                                    outputFile,
                                    object : android.print.PdfPrintHelper.PdfPrintCallback {
                                        override fun onSuccess(file: File) {
                                            Log.d(TAG, "PdfPrintHelper PDF export successful. File size: ${file.length()} bytes")
                                            onSuccess(file)
                                        }

                                        override fun onError(e: Exception) {
                                            Log.e(TAG, "PdfPrintHelper PDF export failed", e)
                                            onError(e)
                                        }
                                    }
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error rendering PDF via PrintDocumentAdapter", e)
                                onError(Exception("PDF তৈরি করতে ব্যর্থ: ${e.localizedMessage}"))
                            }
                        }, 600)
                    }
                }

                Log.d(TAG, "Loading HTML into WebView with base URL file:///android_asset/ ...")
                webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing WebView for PDF export", e)
                onError(Exception("PDF তৈরি করতে সমস্যা হয়েছে: ${e.localizedMessage}"))
            }
        }
    }

    /**
     * Renders HTML to PDF directly into a target Uri chosen via Storage Access Framework (SAF).
     */
    fun exportToPdfToUri(
        context: Context,
        htmlContent: String,
        targetUri: Uri,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val tempFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
        Log.d(TAG, "exportToPdfToUri called with targetUri: $targetUri, tempFile: ${tempFile.absolutePath}")
        
        exportToPdf(
            context = context,
            htmlContent = htmlContent,
            outputFile = tempFile,
            onSuccess = { cacheFile ->
                try {
                    Log.d(TAG, "Copying cacheFile (${cacheFile.length()} bytes) to targetUri: $targetUri")
                    context.contentResolver.openOutputStream(targetUri, "w")?.use { out ->
                        cacheFile.inputStream().use { input ->
                            input.copyTo(out)
                        }
                        out.flush()
                    } ?: throw Exception("Output stream open করতে পারা যায়নি")
                    
                    try { cacheFile.delete() } catch (e: Exception) {}
                    Log.d(TAG, "Successfully written PDF to targetUri")
                    onSuccess()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to copy PDF to targetUri", e)
                    try { cacheFile.delete() } catch (ex: Exception) {}
                    onError(Exception("PDF ফাইলে সংরক্ষণ ব্যর্থ হয়েছে: ${e.localizedMessage}"))
                }
            },
            onError = { e ->
                try { tempFile.delete() } catch (ex: Exception) {}
                onError(e)
            }
        )
    }
}

