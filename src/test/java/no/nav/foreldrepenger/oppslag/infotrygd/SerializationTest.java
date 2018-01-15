package no.nav.foreldrepenger.oppslag.infotrygd;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.foreldrepenger.oppslag.domain.Ytelse;

public class SerializationTest {

	private static ObjectMapper mapper;

	@BeforeAll
	public static void beforeClass() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new Jdk8Module());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	@Test
	public void testYtelse() throws Exception {
		Ytelse ytelse = ytelse();
		String serialized = write(ytelse);
		Ytelse deserialized = mapper.readValue(serialized, Ytelse.class);
		assertEquals(ytelse, deserialized);
	}

	private Ytelse ytelse() {
		return new Ytelse("hello", "world", LocalDate.now().minus(Period.ofYears(2)), Optional.of(LocalDate.now().minus(Period.ofYears(1))) ,"Infotrygd");
	}

	private String write(Object obj) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}
}