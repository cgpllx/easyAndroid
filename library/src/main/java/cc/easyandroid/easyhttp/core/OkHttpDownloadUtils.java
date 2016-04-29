package cc.easyandroid.easyhttp.core;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cc.easyandroid.easyhttp.core.down.ProgressListener;
import cc.easyandroid.easyhttp.core.down.ProgressResponseBody;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpDownloadUtils  {
//    public OkHttpDownloadUtils(OkHttpClient client) {
//        super(client);
//    }

//    /**
//     * 异步下载文件
//     *
//     * @param url
//     * @param destFileDir 本地文件存储的文件夹
//     */
//    public void downloadAsyn(final String url, final String destFileDir) {
//        final Request request = new Request.Builder().url(url).build();
//        final Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(final Request request, final IOException e) {
//                // sendFailedStringCallback(request, e, callback);
//            }
//
//            @Override
//            public void onResponse(Response response) {
//                InputStream is = null;
//                byte[] buf = new byte[2048];
//                int len = 0;
//                FileOutputStream fos = null;
//                try {
//                    is = response.body().byteStream();
//
//                    File dir = new File(destFileDir);
//                    if (!dir.exists()) {
//                        dir.mkdirs();
//                    }
//                    File file = new File(dir, getFileName(url));
//                    fos = new FileOutputStream(file);
//                    while ((len = is.read(buf)) != -1) {
//                        fos.write(buf, 0, len);
//                    }
//                    fos.flush();
//                    // 如果下载文件成功，第一个参数为文件的绝对路径
//                    // sendSuccessResultCallback(file.getAbsolutePath(),
//                    // callback);
//                } catch (IOException e) {
//                    // sendFailedStringCallback(response.request(), e,
//                    // callback);
//                } finally {
//                    try {
//                        if (is != null)
//                            is.close();
//                    } catch (IOException e) {
//                    }
//                    try {
//                        if (fos != null)
//                            fos.close();
//                    } catch (IOException e) {
//                    }
//                }
//
//            }
//        });
//
//    }
    public void downloadAsyn(OkHttpClient client,final String url, final String destFileDir,final ProgressListener progressListener) throws IOException {
        final Request request = new Request.Builder().url(url).build();
        final Call call = client.newCall(request);
        Response response= call.execute();
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            //得到数据的大小
            int length = is.available();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, getFileName(url));
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                progressListener.update(len,length,false);
            }
            progressListener.update(len,length,true);
            fos.flush();
            // 如果下载文件成功，第一个参数为文件的绝对路径
            // sendSuccessResultCallback(file.getAbsolutePath(),
            // callback);
        } catch (IOException e) {
            // sendFailedStringCallback(response.request(), e,
            // callback);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }
        }

    }
//
//    /**
//     * 下载文件包含进度
//     *
//     * @param url      file url
//     * @param dir      file save dir
//     * @param fileName file name
//     */
//    public void DownloadFileAndprogressAsyn(String url, String dir, String fileName, final ProgressListener progressListener, Callback responseCallback) {
//        Request request = new Request.Builder().url(url).build();
//        client.clone().networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response originalResponse = chain.proceed(chain.request());
//                return originalResponse.newBuilder()
//                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
//                        .build();
//            }
//        });
//        client.newCall(request).enqueue(responseCallback);
//    }
//
//    /**
//     * 下载文件包含进度
//     *
//     * @param url      file url
//     * @param dir      file save dir
//     * @param fileName file name
//     */
    public void DownloadFileAndprogress(OkHttpClient client,String url, String dir, String fileName, final ProgressListener progressListener) {
        Request request = new Request.Builder().url(url).build();
        client.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
}