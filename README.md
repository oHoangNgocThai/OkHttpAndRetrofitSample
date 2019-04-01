# OkHttpAndRetrofitSample

Sử dụng Network Connection để thực hiện connect với URL và lấy về dữ liệu. Trong project là ví dụ tìm kiếm repository của Github sử dụng API mà Github cung cấp.

# [Overview](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#overview)

* Android hỗ trợ ứng dụng của bạn có thể kết nối internet hoặc là bất kì một local network nào và cho phép bạn thực hiện các hành động liên quan đến network.
* Một thiết bị Android có nhiều loại kết nối mạng, thông thường là sử dụng Wi-Fi và mạng di động.
* Trước khi thêm chức năng kết nối với network vào ứng dụng, bạn cần đảm bảo rằng dữ liệu và thông tin trong ứng dụng được an toàn khi truyền qua mạng. Sử dụng các gợi ý dưới đây:
    
    * Giảm thiểu lượng dữ liệu người dùng nhạy cảm hoặc cá nhân truyền qua mạng.
    * Gửi tất cả các dữ liệu từ ứng dụng của bạn qua [SSL](https://developer.android.com/training/articles/security-ssl.html).
    * Tạo cấu hình bảo mật mạng ([network security configuration](https://developer.android.com/training/articles/security-config.html)), cho phép ứng dụng của bạn tin cậy vào các CA(Custom trust Anchors) tùy chỉnh hoặc hạn chế bộ CA hệ thống mà nó tin tưởng.

# [Network connection](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#network-connection)

* Hầu hết các ứng dụng Android sử dụng HTTP làm giao thức để gửi và nhận dữ liệu. Nền tảng Android bao gồm **HttpsURLConnection**, nó hỗ trợ **TLS**, tải lên và tải xuống dữ liệu, thời gian chờ, IpPv6 và các luồng kết nối.
* Sử dụng quyền truy cập internet khai báo trong AndroidManifest để cho phép ứng dụng vào mạng:

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* Để tránh hiện tượng UI không phản hồi, không thực hiện các thao tác với mạng trên UI thread. Theo mặc định từ Android 3.0(API 11) trở lên yêu cầu bạn thực hiện thao tác mạng trên một luồng khác UI thread, nếu không sẽ nhận được exception **NetworkOnMainThreadException**.

## [Check network connection](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#check-network-connection)

* Một device có thể có nhiều kết nối mạng, ở đây chúng ta thường sử dụng Wi-Fi và mạng di động. Để xem thêm cách loại kết nối khác, xem thêm tại [ConnectivityManager](https://developer.android.com/reference/android/net/ConnectivityManager.html)
* Trước khi thực hiện các hành động liên quan đến mạng, bạn nên kiểm tra trạng thái của mạng có ổn định và phù hợp không. Nếu định tải file gì đó lớn quá thì nên đợi khi có wifi để có thể tải về. Để kiểm tra kết nối mạng, thông thường sử dụng 2 class sau:

    * **ConnectivityManager**: Trả về các truy vấn về trạng thái kết nối mạng, cũng thông báo khi kết nối mạng thay đổi.
    * **NetworkInfo**: Mô tả trạng thái của giao diện mạng hiện tại là gì(Wi-Fi hay là mạng di động).

> Lưu ý rằng bạn luôn phải kiểm tra xem mạng có connect được không sử dụng **isConnected()**, chứ nếu có Wi-Fi mà lại không vào được mạng thì sẽ gây ra lỗi. Trong trường hợp khác như mạng di động không ổn định, chế độ máy bay và dữ liệu nền bị hạn chế.

* Một cách ngắn gọn hơn để kiểm tra xem giao diện mạng có khả dụng hay không sử dụng phương thức **getActiveNetworkInfo()** trả về một instance của NetworkInfo. Nếu nó trả về null thì là không tìm thấy kết nối, ngược lại sẽ trả về kết nối đầu tiên mà nó kết nối được.

## [Manage network usage](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#manage-network-usage)

* Có thể tùy chọn cho phép người dùng kiểm soát rõ ràng việc sử dụng tài nguyên mạng của ứng dụng. Ví dụ:

    * Chỉ cho phép người dùng tải lên video hoặc là tải về dữ liệu nặng khi kết nối với Wi-Fi.
    * Có thể đồng bộ hóa dữ liệu hoặc không tùy chọn vào các tiêu chí như khả dụng của mạng, thời gian, ...
    
* Để có thể tạo ra ứng dụng có thể truy cập mạng, tệp kê khai AndroidManifest phải được kê khai quyền truy cập:

    * **android.permission.INTERNET**: Cho phép ứng dụng mở kết nối mạng sử dụng socket.
    * **android.permission.ACCESS_NETWORK_STATE**: Cho phép người dùng có thể đọc được các thông tin của mạng.
    
* Để tạo được ứng dụng có thể quản lý được việc sử dụng mạng, cần tạo **intern-filter** có action **android.intent.action.MANAGE_NETWORK_USAGE** để lắng nghe được các sự kiện thay đổi của network:

* Theo đó, tạo ra 1 Activity là lớp con của **PreferenceActivity** để hiển thị ra cho người dùng có thể chỉ định các mục như:

    * Có hiển thị tóm tắt cho từng mục nhập từ nguồn cấp dữ liệu XML hay chỉ là một liên kết cho mỗi mục nhập.
    * Có nên tải xuống nguồn cấp dữ liệu XML nếu có bất kì kết nối nào hay chỉ khi có Wi-Fi.
    
* Lớp này implement **OnSharedPreferenceChangeListener**, khi người dùng thay đổi thì sẽ được lưu lại và làm mới khi người dùng trở lại activity trước đó. Cài đặt chi tiết tham khảo tại [đây](https://developer.android.com/training/basics/network-ops/managing#prefs)

## [Connect and get content](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#connect-and-get-content)

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
                ... 
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.d(TAG, "Exception: ${ex.message}")
            }
        }
        return searchResponse
    }
}
```

* Ở trên sử dụng hàm **downloadUrl** được tạo để lấy ra được **InputStream** khi kết nối thành công với network.

```
var connection: HttpsURLConnection? = null
...
    val url = URL(urlStr)
    connection = url.openConnection() as HttpsURLConnection
    connection.run {
        readTimeout = 6000
        connectTimeout = 6000
        requestMethod = "GET"
        doInput = true
        connect()

        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw IOException("HTTP error code: $responseCode")
        }

        inputStream?.let { stream ->
            result = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            stream.close()
        }
...
```

* Khi kết nối thành công, sẽ trả về cho chúng ta **responseCode** và các thông tin khác, sử dụng **InputStream** để đọc ra dữ liệu:

```
stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
```

# [Network connection with OkHttp](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#network-connection-with-okhttp)

* OkHTTP là một dự án mã nguồn mở được thiết kế để trở thành một client HTTP hiệu quả. Nó hỗ trợ giao thức SPDY, giao thức này là cơ sở cho HTTP 2.0 và cho nhiều request HTTP được phép trên một luồng socket.
* Thêm dependency của **OkHttp** vào file build.gradle:

```
implementation 'com.squareup.okhttp:okhttp:2.5.0'
```

## [Create Request object](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#create-request-object)

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

## [Sending and receive network call](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#sending-and-receive-network-call)

* Để thực hiện việc call network đồng bộ, hãy sử dụng **OkHttpClient** để tạo và sử dụng phương thức **execute**:

```
val client = OkHttpClient()
val response: Response = client.newCall(request).execute()
val strResponse = response.body()?.string()?.trim()
```

* Nếu muốn thực hiện call network bất đồng bộ, hãy sử dụng phương thức **enqueue**, sau đó dữ liệu trả về ở hàm override **onResponse** và on **onFailure**.

# [Network connection with Retrofit](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#network-connection-with-retrofit)

* Sử dụng thư viện Retrofit để xử lý các request và nhận về các response hoặc error. Được xem là thư viện về network mạnh nhất hiện giờ, trước kia có một thời đã sử dụng thư viện **Volley**.
* Ứng dụng sử dụng **Restful APIs** để hiển thị nội dung được lấy từ các api. Mọi tương tác này đều cần có mạng để có thể thực hiện được.
* Trước tiên sử dụng thư viện, thêm dependency vào project:

```
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'
implementation 'com.squareup.okhttp3:logging-interceptor:3.13.1'
implementation 'com.squareup.okhttp3:okhttp:3.13.1'
```

## [Retrofit basic](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#retrofit-basic)

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

* Tạo object sử dụng thư viện **Gson()** để parser dữ liệu bên trong retrofit với annotation **@SerializedName**.

* Sau đó tạo ra một instance từ service đã tạo được cung cấp bởi phương thức **retrofit.create(GithubService::class.java)**:

* Thực hiện call api theo phương thức **enqueue**, trả về dữ liệu nhận được nếu thành công là **response.body()**, nếu request lỗi, sẽ nhận được lỗi trong **response.errorBody()**:

> Sử dụng **response.code()** trong phương thức **onResponse** để check xem request có thành công hay không và dựa vào đó để check body phù hợp.

## [Retrofit with repository pattern](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample/blob/master/app/Documents.md#retrofit-with-repository-pattern)

* Thông qua **Repository Pattern** để quản lý tốt hơn về việc tạo ra một kho lưu trữ lấy dữ liệu từ client hoặc server.
* Sủ dụng Repository có một số đặc điểm sau:
    
    * Các cuộc gọi đến repository luôn luôn không đồng bộ
    * Gọi đến phương thức của repository sẽ nhận được 1 luồng callback hoặc 1 luồng kết quả.
    * Mỗi repository được xác định bởi 1 interface và không cần quan tâm đến triển khai của các phương thức.
    * Mỗi repository có thể có một số triển khai trong đó mỗi triển khai lại có trách nhiệm duy nhất và có thể mở rộng các triển khai bằng các sử dụng **decorator pattern**. 
    * Có thể được configuration bởi dependency container(ví dụ như Dagger 2).
    
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


## [Retrofit 2 Client](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#retrofit-2-client)

> Thiết lập Retrofit 2 và chạy trên Android rất đơn giản, nhưng đôi khi bạn gặp phải vài vấn đề như **Authorisation Headers**, **Basic Authentication** & Hỗ trợ API SSL.

### [Logging Interceptor](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#logging-interceptor)

* Thêm dependency của Loggin interceptor vào project:

```
implementation 'com.squareup.okhttp3:logging-interceptor:3.13.1'
```

* Tạo một instance của **OkHttpClient** để thêm client vào retrofit, tạo một instance của **HttpLoggingInterceptor()** để thêm Log bạn cần, giả sử như **HttpLoggingInterceptor.Level.BODY** bằng phương thức **setLevel()**.

```
fun providerClient(): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()

    val loginInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    clientBuilder.addInterceptor(loginInterceptor)

    return clientBuilder.build()
}
```

* Thêm client vào việc tạo instance của retrofit bằng phương thức **addClient()**

```
return Retrofit.Builder()
    .client(providerClient())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### [Authorization Header](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#authorization-header)

