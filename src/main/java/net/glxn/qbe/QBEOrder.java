package net.glxn.qbe;

import net.glxn.qbe.types.*;

public class QBEOrder {
    private final String fieldToOrderBy;
    private final Order order;

    public QBEOrder(String fieldToOrderBy, Order order) {
        this.fieldToOrderBy = fieldToOrderBy;
        this.order = order;
    }

    public String getFieldToOrderBy() {
        return fieldToOrderBy;
    }

    public Order getOrder() {
        return order;
    }
}
