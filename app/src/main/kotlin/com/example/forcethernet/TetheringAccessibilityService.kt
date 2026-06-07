package com.example.forcethernet

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TetheringAccessibilityService : AccessibilityService() {
    private var isTaskPending = false

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_ENABLE_TETHERING") {
            isTaskPending = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isTaskPending) return
        
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText("Ethernet tethering")
        
        for (node in nodes) {
            val parent = node.parent
            if (parent != null && (parent.isClickable || parent.isCheckable)) {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                isTaskPending = false
                performGlobalAction(GLOBAL_ACTION_BACK)
                return
            }
        }
    }

    override fun onInterrupt() {}
}
