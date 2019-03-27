# OkHttpAndRetrofitSample

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



# Network connection with OkHttp

# Network connection with Retrofit