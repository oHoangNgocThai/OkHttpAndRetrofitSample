package android.thaihn.okhttpandretrofitsample.retrofit.client

import android.content.Context
import android.thaihn.okhttpandretrofitsample.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


object ClientProvider {

    private fun providerLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun providerInterceptor(): Interceptor {
        return Interceptor {
            var request: okhttp3.Request = it.request()
            val headers = request.headers().newBuilder().add("Authorization", "authToken").build()
            request = request.newBuilder().headers(headers).build()
            it.proceed(request)
        }
    }

    private fun providerClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(providerLoggingInterceptor())
            .addInterceptor(providerInterceptor())
            .build()
    }

    fun providerRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(providerClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Throws(
        CertificateException::class, IOException::class, KeyStoreException::class, NoSuchAlgorithmException::class,
        KeyManagementException::class
    )
    fun generateSSL(context: Context): SSLContext {
        // Loading CAs from an InputStream
        var cf: CertificateFactory? = null
        cf = CertificateFactory.getInstance("X.509")

        var ca: Certificate? = null
        // I'm using Java7. If you used Java6 close it manually with finally.
        context.resources.openRawResource(R.raw.demo).use { cert -> ca = cf!!.generateCertificate(cert) }

        // Creating a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // Creating an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

        return sslContext
    }

    private fun providerClientSSlConfig(context: Context) {
        val okHttpClient = OkHttpClient.Builder()

        try {
            okHttpClient.sslSocketFactory(generateSSL(context).socketFactory)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        okHttpClient.hostnameVerifier { hostname, session ->
            val value = true
            //TODO:Some logic to verify your host and set value
            return@hostnameVerifier value
        }
    }
}
