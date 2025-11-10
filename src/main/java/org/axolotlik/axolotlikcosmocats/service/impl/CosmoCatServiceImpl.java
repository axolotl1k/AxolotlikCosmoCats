package org.axolotlik.axolotlikcosmocats.service.impl;

import org.axolotlik.axolotlikcosmocats.service.CosmoCatService;
import org.axolotlik.axolotlikcosmocats.service.exception.CatNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatServiceImpl implements CosmoCatService {

  private final List<String> cosmoCats = List.of("Luna", "Comet", "Nebula", "Orion");

  @Override
  public List<String> getCosmoCats() {
    return cosmoCats;
  }

  @Override
  public String getCosmoCatByName(String name) {
    return cosmoCats.stream()
        .filter(cat -> cat.equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new CatNotFoundException(name));
  }
}
