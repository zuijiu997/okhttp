package zuijiu997;


import lombok.extern.java.Log;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log
public class HttpUtil {

    private static final String CER_NAME = "";   //https签名证书name

    private static OkHttpClient client;
    private static SSLContext sslContext;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        client = new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build();

        //忽略所有证书
        final X509TrustManager[] trustAllCerts = new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
        } catch (Exception e) {
            log.info("ssl出现异常");
        }

        client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }).build();
    }

    public static Response get(String url, Map<String, String> headers) {
        try {
            Request.Builder builder = builder(url, headers);
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response post(String url, Map<String, String> headers, String body) {
        try {
            Request.Builder builder = builder(url, headers);
            RequestBody requestBody = RequestBody.create(body, JSON);
            builder.post(requestBody);
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Request.Builder builder(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        if (headers != null && headers.size() > 0) {
            for (String header : headers.keySet()) {
                builder.addHeader(header, headers.get(header));
            }
        }

        builder.url(url);
        return builder;
    }

//    public static void setCard(InputStream certificate) {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            String certificateAlias = Integer.toString(0);
//            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
//            sslContext = SSLContext.getInstance("TLS");
//            final TrustManagerFactory trustManagerFactory =
//                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            sslContext.init
//                    (
//                            null,
//                            trustManagerFactory.getTrustManagers(),
//                            new SecureRandom()
//                    );
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//    }

    public static class LoggingInterceptor implements Interceptor {

        @NotNull
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();

            Buffer buffer = new Buffer();
            if (request.body() != null) request.body().writeTo(buffer);
            String requestBody = buffer.readString(Charset.forName("UTF-8"));

            long startTime = System.nanoTime();
            log.info(String.format("Sending request %s on %s%n%s, request body: %s",
                    request.url(), chain.connection(), request.headers(), requestBody));

            Response response =  chain.proceed(request);

            String responseBody = response.body() == null ? "" : response.body().string();
            Response.Builder builder = response.newBuilder();
            builder.body(ResponseBody.create(responseBody, JSON));

            long endTime = System.nanoTime();
            log.info(String.format("Received response for %s in %.1fms%n%s, response body: %s",
                    response.request().url(), (endTime - startTime) / 1e6d, response.headers(), responseBody));

            return builder.build();
        }
    }
}
