package net.damroo.androidprototype.service;


import android.util.Log;

import net.damroo.androidprototype.events.WriteOrderEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class DBEventService {

    public DBEventService() {
    }

    // DB jobs in queue mode (background mode executes in sequence)
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void writeOrder(WriteOrderEvent event) {
        try {
            event.orderModel.save();
        } catch (Exception e) {
            // do nothing
        }
    }

}
