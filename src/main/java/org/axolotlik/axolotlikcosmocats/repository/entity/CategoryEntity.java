package org.axolotlik.axolotlikcosmocats.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq_gen")
  @SequenceGenerator(name = "category_seq_gen", sequenceName = "categories_id_seq")
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @OneToMany(mappedBy = "category")
  private List<ProductEntity> products = new ArrayList<>();
}
