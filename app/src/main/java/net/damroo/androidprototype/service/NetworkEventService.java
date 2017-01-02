package net.damroo.androidprototype.service;


import net.damroo.androidprototype.events.OrdersDownloadEvent;
import net.damroo.androidprototype.service.order.OrderNetworkService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;


public class NetworkEventService {

    DaggerComponent network;

    private OrderNetworkService orderNetworkService;


    @Inject
    public NetworkEventService(OrderNetworkService orderNetworkService) {
        network = DaggerDaggerComponent.create();
        network.inject(this);
        this.orderNetworkService = orderNetworkService;
    }


    // network jobs in asynchronous mode
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getFirstUse(OrdersDownloadEvent event) {
        orderNetworkService.downloadOrder(event);
    }


}

