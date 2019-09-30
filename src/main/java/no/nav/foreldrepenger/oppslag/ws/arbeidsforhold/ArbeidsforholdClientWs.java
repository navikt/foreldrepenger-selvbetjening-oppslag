package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.oppslag.util.RetryUtil.DEFAULT_RETRIES;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.errorhandling.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;

public class ArbeidsforholdClientWs implements ArbeidsforholdTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);
    private final ArbeidsforholdV3 arbeidsforholdV3;
    private final ArbeidsforholdV3 healthIndicator;
    private final OrganisasjonClient orgClient;
    private final TokenUtil tokenUtil;
    private final Retry retryConfig;

    ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient, TokenUtil tokenUtil) {
        this(arbeidsforholdV3, healthIndicator, orgClient, tokenUtil, retryConfig());
    }

    public ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient, TokenUtil tokenUtil, Retry retry) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.healthIndicator = healthIndicator;
        this.orgClient = orgClient;
        this.tokenUtil = tokenUtil;
        this.retryConfig = retry;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger AAreg");
            healthIndicator.ping();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        List<Arbeidsforhold> arbeidsforhold = decorateSupplier(retryConfig, () -> arbeidsforholdSiste3år(fnr.getFnr()))
                .get();
        for (Arbeidsforhold forhold : arbeidsforhold) {
            Optional<String> navn = navnFor(forhold.getArbeidsgiverId());
            navn.ifPresent(forhold::setArbeidsgiverNavn);
        }
        return arbeidsforhold;
    }

    private Optional<String> navnFor(String orgnr) {
        try {
            return nameFor(orgnr);
        } catch (SOAPFaultException e) {
            return Optional.empty();
        }
    }

    private List<Arbeidsforhold> arbeidsforholdSiste3år(String fnr) {
        try {
            FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3
                    .finnArbeidsforholdPrArbeidstaker(request(fnr));
            return response.getArbeidsforhold().stream()
                    .map(ArbeidsforholdMapper::map)
                    .filter(this::siste3år)
                    .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsfeil fra AAREG", e);
            throw new UnauthorizedException(e);
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput e) {
            throw new IncompleteRequestException(e);
        } catch (SOAPFaultException e) {
            if (tokenUtil.isExpired()) {
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        }
    }

    private static FinnArbeidsforholdPrArbeidstakerRequest request(String fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fnr);
        request.setIdent(ident);
        Regelverker regelverker = new Regelverker();
        regelverker.setValue("ALLE");
        request.setRapportertSomRegelverk(regelverker);
        return request;
    }

    private Optional<String> nameFor(String orgnr) {
        return orgClient.nameFor(orgnr);
    }

    private static Retry retryConfig() {
        return RetryUtil.retry(DEFAULT_RETRIES, "arbeidforhold", SOAPFaultException.class, LOG);
    }

    boolean siste3år(Arbeidsforhold arbeidsforhold) {
        if (arbeidsforhold.getTom().isPresent()) {
            return arbeidsforhold.getTom().get().isAfter(LocalDate.now().minusYears(3));
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforholdV3=" + arbeidsforholdV3 + ", healthIndicator="
                + healthIndicator + ", orgClient=" + orgClient + ", tokenUtil=" + tokenUtil + ", retryConfig="
                + retryConfig
                + "]";
    }

    @Override
    public String arbeidsgiverNavn(String orgnr) {
        Optional<String> navn = navnFor(orgnr);
        return navn.isPresent() ? navn.get() : null;
    }
}
