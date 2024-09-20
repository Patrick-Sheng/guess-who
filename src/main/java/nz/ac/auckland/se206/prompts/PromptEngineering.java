package nz.ac.auckland.se206.prompts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Utility class for prompt engineering. This class provides methods to load and fill prompt
 * templates with dynamic data.
 */
public class PromptEngineering {

  /**
   * Retrieves a prompt template, fills it with the provided data, and returns the filled prompt.
   *
   * @param promptId the ID of the prompt template to load
   * @param data the data to fill into the template
   * @return the filled prompt
   * @throws IllegalArgumentException if there is an error loading or filling the template
   */
  public static String getPrompt(String promptId, Map<String, String> data) {
    try {
      // Load the prompt template file from resources
      URL resourceUrl = PromptEngineering.class.getClassLoader().getResource("prompts/" + promptId);

      if (data == null) {
        return loadTemplate(resourceUrl.toURI());
      } else {
        return fillTemplate(loadTemplate(resourceUrl.toURI()), data);
      }
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Error loading or filling the prompt template.", e);
    }
  }

  /**
   * Loads the content of a template file from the specified file path.
   *
   * @param filePath the URI of the file to load
   * @return the content of the template file as a string
   * @throws IOException if there is an error reading the file
   */
  private static String loadTemplate(URI filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)));
  }

  /**
   * Fills a template string with the provided data. Replaces placeholders in the template with
   * corresponding values from the data map.
   *
   * @param template the template string to fill
   * @param data the data to fill into the template
   * @return the filled template string
   */
  private static String fillTemplate(String template, Map<String, String> data) {
    for (Map.Entry<String, String> entry : data.entrySet()) {
      template = template.replace("{" + entry.getKey() + "}", entry.getValue());
    }
    return template;
  }
}
