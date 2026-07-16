package br.com.giramundo.store.template;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HomeHeroTemplateTest {

    @Test
    void usesTheStorefrontImageWithoutTheLegacyLogoCopy() throws IOException {
        try (var input = HomeHeroTemplateTest.class.getResourceAsStream("/templates/index.html")) {
            assertThat(input).isNotNull();
            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(template)
                    .contains("class=\"hero hero--storefront\"")
                    .contains("Vista sua estrada. Carregue sua identidade.")
                    .contains("href=\"#loja\"")
                    .doesNotContain("hero-card--storefront")
                    .doesNotContain("img/logo-giramundo.png")
                    .doesNotContain("GIRAMUNDO ORIGINAL")
                    .doesNotContain("Loja pronta para backend Spring");
        }
    }
}
