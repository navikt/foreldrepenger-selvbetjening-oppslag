package no.nav.foreldrepenger.person;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import no.nav.arbeid.cxfclient.CXFClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration {

	@Bean
	public BarneVelger barneVelger(PersonV3 personV3,
	        @Value("${foreldrepenger.selvbetjening.maxmonthsback:12}") int months) {
		return new BarnMorRelasjonSjekkendeBarneVelger(months);
	}

	@Bean
	public PersonV3 personV3(@Value("${PERSON_V3_ENDPOINTURL}") String serviceUrl) {
		return new CXFClient<>(PersonV3.class).configureStsForSystemUser().address(serviceUrl).build();
	}

	@Bean
	public PersonKlient prsonKlient(BarneVelger barneVelger, PersonV3 person) {
		return new PersonKlient(person, barneVelger);
	}
}