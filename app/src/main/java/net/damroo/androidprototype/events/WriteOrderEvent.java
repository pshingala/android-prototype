package net.damroo.androidprototype.events;

import net.damroo.androidprototype.database.model.OrderModel;

public class WriteOrderEvent {
    public final OrderModel orderModel;

    public WriteOrderEvent(OrderModel orderModel) {
        this.orderModel = orderModel;
    }
}
