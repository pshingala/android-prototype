package net.damroo.androidprototype.service;

import net.damroo.androidprototype.retrofit.EPRestAdapter;
import net.damroo.androidprototype.service.order.OrderDBService;
import net.damroo.androidprototype.service.order.OrderNetworkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class DaggerModule {

    @Provides
    @Singleton
    EPRestAdapter provideEPRestAdapter() {
        return new EPRestAdapter();
    }


    @Provides
    @Singleton
    NetworkEventService provideNetworkEventService() {
        DBEventService dbService = new DBEventService();
        OrderNetworkService service = new OrderNetworkService(new EPRestAdapter(), new OrderDBService(dbService));
        return new NetworkEventService(service);
    }


    @Provides
    @Singleton
    DBEventService provideDBEventServiceBac() {
        return new DBEventService();
    }


    @Provides
    @Singleton
    OrderNetworkService provideOrderNetworkService() {
        DBEventService dbService = new DBEventService();
        return new OrderNetworkService(new EPRestAdapter(), new OrderDBService(dbService));
    }

}