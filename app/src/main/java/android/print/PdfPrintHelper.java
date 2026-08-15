package android.print;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;

public class PdfPrintHelper {

    private static final String TAG = "PdfPrintHelper";

    public interface PdfPrintCallback {
        void onSuccess(File file);
        void onError(Exception e);
    }

    public static void generatePdfFromAdapter(
            final PrintDocumentAdapter adapter,
            final File outputFile,
            final PdfPrintCallback callback
    ) {
        try {
            if (outputFile.exists()) {
                outputFile.delete();
            }

            final ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    outputFile,
                    ParcelFileDescriptor.MODE_READ_WRITE | ParcelFileDescriptor.MODE_CREATE | ParcelFileDescriptor.MODE_TRUNCATE
            );

            PrintAttributes printAttributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
                    .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                    .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
                    .build();

            CancellationSignal cancellationSignal = new CancellationSignal();

            adapter.onLayout(
                    null,
                    printAttributes,
                    cancellationSignal,
                    new PrintDocumentAdapter.LayoutResultCallback() {
                        @Override
                        public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                            Log.d(TAG, "onLayoutFinished: pages=" + (info != null ? info.getPageCount() : -1));
                            adapter.onWrite(
                                    new PageRange[]{PageRange.ALL_PAGES},
                                    pfd,
                                    cancellationSignal,
                                    new PrintDocumentAdapter.WriteResultCallback() {
                                        @Override
                                        public void onWriteFinished(PageRange[] pages) {
                                            try {
                                                pfd.close();
                                            } catch (Exception ignored) {}

                                            if (outputFile.exists() && outputFile.length() > 0) {
                                                Log.d(TAG, "PDF write finished. File size: " + outputFile.length() + " bytes");
                                                callback.onSuccess(outputFile);
                                            } else {
                                                callback.onError(new Exception("PDF ফাইলটি খালি তৈরি হয়েছে (0 KB)"));
                                            }
                                        }

                                        @Override
                                        public void onWriteFailed(CharSequence error) {
                                            try { pfd.close(); } catch (Exception ignored) {}
                                            Log.e(TAG, "onWriteFailed: " + error);
                                            callback.onError(new Exception("PDF জেনারেশন ব্যর্থ: " + error));
                                        }

                                        @Override
                                        public void onWriteCancelled() {
                                            try { pfd.close(); } catch (Exception ignored) {}
                                            Log.e(TAG, "onWriteCancelled");
                                            callback.onError(new Exception("PDF জেনারেশন বাতিল করা হয়েছে"));
                                        }
                                    }
                            );
                        }

                        @Override
                        public void onLayoutFailed(CharSequence error) {
                            try { pfd.close(); } catch (Exception ignored) {}
                            Log.e(TAG, "onLayoutFailed: " + error);
                            callback.onError(new Exception("PDF লেআউট ব্যর্থ: " + error));
                        }

                        @Override
                        public void onLayoutCancelled() {
                            try { pfd.close(); } catch (Exception ignored) {}
                            Log.e(TAG, "onLayoutCancelled");
                            callback.onError(new Exception("PDF লেআউট বাতিল করা হয়েছে"));
                        }
                    },
                    new Bundle()
            );
        } catch (Exception e) {
            Log.e(TAG, "Error in generatePdfFromAdapter", e);
            callback.onError(e);
        }
    }
}
