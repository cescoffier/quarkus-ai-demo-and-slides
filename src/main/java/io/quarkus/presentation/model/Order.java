package io.quarkus.presentation.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {
    public String product;
    public BigDecimal amount;
    @Enumerated(EnumType.STRING)
    public OrderStatus status;
    @ManyToOne
    public Customer customer;
}
