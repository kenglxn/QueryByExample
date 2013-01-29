package net.glxn.qbe;

public class QBEOrder {
    private final String fieldToOrderBy;
    private final OrderType orderType;

    public QBEOrder(String fieldToOrderBy, OrderType orderType) {
        this.fieldToOrderBy = fieldToOrderBy;
        this.orderType = orderType;
    }

    public String getFieldToOrderBy() {
        return fieldToOrderBy;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
