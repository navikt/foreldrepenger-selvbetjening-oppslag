package no.nav.foreldrepenger.inntekt;

import static java.util.stream.Collectors.toList;

import java.time.*;
import java.util.*;

import javax.inject.*;

import org.slf4j.*;
import org.springframework.stereotype.*;

import no.nav.foreldrepenger.domain.*;
import no.nav.foreldrepenger.time.*;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.*;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.*;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.*;

@Component
public class InntektClient {
	private static final Logger log = LoggerFactory.getLogger(InntektClient.class);

	private final InntektV3 inntektV3;

	@Inject
	public InntektClient(InntektV3 inntektV3) {
		this.inntektV3 = inntektV3;
	}

	public List<Income> incomeForPeriod(String aktoerId, LocalDate from, LocalDate to) {
		HentInntektListeRequest req = request(aktoerId, from, to);
		try {
			HentInntektListeResponse res = inntektV3.hentInntektListe(req);
			return res.getArbeidsInntektIdent().getArbeidsInntektMaaned().stream()
			        .flatMap(aim -> aim.getArbeidsInntektInformasjon().getInntektListe().stream())
			        .map(InntektMapper::map).collect(toList());
		} catch (Exception ex) {
			log.warn("Error while retrieving income", ex);
			throw new RuntimeException("Error while retrieving income data: " + ex.getMessage());
		}
	}

	private HentInntektListeRequest request(String aktoerId, LocalDate from, LocalDate to) {
		HentInntektListeRequest req = new HentInntektListeRequest();

		PersonIdent person = new PersonIdent();
		person.setPersonIdent(aktoerId);
		req.setIdent(person);

		Ainntektsfilter ainntektsfilter = new Ainntektsfilter();
		ainntektsfilter.setValue("ForeldrepengerA-Inntekt");
		ainntektsfilter.setKodeRef("ForeldrepengerA-Inntekt");
		ainntektsfilter.setKodeverksRef(
		        "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
		req.setAinntektsfilter(ainntektsfilter);

		Uttrekksperiode uttrekksperiode = new Uttrekksperiode();
		uttrekksperiode.setMaanedFom(CalendarConverter.toCalendar(from));
		uttrekksperiode.setMaanedTom(CalendarConverter.toCalendar(to));
		req.setUttrekksperiode(uttrekksperiode);

		Formaal formaal = new Formaal();
		formaal.setValue("Foreldrepenger");
		formaal.setKodeRef("Foreldrepenger");
		formaal.setKodeverksRef(
		        "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
		req.setFormaal(formaal);

		return req;
	}
}