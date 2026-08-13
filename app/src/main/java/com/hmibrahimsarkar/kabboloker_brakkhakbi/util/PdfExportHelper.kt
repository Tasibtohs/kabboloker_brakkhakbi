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
     * Builds HTML template styled like a real published book or poetry collection.
     */
    fun buildHtmlForNotes(
        notes: List<NoteEntity>,
        isSingleNote: Boolean,
        authorName: String = "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি"
    ): String {
        Log.d(TAG, "Building HTML for notes. Count: ${notes.size}, isSingleNote: $isSingleNote, author: $authorName")
        val exportDate = SimpleDateFormat("dd MMMM, yyyy", Locale("bn", "BD")).format(Date())
        
        // CSS @font-face definitions for all 20 local Bengali fonts from assets
        val fontFaceRules = BengaliFonts.fonts.joinToString("\n") { fontOption ->
            """
            @font-face {
                font-family: '${fontOption.key}';
                src: url('fonts/${fontOption.assetFileName}');
            }
            """.trimIndent()
        }

        // 1. Cover Page (Only shown for multi-note book collection export)
        val coverPageHtml = if (!isSingleNote) {
            """
            <div class="cover-page page-break">
                <div class="cover-border">
                    <div class="cover-inner-border">
                        <div class="cover-top-ornament">❖ ❦ ❖</div>
                        <div class="cover-badge">
                            <span class="cover-icon">✒</span>
                        </div>
                        <h1 class="cover-title">কাব্যলোকের ব্রহ্মকবি</h1>
                        <div class="cover-subtitle">~ কবি ও শব্দের আসর ~</div>
                        <div class="cover-divider">✦ ❦ ✦</div>
                        <div class="cover-info">
                            <div class="cover-info-label">কবিতা ও সাহিত্য সংকলন</div>
                            <div class="cover-date">সংকলনের তারিখ: $exportDate</div>
                            <div class="cover-count">মোট কবিতা: ${notes.size} টি</div>
                        </div>
                        <div class="cover-footer-text">একটি অনবদ্য সাহিত্য সৃষ্টি</div>
                        <div class="cover-bottom-ornament">❖ ❦ ❖</div>
                    </div>
                </div>
            </div>
            """.trimIndent()
        } else ""

        // 2. Table of Contents Page (Shown if multiple notes in collection)
        val tocPageHtml = if (!isSingleNote && notes.size > 1) {
            val tocItems = notes.mapIndexed { index, note ->
                val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
                val pageNum = 3 + index
                """
                <div class="toc-item">
                    <span class="toc-item-title">${index + 1}. ${escapeHtml(title)}</span>
                    <span class="toc-leader"></span>
                    <span class="toc-item-page">$pageNum</span>
                </div>
                """.trimIndent()
            }.joinToString("\n")

            """
            <div class="toc-page page-break">
                <div class="toc-container">
                    <div class="toc-header">
                        <div class="toc-ornament">❖</div>
                        <h2 class="toc-title">সূচিপত্র</h2>
                        <div class="toc-subtitle">কাব্য সংকলন সূচী</div>
                        <div class="toc-line"></div>
                    </div>
                    <div class="toc-list">
                        $tocItems
                    </div>
                    <div class="toc-footer">
                        <span>কাব্যলোকের ব্রহ্মকবি</span>
                    </div>
                </div>
            </div>
            """.trimIndent()
        } else ""

        // 3. Poem Pages HTML (With try-catch per note)
        val notesHtml = notes.mapIndexed { index, note ->
            try {
                val title = if (note.title.isNotBlank()) note.title else "শিরোনামহীন কবিতা"
                val content = if (note.content.isNotBlank()) note.content else "(ফাঁকা কবিতা)"
                
                val noteDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.createdAt))
                val updatedDate = SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale("bn", "BD")).format(Date(note.updatedAt))
                
                val fontOption = BengaliFonts.getFontByKey(note.fontFamilyKey)
                val fontCssFamily = "'${fontOption.key}', 'Tiro Bangla', serif, sans-serif"

                val readableTitleColor = ensureReadableTextColor(note.titleColorHex, defaultColorHex = "#B8860B")
                val readableTextColor = ensureReadableTextColor(note.textColorHex, defaultColorHex = "#2C2C3A")
                val alignment = note.textAlign.lowercase()
                
                val textDecorations = mutableListOf<String>()
                if (note.isUnderline) textDecorations.add("underline")
                if (note.isStrikethrough) textDecorations.add("line-through")
                val textDecorationCss = if (textDecorations.isNotEmpty()) textDecorations.joinToString(" ") else "none"
                
                val fontWeight = if (note.isBold) "bold" else "normal"
                val fontStyle = if (note.isItalic) "italic" else "normal"
                
                // Dynamic font size and line-spacing based on poem line count and length to fit within a single page
                val rawLines = content.lines()
                val lineCount = rawLines.size
                val charCount = content.length

                val (fontSizePx, lineHeightRatio) = when {
                    lineCount > 35 || charCount > 1000 -> Pair(16.0f, 1.4f)
                    lineCount > 22 || charCount > 500  -> Pair(18.0f, 1.45f)
                    lineCount > 14 || charCount > 300  -> Pair(20.0f, 1.5f)
                    else -> {
                        val userSp = note.fontSizeSp.coerceIn(14f, 22f)
                        val fPx = (userSp * 1.25f).coerceIn(20.0f, 24.0f)
                        val lRatio = note.lineSpacingMultiplier.coerceIn(1.45f, 1.6f)
                        Pair(fPx, lRatio)
                    }
                }

                val safeTitle = escapeHtml(title)
                val safeContent = escapeHtml(content)

                val displayPageNum = if (isSingleNote) "১" else "${3 + index}"

                val isPageBreakNeeded = !(isSingleNote && index == 0)

                val formattedAuthorText = when {
                    authorName.isBlank() -> ""
                    authorName.contains("কাব্যলোকের") -> "— লেখক: $authorName"
                    else -> "— লেখক: $authorName – কাব্যলোকের ব্রহ্মকবি"
                }

                val authorSignatureHtml = if (formattedAuthorText.isNotBlank()) {
                    val dividerMargin = when (alignment) {
                        "center" -> "margin: 0 auto 8px auto;"
                        "right" -> "margin: 0 0 8px auto;"
                        else -> "margin: 0 auto 8px 0;"
                    }
                    """
                    <div class="poem-author-section" style="text-align: $alignment;">
                        <div class="author-divider-line" style="$dividerMargin"></div>
                        <div class="author-signature-text" style="font-family: $fontCssFamily; text-align: $alignment;">${escapeHtml(formattedAuthorText)}</div>
                    </div>
                    """.trimIndent()
                } else ""

                """
                <div class="note-page ${if (isPageBreakNeeded) "page-break" else ""}">
                    <div class="page-top-ornament">
                        <span class="top-line"></span>
                        <span class="top-symbol">✦ ❦ ✦</span>
                        <span class="top-line"></span>
                    </div>
                    
                    <div class="poem-header" style="text-align: $alignment;">
                        <h1 class="poem-title" style="color: $readableTitleColor; font-family: $fontCssFamily;">$safeTitle</h1>
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

                    <div class="poem-body" style="
                        color: $readableTextColor;
                        font-size: ${fontSizePx}px;
                        font-weight: $fontWeight;
                        font-style: $fontStyle;
                        text-decoration: $textDecorationCss;
                        text-align: $alignment;
                        line-height: $lineHeightRatio;
                        font-family: $fontCssFamily;
                    ">$safeContent</div>

                    $authorSignatureHtml

                    <div class="page-bottom-footer">
                        <div class="footer-divider"></div>
                        <div class="footer-row">
                            <span class="footer-app-name">কাব্যলোকের ব্রহ্মকবি</span>
                            <span class="footer-page-num">— $displayPageNum —</span>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            } catch (e: Exception) {
                Log.e(TAG, "Error building HTML for note index $index, id ${note.id}", e)
                """
                <div class="note-page page-break">
                    <h1 class="poem-title" style="color: #2C2C3A;">${escapeHtml(note.title.ifBlank { "নোট" })}</h1>
                    <div class="poem-body" style="color: #2C2C3A;">${escapeHtml(note.content)}</div>
                </div>
                """.trimIndent()
            }
        }.joinToString("\n")

        // 4. Ending Page HTML (Only for multi-note book collection)
        val endPageHtml = if (!isSingleNote) {
            """
            <div class="end-page page-break">
                <div class="end-container">
                    <div class="end-symbol">❖ ❦ ❖</div>
                    <h2 class="end-title">সমাপ্তি</h2>
                    <div class="end-line"></div>
                    <p class="end-text">কাব্যলোকের ব্রহ্মকবি দিয়ে তৈরি • সাহিত্য সংকলন</p>
                </div>
            </div>
            """.trimIndent()
        } else ""

        return """
        <!DOCTYPE html>
        <html lang="bn">
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=794, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                $fontFaceRules

                @page {
                    size: A4 portrait;
                    margin: 0;
                }

                * {
                    box-sizing: border-box !important;
                    -webkit-print-color-adjust: exact !important;
                    print-color-adjust: exact !important;
                }

                html, body {
                    width: 794px;
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

                /* ================= COVER PAGE STYLES ================= */
                .cover-page {
                    width: 794px;
                    min-height: 1123px;
                    padding: 40px;
                    background-color: #FAF9F4;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    box-sizing: border-box;
                }
                .cover-border {
                    border: 2px solid #D4A017;
                    padding: 10px;
                    width: 100%;
                    height: 100%;
                    min-height: 1023px;
                    box-sizing: border-box;
                }
                .cover-inner-border {
                    border: 1px dashed #B8860B;
                    padding: 50px 40px;
                    width: 100%;
                    height: 100%;
                    min-height: 1000px;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    text-align: center;
                    background: radial-gradient(circle at center, #FFFDF8 0%, #FAF9F4 100%);
                    box-sizing: border-box;
                }
                .cover-top-ornament, .cover-bottom-ornament {
                    color: #B8860B;
                    font-size: 18px;
                    letter-spacing: 4px;
                    margin: 20px 0;
                }
                .cover-badge {
                    width: 60px;
                    height: 60px;
                    border-radius: 50%;
                    background-color: #F3ECE0;
                    border: 1.5px solid #D4A017;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: 25px;
                }
                .cover-icon {
                    font-size: 30px;
                    color: #B8860B;
                }
                .cover-title {
                    font-size: 36px;
                    color: #B8860B;
                    font-family: 'anupam_mahdi', serif;
                    margin: 0 0 12px 0;
                    line-height: 1.3;
                }
                .cover-subtitle {
                    font-size: 18px;
                    color: #665A48;
                    font-style: italic;
                    margin-bottom: 30px;
                }
                .cover-divider {
                    color: #D4A017;
                    font-size: 18px;
                    margin-bottom: 40px;
                }
                .cover-info {
                    margin-bottom: 60px;
                }
                .cover-info-label {
                    font-size: 16px;
                    font-weight: bold;
                    color: #2C2C3A;
                    margin-bottom: 10px;
                    letter-spacing: 0.5px;
                }
                .cover-date {
                    font-size: 13px;
                    color: #6C6C7E;
                    margin-bottom: 6px;
                }
                .cover-count {
                    font-size: 13px;
                    color: #8A8A9B;
                }
                .cover-footer-text {
                    font-size: 12px;
                    color: #B8860B;
                    font-style: italic;
                    margin-top: auto;
                }

                /* ================= TABLE OF CONTENTS STYLES ================= */
                .toc-page {
                    width: 794px;
                    padding: 60px 70px;
                    min-height: 1123px;
                    background-color: #FAF9F4;
                    position: relative;
                    box-sizing: border-box;
                }
                .toc-container {
                    width: 100%;
                    box-sizing: border-box;
                }
                .toc-header {
                    text-align: center;
                    margin-bottom: 40px;
                }
                .toc-ornament {
                    color: #B8860B;
                    font-size: 18px;
                    margin-bottom: 8px;
                }
                .toc-title {
                    font-size: 30px;
                    color: #B8860B;
                    margin: 0 0 6px 0;
                    font-family: 'anupam_mahdi', serif;
                }
                .toc-subtitle {
                    font-size: 13px;
                    color: #7A7A8E;
                    font-style: italic;
                    margin-bottom: 15px;
                }
                .toc-line {
                    height: 1.5px;
                    background: linear-gradient(to right, transparent, #D4A017, transparent);
                    width: 60%;
                    margin: 0 auto;
                }
                .toc-list {
                    margin-bottom: 50px;
                    width: 100%;
                }
                .toc-item {
                    display: flex;
                    align-items: baseline;
                    margin-bottom: 14px;
                    font-size: 15px;
                    width: 100%;
                }
                .toc-item-title {
                    color: #2C2C3A;
                    font-weight: 500;
                    white-space: nowrap;
                }
                .toc-leader {
                    flex-grow: 1;
                    border-bottom: 1px dotted #C8C4B7;
                    margin: 0 10px;
                    height: 1em;
                }
                .toc-item-page {
                    color: #B8860B;
                    font-weight: bold;
                    white-space: nowrap;
                }
                .toc-footer {
                    position: absolute;
                    bottom: 40px;
                    left: 70px;
                    right: 70px;
                    text-align: center;
                    font-size: 12px;
                    color: #8C8C9E;
                    border-top: 1px solid #E5E0D3;
                    padding-top: 10px;
                    box-sizing: border-box;
                }

                /* ================= POEM PAGE STYLES ================= */
                .note-page {
                    width: 794px;
                    padding: 40px 55px 45px 55px;
                    min-height: 1123px;
                    position: relative;
                    background-color: #FAF9F4;
                    box-sizing: border-box;
                }
                .page-top-ornament {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-bottom: 16px;
                    width: 100%;
                }
                .top-line {
                    height: 1px;
                    width: 70px;
                    background-color: #D4A017;
                    opacity: 0.5;
                }
                .top-symbol {
                    color: #B8860B;
                    font-size: 11px;
                    margin: 0 10px;
                }
                .poem-header {
                    width: 100%;
                    margin-bottom: 16px;
                    box-sizing: border-box;
                }
                .poem-title {
                    width: 100%;
                    font-size: 28px;
                    margin: 0 0 8px 0;
                    font-weight: bold;
                    line-height: 1.35;
                    box-sizing: border-box;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                }
                .poem-divider {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin: 8px 0 10px 0;
                    width: 100%;
                }
                .poem-divider-line {
                    height: 1px;
                    width: 50px;
                    background-color: #D4A017;
                    opacity: 0.6;
                }
                .poem-symbol {
                    color: #B8860B;
                    font-size: 13px;
                    margin: 0 8px;
                }
                .poem-meta {
                    width: 100%;
                    font-size: 11px;
                    color: #7A7A8E;
                    font-style: italic;
                    box-sizing: border-box;
                }
                .poem-body {
                    width: 100%;
                    box-sizing: border-box;
                    display: block;
                    white-space: pre-wrap;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                    word-break: normal;
                    padding-bottom: 10px;
                }
                .poem-author-section {
                    width: 100%;
                    margin-top: 18px;
                    margin-bottom: 35px;
                    box-sizing: border-box;
                    page-break-inside: avoid !important;
                    break-inside: avoid !important;
                }
                .author-divider-line {
                    height: 1px;
                    width: 25%;
                    max-width: 160px;
                    min-width: 80px;
                    background-color: #D4A017;
                    opacity: 0.45;
                }
                .author-signature-text {
                    font-size: 11pt;
                    font-style: italic;
                    font-weight: 500;
                    color: #6C6C7E;
                    line-height: 1.45;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                .page-bottom-footer {
                    position: absolute;
                    bottom: 25px;
                    left: 55px;
                    right: 55px;
                    box-sizing: border-box;
                }
                .footer-divider {
                    height: 1px;
                    background-color: #E2DDD0;
                    margin-bottom: 10px;
                }
                .footer-row {
                    display: flex;
                    justify-content: space-between;
                    font-size: 12px;
                    color: #8C8C9E;
                    width: 100%;
                }

                /* ================= END PAGE STYLES ================= */
                .end-page {
                    width: 794px;
                    min-height: 1123px;
                    padding: 60px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;
                    background-color: #FAF9F4;
                    box-sizing: border-box;
                }
                .end-container {
                    margin: auto;
                    width: 100%;
                }
                .end-symbol {
                    color: #B8860B;
                    font-size: 22px;
                    margin-bottom: 15px;
                }
                .end-title {
                    font-size: 28px;
                    color: #B8860B;
                    font-family: 'anupam_mahdi', serif;
                    margin: 0 0 12px 0;
                }
                .end-line {
                    height: 1.5px;
                    width: 100px;
                    background-color: #D4A017;
                    margin: 0 auto 18px auto;
                }
                .end-text {
                    font-size: 13px;
                    color: #7A7A8E;
                    font-style: italic;
                }
            </style>
        </head>
        <body>
            $coverPageHtml
            $tocPageHtml
            $notesHtml
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

