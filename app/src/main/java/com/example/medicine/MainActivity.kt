

package com.example.medicine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.medicine.ui.navigation.AppNavigation
import com.example.medicine.ui.theme.MedicineTheme
import com.example.medicine.util.PermissionUtil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 请求通知权限
        PermissionUtil.requestNotificationPermission(this)
        setContent {
            MedicineTheme {
                AppNavigation()
            }
        }
    }
}