* Thêm request header cho HTTP, ví dụ điển hình nhất là thêm **Authorization header** cho API của bạn.
* Tạo instance của **Interceptor** và thêm các header mà bạn mong muốn vào bên trong của phương thức override **intercept**:

```
return Interceptor {
    var request: okhttp3.Request = it.request()
    val headers = request.headers().newBuilder().add("Authorization", "authToken").build()
    request = request.newBuilder().headers(headers).build()
    it.proceed(request)
}
``` 

* **Basic authentication** là một sơ đồ xác thực cơ bản trong giao thức HTTP. Client gửi request đến HTTP sau đó kèm theo **Authorization header** chứa chuỗi cơ bản theo sau là khoảng trắng và tên người dùng được mã hóa dạng base64 **username:password**. Ví dụ:

```
Authorization: Basic ZGVtbzpwQDU1dzByZA==
```

* Để thêm **Basic Auth** vào trong header của API, sử dụng **OkHttp3 Credentials** để mã hóa tên người dùng và mật khẩu.

```
if (!TextUtils.isEmpty(username)
    && !TextUtils.isEmpty(password)) {
        authToken = Credentials.basic(username, password);
}
```

### [SSL Configuration](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#ssl-configuration)

* Theo mặc định, Retrofit không thể kết nối với các API được bảo vệ bằng SSL, vì vậy phải sử dụng Retrofit 2 Client để config connect được với SSL.
* Bước đầu tiên, tạo file **.crt** nằm trong thư mục **raw** của resource. Sau đó tạo function để tự động sinh ra SSL certificate từ file **.crt** và trả về đối tượng **SSLContext**.

