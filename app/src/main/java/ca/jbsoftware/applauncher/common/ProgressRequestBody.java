package ca.jbsoftware.applauncher.common;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface ProgressListener {
        void onProgressUpdate(int percentage);
    }

    @NonNull
    private final File mFile;

    @Nullable
    private final ProgressListener mListener;

    public ProgressRequestBody(@NonNull File file, @Nullable ProgressListener listener) {
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() {
        return mFile.length();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long uploaded = 0;

        try (FileInputStream in = new FileInputStream(mFile)) {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            int num = 0;
            while ((read = in.read(buffer)) != -1) {
                int progress = (int) (100 * uploaded / fileLength);
                if (progress > num + 1) {
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    num = progress;
                }
                uploaded += read;
                sink.write(buffer, 0, read);
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private final long mUploaded;
        private final long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            if (mListener != null) {
                mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
            }
        }
    }
}
