package uz.alex.its.beverlee.view.interfaces;

import uz.alex.its.beverlee.model.notification.Push;

public interface NotificationCallback {
    void onNotificationTapped(final Push item, final int position);
}
