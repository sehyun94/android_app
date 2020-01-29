package com.example.toyapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import com.example.toyapp.utils.APP_URL
import com.example.toyapp.utils.CHOOSE_FILE_REQUEST_CODE
import com.example.toyapp.utils.customWebViewSetting
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // file callback
    var chooseFileCallback: ValueCallback<Array<Uri>>? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 웹뷰 설정 커스텀 함수 호출
        webview.customWebViewSetting()

        // 웹뷰 URL 로드
        webview.loadUrl(APP_URL)
    }

    // 파일 콜백시 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHOOSE_FILE_REQUEST_CODE) {
            chooseFileCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
            chooseFileCallback = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


    override fun onBackPressed() {
        if(webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }


}
