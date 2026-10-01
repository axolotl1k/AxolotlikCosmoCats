package org.axolotlik.axolotlikcosmocats.repository;

import org.axolotlik.axolotlikcosmocats.repository.entity.OrderEntity;
import org.axolotlik.axolotlikcosmocats.repository.projection.ProductSalesStats;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

  @Query(
      "SELECT p.name as productName, COUNT(p.id) as salesCount "
          + "FROM OrderEntity o "
          + "JOIN o.products p "
          + "GROUP BY p.name "
          + "ORDER BY salesCount DESC")
  List<ProductSalesStats> getTopSellingProducts(Pageable pageable);
}
