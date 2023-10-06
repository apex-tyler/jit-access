package com.google.solutions.jitaccess.core.adapters;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.OffsetDateTime;

@ApplicationScoped
public class KubernetesAdapter {

  public String test() throws IOException, InterruptedException {

    File applyFile = createTempFile("whereami-tvaslev", "whereami",
        "tvaslev@apexfintechsolutions.com", 5);

    Runtime runtime = Runtime.getRuntime();
    Process credExec = runtime.exec(
        new String[]{"gcloud", "container", "clusters", "get-credentials",
            "flt-tst-aware-gelding-uswe1-1-24", "--region", "us-west1", "--project",
            "apex-gke-fleet-tst-00"});

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

    private static File createTempFile(String resourceName, String namespace, String userEmail, int minutes) throws IOException {
      try (InputStream in = KubernetesAdapter.class.getResourceAsStream("/transientrolebinding-template.yaml")) {
        String file = new String(in.readAllBytes())
            .replace("{{NAME}}", resourceName)
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

