package com.peihua8858.permissions

import android.Manifest
import android.os.Bundle
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
import com.peihua8858.permissions.compose.isGranted
import com.peihua8858.permissions.compose.deniedPermanently
import com.peihua8858.permissions.compose.isDenied
import com.peihua8858.permissions.compose.rememberPermissionState
import com.peihua8858.permissions.compose.rememberPermissionsState
import com.peihua8858.permissions.compose.shouldShowRationale
import com.peihua8858.permissions.core.requestPermission
import com.peihua8858.permissions.theme.ComposeDemoTheme
import com.peihua8858.permissions.theme.MarketFontFamily

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        this.setContent {
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
//                        val permissionState =
//                            com.google.accompanist.permissions.rememberPermissionState(Manifest.permission.READ_PHONE_NUMBERS)
                        val permissionState3 =
                            rememberPermissionState(Manifest.permission.READ_PHONE_NUMBERS)
                        val permissionState2 = remember { mutableStateOf(false) }
                        val permissionState4 = rememberPermissionsState(
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.CAMERA
                        )
//                        val permissionState5 = com.google.accompanist.permissions.rememberMultiplePermissionsState(
//                            listOf(Manifest.permission.READ_MEDIA_IMAGES,
//                                Manifest.permission.CAMERA)
//                        )
                        if (permissionState3.status.isGranted) {
                            showToast("权限已获取")
                        }else if (permissionState3.status.shouldShowRationale) {
                            showToast("权限被拒绝,需要解释")
                        }else if (permissionState3.status.isDenied) {
                            showToast("权限被拒绝")
                        }
                        if (permissionState4.allPermissionsGranted) {
                            showToast("权限已获取")
                        } else if (permissionState4.shouldShowRationale) {
                            showToast("权限被拒绝,需要解释")
                        } else if (permissionState4.deniedPermanently) {
                            showToast("权限被拒绝")
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(innerPadding)
                        ) {
//                            Text(
//                                text = "Android Google获取单个权限 ${permissionState.status.isGranted}",
//                                fontFamily = MarketFontFamily.NotoSansSc500,
//                                modifier = Modifier.clickable {
//                                    permissionState.launchPermissionRequest()
//                                }
//                            )
//                            Text(
//                                text = "Android Google获取多个权限 ${permissionState5.allPermissionsGranted}",
//                                fontFamily = MarketFontFamily.NotoSansSc500,
//                                modifier = Modifier.clickable {
//                                    permissionState5.launchMultiplePermissionRequest()
//                                }
//                            )
                            Text(
                                text = "Android 获取单个权限 ${permissionState3.status.isGranted}",
                                fontFamily = MarketFontFamily.NotoSansSc500,
                                modifier = Modifier.clickable {
                                    permissionState3.launchPermissionRequest()
                                }
                            )
                            Text(
                                text = "Android 获取多个权限 ${permissionState4.allPermissionsGranted}",
                                fontFamily = MarketFontFamily.NotoSansSc500,
                                modifier = Modifier.clickable {
                                    permissionState4.launchMultiplePermissionRequest()
                                }
                            )
                            Text(
                                text = "Android 获取多个权限 ${permissionState2.value}",
                                fontFamily = MarketFontFamily.NotoSansSc500,
                                modifier = Modifier.clickable {
                                    requestPermission(Manifest.permission.READ_PHONE_NUMBERS) {
                                        onDenied {
                                            showToast("权限被拒绝")
                                            permissionState2.value = false
                                        }
                                        onShowRationale {
                                            showToast("权限被拒绝,需要解释")
                                            permissionState2.value = false
                                        }
                                        onGranted {
                                            showToast("权限已获取")
                                            permissionState2.value = true
                                        }
                                    }
                                }
                            )

                        }
                    }
                }

            }
        }
    }

}
