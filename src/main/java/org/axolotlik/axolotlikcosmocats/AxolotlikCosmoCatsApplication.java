package org.axolotlik.axolotlikcosmocats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AxolotlikCosmoCatsApplication {

  public static void main(String[] args) {
    SpringApplication.run(AxolotlikCosmoCatsApplication.class, args);
  }
}
