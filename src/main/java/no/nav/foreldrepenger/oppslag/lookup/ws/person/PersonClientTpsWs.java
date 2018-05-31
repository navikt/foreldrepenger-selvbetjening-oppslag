package no.nav.foreldrepenger.oppslag.lookup.ws.person;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.errorhandling.ForbiddenException;
import no.nav.foreldrepenger.oppslag.errorhandling.NotFoundException;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.BANKKONTO;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.KOMMUNIKASJON;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;

public class PersonClientTpsWs implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);

    private final PersonV3 person;

    private static final Counter ERROR_COUNTER = Metrics.counter("person.lookup.error");

    public PersonClientTpsWs(PersonV3 person) {
        this.person = Objects.requireNonNull(person);
    }

    @Override
    public void ping() {
        person.ping();
    }

    @Override
    public Person hentPersonInfo(ID id) {

        try {
            LOG.info("Doing person lookup");
            HentPersonRequest request = RequestUtils.request(id.getFnr(), KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson =
                hentPerson(id, request).getPerson();
            return PersonMapper.map(id, tpsPerson);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }

    }

    private HentPersonResponse hentPerson(ID id, HentPersonRequest request) {
        try {
            return person.hentPerson(request);
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Fant ikke person {}", id.getAktorId().getAktør(), e);
            throw new NotFoundException(e);
        } catch (HentPersonSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved oppslag.", e);
            throw new ForbiddenException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + "]";
    }

}
