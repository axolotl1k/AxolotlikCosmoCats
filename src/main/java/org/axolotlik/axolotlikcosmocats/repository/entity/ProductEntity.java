package org.axolotlik.axolotlikcosmocats.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "products",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "category_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
  @SequenceGenerator(name = "product_seq_gen", sequenceName = "products_id_seq")
  private Long id;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private Double price;

  private boolean available;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private CategoryEntity category;
}
