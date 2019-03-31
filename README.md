# OkHttpAndRetrofitSample

Sử dụng Network Connection để thực hiện connect với URL và lấy về dữ liệu. Trong project là ví dụ tìm kiếm repository của Github sử dụng API mà Github cung cấp.

# Overview

* Android hỗ trợ ứng dụng của bạn có thể kết nối internet hoặc là bất kì một local network nào và cho phép bạn thực hiện các hành động liên quan đến network.
* Một thiết bị Android có nhiều loại kết nối mạng, thông thường là sử dụng Wi-Fi và mạng di động.
* Trước khi thêm chức năng kết nối với network vào ứng dụng, bạn cần đảm bảo rằng dữ liệu và thông tin trong ứng dụng được an toàn khi truyền qua mạng. Sử dụng các gợi ý dưới đây:
    
    * Giảm thiểu lượng dữ liệu người dùng nhạy cảm hoặc cá nhân truyền qua mạng.
    * Gửi tất cả các dữ liệu từ ứng dụng của bạn qua [SSL](https://developer.android.com/training/articles/security-ssl.html).
    * Tạo cấu hình bảo mật mạng ([network security configuration](https://developer.android.com/training/articles/security-config.html)), cho phép ứng dụng của bạn tin cậy vào các CA(Custom trust Anchors) tùy chỉnh hoặc hạn chế bộ CA hệ thống mà nó tin tưởng.

# Network connection

* Hầu hết các ứng dụng Android sử dụng HTTP làm giao thức để gửi và nhận dữ liệu. Nền tảng Android bao gồm **HttpsURLConnection**, nó hỗ trợ **TLS**, tải lên và tải xuống dữ liệu, thời gian chờ, IpPv6 và các luồng kết nối.
* Sử dụng quyền truy cập internet khai báo trong AndroidManifest để cho phép ứng dụng vào mạng:

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* Để tránh hiện tượng UI không phản hồi, không thực hiện các thao tác với mạng trên UI thread. Theo mặc định từ Android 3.0(API 11) trở lên yêu cầu bạn thực hiện thao tác mạng trên một luồng khác UI thread, nếu không sẽ nhận được exception **NetworkOnMainThreadException**.

## Check network connection

* Một device có thể có nhiều kết nối mạng, ở đây chúng ta thường sử dụng Wi-Fi và mạng di động. Để xem thêm cách loại kết nối khác, xem thêm tại [ConnectivityManager](https://developer.android.com/reference/android/net/ConnectivityManager.html)
* Trước khi thực hiện các hành động liên quan đến mạng, bạn nên kiểm tra trạng thái của mạng có ổn định và phù hợp không. Nếu định tải file gì đó lớn quá thì nên đợi khi có wifi để có thể tải về. Để kiểm tra kết nối mạng, thông thường sử dụng 2 class sau:

    * **ConnectivityManager**: Trả về các truy vấn về trạng thái kết nối mạng, cũng thông báo khi kết nối mạng thay đổi.
    * **NetworkInfo**: Mô tả trạng thái của giao diện mạng hiện tại là gì(Wi-Fi hay là mạng di động).
    
* Ví dụ kiểm tra mạng xem kết nối là dạng di động hay là Wi-Fi, chúng ta làm như sau:

```
val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
var isWifiConn: Boolean = false
var isMobileConn: Boolean = false
connMgr.allNetworks.forEach { network ->
    connMgr.getNetworkInfo(network).apply {
        if (type == ConnectivityManager.TYPE_WIFI) {
            isWifiConn = isWifiConn or isConnected
        }
        if (type == ConnectivityManager.TYPE_MOBILE) {
            isMobileConn = isMobileConn or isConnected
        }
    }
}
Log.d(DEBUG_TAG, "Wifi connected: $isWifiConn")
Log.d(DEBUG_TAG, "Mobile connected: $isMobileConn")
```

> Lưu ý rằng bạn luôn phải kiểm tra xem mạng có connect được không sử dụng **isConnected()**, chứ nếu có Wi-Fi mà lại không vào được mạng thì sẽ gây ra lỗi. Trong trường hợp khác như mạng di động không ổn định, chế độ máy bay và dữ liệu nền bị hạn chế.

* Một cách ngắn gọn hơn để kiểm tra xem giao diện mạng có khả dụng hay không sử dụng phương thức **getActiveNetworkInfo()** trả về một instance của NetworkInfo. Nếu nó trả về null thì là không tìm thấy kết nối, ngược lại sẽ trả về kết nối đầu tiên mà nó kết nối được.

```
fun isOnline(): Boolean {
    val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
    return networkInfo?.isConnected == true
}
```

## Manage network usage

* Có thể tùy chọn cho phép người dùng kiểm soát rõ ràng việc sử dụng tài nguyên mạng của ứng dụng. Ví dụ:

    * Chỉ cho phép người dùng tải lên video hoặc là tải về dữ liệu nặng khi kết nối với Wi-Fi.
    * Có thể đồng bộ hóa dữ liệu hoặc không tùy chọn vào các tiêu chí như khả dụng của mạng, thời gian, ...
    
* Để có thể tạo ra ứng dụng có thể truy cập mạng, tệp kê khai AndroidManifest phải được kê khai quyền truy cập:

    * **android.permission.INTERNET**: Cho phép ứng dụng mở kết nối mạng sử dụng socket.
    * **android.permission.ACCESS_NETWORK_STATE**: Cho phép người dùng có thể đọc được các thông tin của mạng.
    
* Để tạo được ứng dụng có thể quản lý được việc sử dụng mạng, cần tạo **intern-filter** có action **android.intent.action.MANAGE_NETWORK_USAGE** để lắng nghe được các sự kiện thay đổi của network:

```
<activity android:label="SettingsActivity" android:name=".SettingsActivity">
    <intent-filter>
        <action android:name="android.intent.action.MANAGE_NETWORK_USAGE" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

* Theo đó, tạo ra 1 Activity là lớp con của **PreferenceActivity** để hiển thị ra cho người dùng có thể chỉ định các mục như:

    * Có hiển thị tóm tắt cho từng mục nhập từ nguồn cấp dữ liệu XML hay chỉ là một liên kết cho mỗi mục nhập.
    * Có nên tải xuống nguồn cấp dữ liệu XML nếu có bất kì kết nối nào hay chỉ khi có Wi-Fi.
    
* Lớp này implement **OnSharedPreferenceChangeListener**, khi người dùng thay đổi thì sẽ được lưu lại và làm mới khi người dùng trở lại activity trước đó. Cài đặt chi tiết tham khảo tại [đây](https://developer.android.com/training/basics/network-ops/managing#prefs)

## Connect and get content

* Khi connect với mạng, cần thực hiện trên một thread khác, sử dụng **AsyncTask** để tạo một thread chạy dưới background, sau đó cập nhật trên main thread.

```
inner class SearchRepositoryAsync : AsyncTask<String, Int, SearchResponse>() {

    override fun doInBackground(vararg params: String?): SearchResponse? {
        var searchResponse: SearchResponse? = null
        val key: String? = params[0]
        if (key != null) {
            try {
                val urlString = createUrl(key)
                val resultString = downloadUrl(urlString)
                Log.d(TAG, "Response: $resultString")

                resultString?.let {
                    searchResponse = Gson().fromJson(it, SearchResponse::class.java)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, "Exception: ${ex.message}")
            }
        }
        return searchResponse
    }

    override fun onPostExecute(result: SearchResponse?) {
        Log.d(TAG, "Result: $result")
        if (result != null) {
            // update ui
        }
}
```

* Ở trên sử dụng hàm **downloadUrl** được tạo để lấy ra được **InputStream** khi kết nối thành công với network.

```
@Throws(IOException::class)
    fun downloadUrl(urlStr: String): String {
        var result = ""
        var connection: HttpsURLConnection? = null
        try {
            val url = URL(urlStr)
            connection = url.openConnection() as HttpsURLConnection
            connection.run {
                readTimeout = 6000
                connectTimeout = 6000
                requestMethod = "GET"
                doInput = true
                connect()

                Log.d(TAG, "responseCode: $responseCode")
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                inputStream?.let { stream ->
                    result = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    stream.close()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    return result
}
```

* Khi kết nối thành công, sẽ trả về cho chúng ta **responseCode** và các thông tin khác, sử dụng **InputStream** để đọc ra dữ liệu:

```
stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
```

## Optimize network data usage

# Network connection with OkHttp

* OkHTTP là một dự án mã nguồn mở được thiết kế để trở thành một client HTTP hiệu quả. Nó hỗ trợ giao thức SPDY, giao thức này là cơ sở cho HTTP 2.0 và cho nhiều request HTTP được phép trên một luồng socket.
* Thêm dependency của **OkHttp** vào file build.gradle:

```
implementation 'com.squareup.okhttp:okhttp:2.5.0'
```

## Create Request object

* Để sử dụng OkHttp, chúng ta cần phải tạo ra một Request Object chứa thông tin để gửi request:

```
val request = Request.Builder()
    .url(url)
    .build()
```

* Bạn cũng có thể thêm các query hoặc param cho url bằng cách sử dụng **HttpUrl.Builder**:

```
val url: String = HttpUrl.parse("https://api.github.com/search/repositories")?.newBuilder()?.apply {
    addQueryParameter("q", key)
    addQueryParameter("sort", "")
    addQueryParameter("order", "desc")
}?.build().toString()
```

* Có thể thêm Header vào Request object như sau:

```
val request = Request.Builder()
    .header("Content-Type", "application/json")
    ...
```

## Sending and receive network call

* Để thực hiện việc call network đồng bộ, hãy sử dụng **OkHttpClient** để tạo và sử dụng phương thức **execute**:

```
val client = OkHttpClient()
val response: Response = client.newCall(request).execute()
val strResponse = response.body()?.string()?.trim()
```

* Nếu muốn thực hiện call network bất đồng bộ, hãy sử dụng phương thức **enqueue**:

```
OkHttpClient().newCall(request).enqueue(object : Callback {

    override fun onResponse(call: Call, response: Response) {
        val strResponse = response.body()?.string()?.trim()
        Log.d(TAG, "Response: $strResponse")
        if (strResponse != null) {
            responseSearch = Gson().fromJson(strResponse, SearchResponse::class.java)
        }
        return responseSearch
    }

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        Log.d(TAG, "onFailure: ${e.message}")
    }
})
``` 

# Network connection with Retrofit

* Sử dụng thư viện Retrofit để xử lý các request và nhận về các response hoặc error. Được xem là thư viện về network mạnh nhất hiện giờ, trước kia có một thời đã sử dụng thư viện **Volley**.
* Ứng dụng sử dụng **Restful APIs** để hiển thị nội dung được lấy từ các api. Mọi tương tác này đều cần có mạng để có thể thực hiện được.
* Trước tiên sử dụng thư viện, thêm dependency vào project:

```
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
implementation 'com.squareup.okhttp3:logging-interceptor:3.13.1'
implementation 'com.squareup.okhttp3:okhttp:3.13.1'
```

## Retrofit basic

* Tạo một Service API để định nghĩa phương thức request và các tham số cần truyền vào, ở đây mình sử dụng API search các repository của github:

```
@GET("search/repositories")
fun searchUser(
    @Query("q") query: String,
    @Query("sort") sort: String,
    @Query("order") order: String
): Call<SearchResponse>
```
> Ở đây sẽ trả về object [SearchResponse](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/src/main/java/android/thaihn/okhttpandretrofitsample/entity/SearchResponse.kt) khi tìm kiếm repository.

* Tạo object sử dụng thư viện **Gson()** để parser dữ liệu bên trong retrofit:

```
data class SearchResponse(
        @SerializedName("total_count")
        val total_count: Int,
        
        @SerializedName("incomplete_results")
        val incomplete_results: Boolean,
        
        @SerializedName("items")
        val items: List<Repository>?
)
```

* Sau đó tạo ra một instance từ service đã tạo được cung cấp bởi phương thức **retrofit.create(GithubService::class.java)**:

```
private fun createService(): GithubService {
    val BASE_URL = "https://api.github.com"

    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(GithubService::class.java)
}
```

* Thực hiện call api theo phương thức **enqueue**, trả về dữ liệu nhận được nếu thành công là **response.body()**, nếu request lỗi, sẽ nhận được lỗi trong **response.errorBody()**:

```
val service = createService()
val callSearch: Call<SearchResponse> = service.searchUser("mario", "start", "desc")
callSearch.enqueue(object : Callback<SearchResponse> {

    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
        Log.d(TAG, "Response: ${response.body()}")
        val errorBody = response.errorBody()
        response.body()?.items?.let {
            mRepositoryAdapter.updateAllData(it)
        }
    }

    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
        t.printStackTrace()
        Log.d(TAG, "onFailure: ${t.message}")
    }
})
```
> Sử dụng **response.code()** để check xem request có thành công hay không và dựa vào đó để check body phù hợp.

## Retrofit with repository pattern

* Thông qua **Repository Pattern** để quản lý tốt hơn về việc tạo ra một kho lưu trữ lấy dữ liệu từ client hoặc server.
* Sủ dụng Repository có một số đặc điểm sau:
    
    * Các cuộc gọi d
* Tạo một interface **DataSource** để định nghĩa các phương thức lấy dữ liệu ở cả api và local:

```
interface GithubDataSource {
    fun searchRepository(name: String): Observable<SearchResponse>
}
```

* Tiếp theo tạo **RemoteDataSoure** để định nghĩa phương thức lấy dữ liệu từ Remote Api, được cung cấp dựa trên **Retrofit** và tạo ra 1 instance của **GithubApi**.

```
class GithubRemoteDataSource(
    private val githubApi: GithubService
) : GithubDataSource {

    override fun searchRepository(name: String): Observable<SearchResponse> {
        return githubApi.searchUser(name, "start", "desc")
    }
}
```

* Tạo 1 class Repository để định nghĩa phương thức lấy ra dữ liệu như sau:

```
class GithubRepository(
    private val githubRemoteDataSource: GithubRemoteDataSource
) : GithubDataSource {

    override fun searchRepository(name: String): Observable<SearchResponse> {
        return githubRemoteDataSource.searchRepository(name)
    }
}
```

* Để cung cấp được instance của GithubApi, chúng ta thêm client của OkHttp như sau:

```
object RetrofitProvider {

    private const val BASE_URL = "https://api.github.com"
    private const val CONNECT_TIMEOUT = 10L
    private const val READ_TIMEOUT = 10L
    private const val WRITE_TIMEOUT = 10L

    private fun providerHttpClient() = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .build()

    private fun providerRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(providerHttpClient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun providerGithubApi() = providerRetrofit().create(GithubService::class.java)
}
```
