package no.nav.foreldrepenger.stub;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.ws.person.AnnenForelder;
import no.nav.foreldrepenger.lookup.ws.person.Bankkonto;
import no.nav.foreldrepenger.lookup.ws.person.Barn;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.lookup.ws.person.ID;
import no.nav.foreldrepenger.lookup.ws.person.Kjønn;
import no.nav.foreldrepenger.lookup.ws.person.Navn;
import no.nav.foreldrepenger.lookup.ws.person.Person;
import no.nav.foreldrepenger.lookup.ws.person.PersonClient;

public class PersonClientStub implements PersonClient {
    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Timed("lookup.person")
    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Anne", "Lene", "Sveen", Kjønn.K);
        return new Person(id, CountryCode.NO, Kjønn.valueOf("M"), navn,
                "NN", new Bankkonto("1234567890", "Stub NOR"),
                now().minusYears(20), barn(id.getFnr()));
    }

    @Override
    public void ping() {
        LOG.info("PONG");
    }

    private List<Barn> barn(Fødselsnummer fnrMor) {
        Barn barn = new Barn(fnrMor,
                new Fødselsnummer("01011812345"),
                now().minusYears(1),
                new Navn("Mo", null, "Sveen", Kjønn.M),
                new AnnenForelder(new Navn("Anne", "N", "Forelder", Kjønn.K), new Fødselsnummer("01019012345"),
                        now().minusYears(25)));
        return singletonList(barn);
    }

    @Override
    public Navn navn(Fødselsnummer fnr) {
        return new Navn("Ole", "Gunnar", "Solskjær", Kjønn.M);
    }
}
