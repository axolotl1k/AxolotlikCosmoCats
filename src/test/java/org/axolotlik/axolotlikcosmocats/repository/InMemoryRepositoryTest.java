package org.axolotlik.axolotlikcosmocats.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryRepository unit tests")
class InMemoryRepositoryTest {

  // Простий підклас для тестування generic базового репозиторію
  private static class DummyRepository extends InMemoryRepository<String> {}

  private DummyRepository repository;

  @BeforeEach
  void setUp() {
    repository = new DummyRepository();
  }

  @Test
  @DisplayName("should generate sequential IDs correctly")
  void generateIdShouldIncrementSequentially() {
    Long id1 = repository.generateId();
    Long id2 = repository.generateId();

    assertThat(id2).isEqualTo(id1 + 1);
  }

  @Test
  @DisplayName("should store and retrieve entity by ID")
  void saveAndFindByIdShouldStoreAndRetrieveEntity() {
    Long id = repository.generateId();
    repository.save(id, "cosmoCat");

    Optional<String> result = repository.findById(id);

    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo("cosmoCat");
  }

  @Test
  @DisplayName("should return all stored entities")
  void findAllShouldReturnAllSavedEntities() {
    repository.save(repository.generateId(), "alpha");
    repository.save(repository.generateId(), "beta");

    assertThat(repository.findAll()).hasSize(2).contains("alpha", "beta");
  }

  @Test
  @DisplayName("should delete entity by ID")
  void deleteByIdShouldRemoveEntity() {
    Long id = repository.generateId();
    repository.save(id, "toDelete");

    repository.deleteById(id);

    assertThat(repository.findById(id)).isEmpty();
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("should check existence of entity by ID")
  void existsByIdShouldReturnTrueIfEntityExists() {
    Long id = repository.generateId();
    repository.save(id, "exists");

    assertThat(repository.existsById(id)).isTrue();
    assertThat(repository.existsById(999L)).isFalse();
  }
}
