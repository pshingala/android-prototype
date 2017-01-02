package net.damroo.androidprototype.service.order;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.damroo.androidprototype.database.model.OrderModel;
import net.damroo.androidprototype.database.model.OrderModel_Table;
import net.damroo.androidprototype.service.DBEventService;
import net.damroo.androidprototype.service.DaggerComponent;
import net.damroo.androidprototype.service.DaggerDaggerComponent;

import javax.inject.Inject;

public class OrderDBService {

    private DaggerComponent network;

    private DBEventService service;

    @Inject
    public OrderDBService(DBEventService service) {
        network = DaggerDaggerComponent.create();
        network.inject(this);
        this.service = service;
    }


    public boolean ordersExist() {
        OrderModel orderModel = SQLite.select().from(OrderModel.class).querySingle();
        if (orderModel == null)
            return false;
        return true;
    }


    public OrderModel getOldestDBOrder() {
        OrderModel orderModel = SQLite.select().from(OrderModel.class).where().orderBy(OrderModel_Table.creationDate, true).querySingle();
        return orderModel;
    }


    public OrderModel getLatestDBOrder() {
        OrderModel orderModel = SQLite.select().from(OrderModel.class).where().orderBy(OrderModel_Table.creationDate, false).querySingle();
        return orderModel;
    }


    public OrderModel getOrderByOrderId(String orderId) {
        OrderModel orderModel = SQLite.select().from(OrderModel.class).where(OrderModel_Table.orderId.is(orderId)).querySingle();
        return orderModel;
    }


}
