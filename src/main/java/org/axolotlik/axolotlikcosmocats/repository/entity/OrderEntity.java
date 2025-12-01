package org.axolotlik.axolotlikcosmocats.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.axolotlik.axolotlikcosmocats.common.OrderStatus;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
  @SequenceGenerator(name = "order_seq_gen", sequenceName = "orders_id_seq")
  private Long id;

  @Column(name = "total_price")
  private Double totalPrice;

  @Enumerated(EnumType.ORDINAL)
  private OrderStatus status;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "orders_products",
          joinColumns = @JoinColumn(name = "order_id"),
          inverseJoinColumns = @JoinColumn(name = "product_id"))
  private List<ProductEntity> products;
}

