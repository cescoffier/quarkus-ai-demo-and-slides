package io.quarkus.presentation.service;

import io.quarkus.presentation.model.Order;
import io.quarkus.presentation.model.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class OrderService {

    public Optional<Order> findById(long id) {
        return Optional.ofNullable(Order.findById(id));
    }

    @Transactional
    public boolean cancelOrder(long id) {
        return findById(id).map(order -> {
            order.status = OrderStatus.CANCELLED;
            return true;
        }).orElse(false);
    }
}
