package org.axolotlik.axolotlikcosmocats.service;

import java.util.List;

public interface CosmoCatService {

  List<String> getCosmoCats();

  String getCosmoCatByName(String name);
}
