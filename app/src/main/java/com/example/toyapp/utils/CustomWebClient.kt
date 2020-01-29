package com.example.toyapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.toyapp.MainActivity
import java.util.*

fun WebView.customWebViewSetting() : WebView {
    // 컨텍스트 획득
    val self = this
    val context = this.context

    // 기본 설정
    this.settings.apply {

        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        // 팝업 새창 허용
        setSupportMultipleWindows(true)
    }


    this.webChromeClient = object : WebChromeClient() {

        // For Android 5.0+
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            // Callback 초기화 (필수..)
            if ( (webView?.context as? MainActivity)?.chooseFileCallback != null) {
                (webView.context as? MainActivity)?.chooseFileCallback?.onReceiveValue(null)
                (webView.context as? MainActivity)?.chooseFileCallback = null
            }
            // 선택된 파일정보 전달..?
            (webView?.context as? MainActivity)?.chooseFileCallback = filePathCallback

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            // 파일 결과 셋
            startActivityForResult(webView?.context as Activity, Intent.createChooser(intent, "File Chooser"), CHOOSE_FILE_REQUEST_CODE, null)

            return true
        }

        // 새창열기 이벤트
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {

            self.removeAllViews()

            val newWebView = WebView(context)
            newWebView.customWebViewSetting()
            newWebView.webViewClient = object: WebViewClient() {

                // WebView 에 새로운 Url이 로드될려고 할 경우 실행된다.
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return if (url != null) {
                        Log.d("[POPUP URL]", "${url}")
                        view?.loadUrl(url)
                        // true 를 반환하는 경우 새로운 URL을 찾기위해 외부 브라우저를 더이상 찾지않는다.
                        //return true
                        true
                    } else {
                        //return false
                        false
                    }
                }
            }


            val dialog = Dialog(context)
            dialog.setContentView(newWebView)
            // 팝업창 화면 부모 뷰의 내부 여백 크기제외한 만큼 설정
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.show()

            newWebView.webChromeClient = object: WebChromeClient() {
                // 팝업 닫는 함수
                override fun onCloseWindow(window: WebView?) {
                    super.onCloseWindow(window)
                    dialog.dismiss()
                    Log.d("[CLOSE CALL]", "${window}")
                }
            }

            (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
            resultMsg.sendToTarget()

            return true
        }

    }

    return this
}



