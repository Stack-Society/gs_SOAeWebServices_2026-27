// Mesmo pacote da aplicação, para o teste encontrar a classe ApiApplication
package br.com.fiap3espa.api;

// Anotação do JUnit 5 que marca um método como teste
import org.junit.jupiter.api.Test;
// Anotação que sobe o contexto completo do Spring durante o teste
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste gerado pelo Spring Initializr: verifica se a aplicação inteira consegue iniciar.
 * Atenção: como sobe tudo (inclusive a conexão com o Oracle e o Flyway),
 * precisa das variáveis DB_USERNAME e DB_PASSWORD definidas para passar.
 */
// Carrega todas as configurações e beans da aplicação, como numa execução real
@SpringBootTest
class ApiApplicationTests {

	// Teste vazio: se o contexto do Spring carregar sem erro, o teste passa
	@Test
	void contextLoads() {
	}

}
