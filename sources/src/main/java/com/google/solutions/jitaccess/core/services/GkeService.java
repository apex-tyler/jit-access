package com.google.solutions.jitaccess.core.services;

import com.google.solutions.jitaccess.core.adapters.KubernetesAdapter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;


@ApplicationScoped
public class GkeService {

  private final KubernetesAdapter kubernetesAdapter;

  @Inject
  public GkeService(KubernetesAdapter kubernetesAdapter) {
    this.kubernetesAdapter = kubernetesAdapter;
  }

  public String test() throws IOException, InterruptedException {
    System.out.println("gke service test");
    String result = kubernetesAdapter.test();
    System.err.println(String.format("result is %s", result));
    return result;
  }

}
