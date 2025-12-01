package org.axolotlik.axolotlikcosmocats.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq_gen")
  @SequenceGenerator(name = "cart_seq_gen", sequenceName = "carts_id_seq")
  private Long id;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "carts_products",
      joinColumns = @JoinColumn(name = "cart_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id"))
  private List<ProductEntity> products;
}
