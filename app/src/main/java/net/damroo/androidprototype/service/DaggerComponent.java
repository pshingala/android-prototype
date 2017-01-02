package net.damroo.androidprototype.service;

import net.damroo.androidprototype.activity.OrderListViewActivity;
import net.damroo.androidprototype.service.order.OrderDBService;
import net.damroo.androidprototype.service.order.OrderNetworkService;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {DaggerModule.class})
public interface DaggerComponent {

    void inject(NetworkEventService service);

    void inject(OrderListViewActivity orderListViewActivity);

    void inject(OrderNetworkService orderNetworkService);

    void inject(OrderDBService orderDBService);

}
