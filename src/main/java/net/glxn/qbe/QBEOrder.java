package net.glxn.qbe;

import net.glxn.qbe.types.*;

public class QBEOrder {
    private final String orderBy;
    private final Order order;

    public QBEOrder(String fieldToOrderBy, Order order) {
        this.orderBy = fieldToOrderBy;
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Order getOrder() {
        return order;
    }
}
