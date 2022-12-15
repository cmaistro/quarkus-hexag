package com.example.quarkus.infrastructure.repositories.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tb_order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID externalId;
    private UUID customerId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "order_id_fk"))
    private List<OrderItemEntity> orderItems;
    private BigDecimal discount;
    private BigDecimal freightValue;

    @Override
    public String toString() {
        return "OrderEntity{" + "id=" + id + ", externalId=" + externalId + ", customerId=" + customerId + ", discount="
                + discount + ", freightValue=" + freightValue + '}';
    }
}
