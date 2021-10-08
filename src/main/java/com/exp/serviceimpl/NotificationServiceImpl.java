package com.exp.serviceimpl;

import com.exp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationJob notificationJob;

    @Override
    public void sendPushNotif() {
    }
}