```
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
```

* Set **SSLSocketFactory** vào OkHttpClient sử dụng SSLContext và cũng đặt trình xác minh tên máy chủ:

```
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
```



## [Retrofit with LiveData](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#retrofit-with-livedata)

* Thông thường khi thiết kế triến trúc là MVVM hoặc MVP, chúng ta thường muốn tương tác với repository thông qua ViewModel để nhận được những bản cập nhật mới nhất nếu có. Vấn đề nhận thấy là phải thực hiện gần nhưu cùng một số kiểm tra mỗi lần nhận được phản hồi, những kiểm tra lại dữ liệu này như:

    * Nếu response.body() không null mới thực hiện.
    * Nếu không có exception được ném vào phương thức **onFailure()**.
    * Phải check xem view của mình nếu như một exception nhận được nhưng không xử lý đúng như dữ liệu của mình.
    
* Những vấn đề trên đã có từ rất lâu, trước cả khi có Architecture Component, ngoài ra nếu nhận được cập nhật cho observer của mình cũng phải kiểm tra lại nhiều lần để xử lý cho đúng. 
* Vì vậy nếu như có thể xử lý được việc trừu tượng toàn bộ quá trình từ request callback đến gọi dữ liệu của mình sau đó cũng sửa đổi my observer để thông báo về cho người dùng về lỗi của họ.

