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

  public String test(String cluster, String region, String project, String namespace, String userEmail, int minutes) throws IOException, InterruptedException {
    return kubernetesAdapter.test(cluster, region, project, namespace, userEmail, minutes);
  }

}
