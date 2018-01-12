package no.nav.foreldrepenger.oppslag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import no.nav.modig.testcertificates.TestCertificates;

@SpringBootApplication
@ComponentScan("no.nav.foreldrepenger.oppslag")
public class OppslagApplication {

	public static void main(String[] args) {
		setProperty("no.nav.modig.security.sts.url", System.getProperty("SECURITYTOKENSERVICE_URL"));
		setProperty("no.nav.modig.security.systemuser.username", System.getProperty("FPSELVBETJENING_USERNAME"));
		setProperty("no.nav.modig.security.systemuser.password", System.getProperty("FPSELVBETJENING_PASSWORD"));

		TestCertificates.setupKeyAndTrustStore();

		SpringApplication.run(OppslagApplication.class, args);
	}

	private static void setProperty(String key, String value) {
		if (value == null) {
			throw new IllegalArgumentException("Key " + key + " har ingen verdi");
		}
		System.setProperty(key, value);
	}

}
