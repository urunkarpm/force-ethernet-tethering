package com.example.forcethernet

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class TetheringAccessibilityService : AccessibilityService() {

    companion object {
        private const val TETHERING_TEXT = "Ethernet tethering"
        @Volatile
        var isTaskPending = false
    }

    @Suppress("DEPRECATION")
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isTaskPending) return
        
        val rootNode = rootInActiveWindow ?: return
        val nodes = rootNode.findAccessibilityNodeInfosByText(TETHERING_TEXT)
        
        if (nodes.isNullOrEmpty()) {
            rootNode.recycle()
            return
        }

        for (node in nodes) {
            val parent = node.parent ?: continue
            var switchNode: AccessibilityNodeInfo? = null
            var clickableNode: AccessibilityNodeInfo? = null
            
            // Find the Switch sibling and a clickable node
            for (i in 0 until parent.childCount) {
                val child = parent.getChild(i) ?: continue
                if (child.className?.toString()?.contains("Switch") == true || child.isCheckable) {
                    switchNode = child
                }
                if (child.isClickable) {
                    clickableNode = child
                }
                if (switchNode == null || clickableNode == null) {
                    // Search one level deeper for the switch in case it's wrapped
                    for (j in 0 until child.childCount) {
                        val grandChild = child.getChild(j) ?: continue
                        if (grandChild.className?.toString()?.contains("Switch") == true || grandChild.isCheckable) {
                            switchNode = grandChild
                        }
                    }
                }
            }

            // Fallback to parent if no specific clickable node found
            if (clickableNode == null && parent.isClickable) {
                clickableNode = parent
            }

            val isAlreadyEnabled = switchNode?.isChecked ?: parent.isChecked

            if (!isAlreadyEnabled) {
                if (clickableNode != null) {
                    clickableNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        performGlobalAction(GLOBAL_ACTION_BACK)
                    }, 500)
                    isTaskPending = false
                } else if (switchNode != null && switchNode.isClickable) {
                    switchNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        performGlobalAction(GLOBAL_ACTION_BACK)
                    }, 500)
                    isTaskPending = false
                }
            } else {
                // Already enabled
                performGlobalAction(GLOBAL_ACTION_BACK)
                isTaskPending = false
            }
            parent.recycle()
            if (!isTaskPending) break
        }
        
        // Clean up remaining nodes
        for (node in nodes) {
            try {
                node.recycle()
            } catch (e: Exception) {
                // Ignore
            }
        }
        rootNode.recycle()
    }

    override fun onInterrupt() {}
}
