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
            if (parent != null) {
                // Check if already enabled. 
                // Note: AccessibilityNodeInfo might have a Switch or Checkbox as a child or as the node itself.
                val isAlreadyEnabled = parent.isChecked || (0 until parent.childCount).any { 
                    parent.getChild(it)?.isChecked == true 
                }

                if (!isAlreadyEnabled) {
                    if (parent.isClickable || parent.isCheckable) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        // Delay before going back to ensure toggle is processed
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            performGlobalAction(GLOBAL_ACTION_BACK)
                        }, 500)
                    }
                } else {
                    // Already ON, just go back
                    performGlobalAction(GLOBAL_ACTION_BACK)
                }
                isTaskPending = false
                return
            }
        }
    }

    override fun onInterrupt() {}
}
