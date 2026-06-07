package com.example.forcethernet

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TetheringAccessibilityService : AccessibilityService() {
    private var isTaskPending = false

    companion object {
        private const val TETHERING_TEXT = "Ethernet tethering"
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_ENABLE_TETHERING") {
            isTaskPending = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isTaskPending) return
        
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText(TETHERING_TEXT)
        
        if (nodes.isNullOrEmpty()) {
            rootNode.recycle()
            return
        }

        for (node in nodes) {
            val parent = node.parent
            if (parent != null) {
                var isAlreadyEnabled = parent.isChecked
                if (!isAlreadyEnabled) {
                    for (i in 0 until parent.childCount) {
                        val child = parent.getChild(i)
                        if (child != null) {
                            if (child.isChecked) {
                                isAlreadyEnabled = true
                            }
                            child.recycle()
                        }
                        if (isAlreadyEnabled) break
                    }
                }

                if (!isAlreadyEnabled) {
                    if (parent.isClickable || parent.isCheckable) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            performGlobalAction(GLOBAL_ACTION_BACK)
                        }, 500)
                        isTaskPending = false
                    }
                } else {
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    isTaskPending = false
                }
                parent.recycle()
                if (!isTaskPending) break
            }
            node.recycle()
        }
        
        // Clean up remaining nodes in the list
        for (node in nodes) {
            try {
                node.recycle()
            } catch (e: Exception) {
                // Ignore already recycled
            }
        }
        rootNode.recycle()
    }

    override fun onInterrupt() {}
}
