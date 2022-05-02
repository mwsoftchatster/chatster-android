package nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager;



import java.io.BufferedInputStream;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import nl.mwsoft.www.chatster.MyApplication;
import okhttp3.OkHttpClient;

public class OkHttpClientManager {

    public static OkHttpClient setUpSecureClient() {

        try {
            // Load CAs from a resource
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream ins = MyApplication.context().getResources().openRawResource(
                    MyApplication.context().getResources().getIdentifier("chatstercert",
                            "raw", MyApplication.context().getPackageName()));
            InputStream caInput = new BufferedInputStream(ins);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Instances of this class represent a secure socket protocol implementation which acts
            // as a factory for secure socket factories or SSLEngines. This class is initialized with
            // an optional set of key and trust managers and source of secure random bytes.
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(),
                    new java.security.SecureRandom());

            // SSL (Secure Sockets Layer) is the standard security technology for establishing an
            // encrypted link between a web server and a browser. This link ensures that all data
            // passed between the web server and browsers remain private and integral.

            // Socket is full-duplex, which means you can send and receive data simultaneously

            // Create an ssl socket factory
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .retryOnConnectionFailure(true)
                    .build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
