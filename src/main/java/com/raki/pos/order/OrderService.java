// src/main/java/com/raki/pos/order/OrderService.java
package com.raki.pos.order;

import com.raki.pos.model.Order;
import com.raki.pos.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<OrderItem> getItemsForOrder(Long orderId) {
        return repository.findItemsByOrderId(orderId);
    }

    public List<Order> getOrdersForList(Long businessId) {
        return repository.findByBusiness(businessId);
    }

    public OrderWithItemsDTO createOrderWithItems(Order order, List<OrderItem> items) {
        Long orderId = repository.createOrder(order);

        for (OrderItem item : items) {
            item.setOrderId(orderId);
            if (item.getPaymentStatus() == null) {
                item.setPaymentStatus("UNPAID");
            }
            repository.addOrderItem(item);
        }

        Order dbOrder = repository.findById(orderId);
        List<OrderItem> dbItems = repository.findItemsByOrderId(orderId);

        return new OrderWithItemsDTO(dbOrder, dbItems);
    }

    public OrderWithItemsDTO getOrderWithItems(Long orderId) {
        Order order = repository.findById(orderId);
        List<OrderItem> items = repository.findItemsByOrderId(orderId);
        return new OrderWithItemsDTO(order, items);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return Optional.ofNullable(repository.findById(orderId));
    }

    public void save(Order order) {
        repository.updateStatus(order.getId(), order.getStatus());
    }
}
