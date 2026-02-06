package com.raki.pos.order;

import com.raki.pos.model.Order;
import com.raki.pos.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithItemsDTO {
    private Order order;
    private List<OrderItem> items;
}
