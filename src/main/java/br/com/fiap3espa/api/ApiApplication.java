// Pacote raiz: o Spring procura componentes (controllers, services, repositórios) neste pacote e nos subpacotes
package br.com.fiap3espa.api;

// Classe que inicializa uma aplicação Spring Boot
import org.springframework.boot.SpringApplication;
// Anotação que liga a configuração automática do Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API AprendIA.
 * Ao executar, o Spring Boot conecta no Oracle, roda as migrações do Flyway,
 * valida as entidades, carrega o modelo de IA e sobe o servidor HTTP na porta 8080.
 */
// Reúne @Configuration, @EnableAutoConfiguration e @ComponentScan (varre br.com.fiap3espa.api e subpacotes)
@SpringBootApplication
public class ApiApplication {

	// Método principal chamado pela JVM ao iniciar o programa
	public static void main(String[] args) {
		// Cria o contexto do Spring, configura tudo automaticamente e inicia o servidor web
		SpringApplication.run(ApiApplication.class, args);
	}

}
