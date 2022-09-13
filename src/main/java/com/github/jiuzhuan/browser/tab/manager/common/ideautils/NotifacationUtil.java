package com.github.jiuzhuan.browser.tab.manager.common.ideautils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * 气泡通知
 *
 * @date 2022/8/12 17:28
 */
public class NotifacationUtil {

    public static void info(String title, String msg) {
        Notifications.Bus.notify(
                new Notification("Browser Tab Manager Notification Group", title, msg, NotificationType.INFORMATION)
        );
    }

    public static void error(String title, String msg) {
        Notifications.Bus.notify(
                new Notification("Browser Tab Manager Notification Group", title, msg, NotificationType.ERROR)
        );
    }

    public static void notify(Boolean success, String title, String msg) {
        if (Boolean.TRUE.equals(success)) {
            info(title, msg);
        } else {
            error(title, msg);
        }
    }
}
