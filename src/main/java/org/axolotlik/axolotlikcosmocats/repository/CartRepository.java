package org.axolotlik.axolotlikcosmocats.repository;

import org.axolotlik.axolotlikcosmocats.repository.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {}
