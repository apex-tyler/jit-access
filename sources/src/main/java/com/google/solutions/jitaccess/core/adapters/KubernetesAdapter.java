package com.google.solutions.jitaccess.core.adapters;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@ApplicationScoped
public class KubernetesAdapter {

  // flt-tst-aware-gelding-uswe1-1-24
  // us-west1
  // apex-gke-fleet-tst-00
  // whereami
  // tvaslev@apexfintechsolutions.com
  // 5
  public String test(String cluster, String region, String project, String namespace, String userEmail, int minutes) throws IOException, InterruptedException {

    File applyFile = createTempFile(namespace,
        userEmail, minutes);
    Runtime runtime = Runtime.getRuntime();
    Process credExec = runtime.exec(
        new String[]{"gcloud", "container", "clusters", "get-credentials",
            cluster, "--region", region, "--project",
            project});

    if (credExec.waitFor() == 0) {
      Process applyExec = runtime.exec(new String[]{"kubectl", "apply", "-f", applyFile.getAbsolutePath()});
      if (applyExec.waitFor() == 0) {
        return readStream(applyExec.inputReader());
      } else {
        throw new RuntimeException(readStream(applyExec.errorReader()));
      }
    } else {
      throw new RuntimeException(readStream(credExec.errorReader()));
    }

  }

    private static String readStream(BufferedReader reader) throws IOException {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
        sb.append(line).append("\n");
      }
      return sb.toString();
    }

    private static File createTempFile(String namespace, String userEmail, int minutes) throws IOException {
      try (InputStream in = KubernetesAdapter.class.getResourceAsStream("/transientrolebinding-template.yaml")) {
        String file = new String(in.readAllBytes())
            .replace("{{NAME}}", String.format("%s-%s", namespace, userEmail.substring(0, userEmail.indexOf("@"))))
            .replace("{{NAMESPACE}}", namespace)
            .replace("{{USER}}", userEmail)
            .replace("{{VALID_FROM}}", Instant.now().toString())
            .replace("{{VALID_UNTIL}}", Instant.now().plusSeconds(60 * minutes).toString());
        File tempFile = File.createTempFile("jit-", ".yaml");
        try (FileWriter writer = new FileWriter(tempFile)) {
          writer.write(file);
        }
        return tempFile;
      }
    }


}