### [API Concerns](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#api-concerns)

* Đầu tiên chúng ta sẽ đơn giản hóa phương thức callback bằng cách triển khai lại **interface Callback<T>** và cung cấp triển khai của mình sử dụng Observable:

```
abstract class ApiCallback<T> : Callback<Response<T>> {

    override fun onResponse(call: retrofit2.Call<Response<T>>, response: retrofit2.Response<Response<T>>) {
        val body = response.body()
        if (body != null) {
            handleResponseData(body)
        } else {
            handleError(call)
        }
    }

    override fun onFailure(call: retrofit2.Call<Response<T>>, t: Throwable) {
        if (t is java.lang.Exception) {
            handleException(t)
        } else {
            // Do something else
        }
    }

    abstract fun handleResponseData(t: Response<T>)

    abstract fun handleError(response: retrofit2.Call<Response<T>>)

    abstract fun handleException(ex: Exception)
}
```

* Class này xử lý các trường hợp lỗi, thành công hoặc exception sau đó cung cấp kết quả tùy thuộc vào phương thức được thực thi. Vì vậy làm giảm đi các quá trình check lại. Để làm cho việc xử lý lỗi ít hơn, hãy tạo 1 mô hình đặc biệt chỉ bao gồm kiểu dữ liệu chung và một exception.

```
class DataWrapper<T> {
    var apiException: Exception? = null
    var data: T? = null
}
```

* Sau khi có DataWrapper, tiếp tục tạo một GenericRequestHandler để xử lý logic thường được sử dụng cho những request tới API:

```
abstract class GenericRequestHandler<T> {

    abstract fun makeRequest(): Call<Response<T>>

    fun doRequest(): LiveData<DataWrapper<T>> {
        val liveData = MutableLiveData<DataWrapper<T>>()

        val dataWrapper = DataWrapper<T>()

        makeRequest().enqueue(object : ApiCallback<T>(){

            override fun handleError(response: Call<Response<T>>) {
                // handle error from response
            }

            override fun handleException(ex: Exception) {
                dataWrapper.apiException = ex
                liveData.value = dataWrapper
            }

            override fun handleResponseData(t: Response<T>) {
                // parser data from data
            }
        })
        return liveData
    }
}
```

* Sau khi được class xử lý việc gửi request, chúng ta sẽ cần class tạo ra **makeRequest()**.

```
class SignInInteractor : GenericRequestHandler<User>() {
    private val authService = APIService.getInstance().getServiceForApi(AuthService::class.java)
    private var userId: String? = null
    private var pinCode: String? = null

    fun onAuthRequest(): LiveData<DataWrapper<User>> {
        return doRequests()
    }

    override fun makeRequest(): Call<Response<User>> {
        return authService.postUserPhoneLogin(RequestBody.UserPhoneLogin(userId, pinCode))
    }

    companion object {

        fun createInstance(userId: String, pinCode: String): SignInInteractor {
            val signInWithPinLoader = SignInInteractor()
            signInWithPinLoader.userId = userId
            signInWithPinLoader.pinCode = pinCode
            return signInWithPinLoader
        }
    }
}
```

### [View concerns](https://github.com/oHoangNgocThai/OkHttpAndRetrofitSample#view-concerns)

* Custom class Observer để trả về dữ liệu hoặc là error như sau:

```
class ApiObserver<T>(private val changeListener: ChangeListener<T>) : Observer<DataWrapper<T>> {

    fun onChanged(@Nullable tDataWrapper: DataWrapper<T>?) {
        if (tDataWrapper != null) {
            if (tDataWrapper.apiException != null) {
                changeListener.onException(tDataWrapper.apiException)
            } else {
                changeListener.onSuccess(tDataWrapper.data)
            }
            return
        }
        //custom exceptionn to suite my use case
//        changeListener.onException(ValidationAPIException(ERROR_CODE, null))
    }

    interface ChangeListener<T> {
        fun onSuccess(dataWrapper: T?)
        fun onException(exception: Exception?)
    }

    companion object {
        private val ERROR_CODE = 0
    }
}
```

* Giờ thì xử lý Observer đó ở trong ViewModel và trả về dữ liệu thông qua LiveData về activity.
