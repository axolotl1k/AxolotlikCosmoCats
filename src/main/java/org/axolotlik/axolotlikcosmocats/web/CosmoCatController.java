package org.axolotlik.axolotlikcosmocats.web;

import org.axolotlik.axolotlikcosmocats.featuretoggle.FeatureToggles;
import org.axolotlik.axolotlikcosmocats.featuretoggle.annotation.FeatureToggle;
import org.axolotlik.axolotlikcosmocats.service.CosmoCatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cosmo-cats")
@RequiredArgsConstructor
public class CosmoCatController {

  private final CosmoCatService cosmoCatService;

  @FeatureToggle(FeatureToggles.COSMO_CATS)
  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<List<String>> getCosmoCats() {
    return ResponseEntity.ok(cosmoCatService.getCosmoCats());
  }

  @FeatureToggle(FeatureToggles.COSMO_CATS)
  @GetMapping("/{name}")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<String> getCosmoCat(@PathVariable String name) {
    return ResponseEntity.ok(cosmoCatService.getCosmoCatByName(name));
  }
}
