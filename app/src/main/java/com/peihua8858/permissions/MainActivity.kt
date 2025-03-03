package com.peihua8858.permissions

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.fz.toast.showToast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.peihua8858.permissions.core.requestPermission
import com.peihua8858.permissions.core.requestPermissions
import com.peihua8858.permissions.theme.ComposeDemoTheme
import com.peihua8858.permissions.theme.MarketFontFamily

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ComposeDemoTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            ),
                            title = {
                                Text(
                                    text = "Demo",
                                    fontFamily = MarketFontFamily.NotoSansSc500,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .wrapContentWidth(Alignment.CenterHorizontally) // 水平居中
                                        .fillMaxWidth(),
                                )
                            },
                        )
                    }) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        val permissionState =
                            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
                        val permissionState2  = remember { mutableStateOf(false) }
                        permissionState.status
                        requestPermission(Manifest.permission.CAMERA){
                        }
                        requestPermissions(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA
                        ) {
//                            onGranted {
//                                // 权限已授予
//                                permissionState2.value = true
//                                Log.d("MainActivity>>>", "权限已授予")
//                                showToast("权限已授予")
//                            }
//                            onDenied {
//                                // 权限被拒绝
//                                permissionState2.value = false
//                                Log.d("MainActivity>>>", "权限被拒绝")
//                                showToast("权限被拒绝")
//                            }
//                            onShowRationale {
//
//                            }
                        }
                        Column(modifier = Modifier
                            .align(Alignment.Center)
                            .padding(innerPadding)) {
                            Text(
                                text = "Android 系统获取权限 ${permissionState.status.isGranted}",
                                fontFamily = MarketFontFamily.NotoSansSc500,
                                modifier = Modifier.clickable {

                                }
                            )
                            Text(
                                text = "Android 自定义获取权限 ${permissionState2.value}",
                                fontFamily = MarketFontFamily.NotoSansSc500,
                                modifier = Modifier.clickable {

                                }
                            )

                        }
                    }
                }

            }
        }
    }

}
