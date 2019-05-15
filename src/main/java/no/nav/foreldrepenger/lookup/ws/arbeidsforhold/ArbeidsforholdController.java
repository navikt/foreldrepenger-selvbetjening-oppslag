package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import static no.nav.foreldrepenger.lookup.Constants.ISSUER;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@ProtectedWithClaims(issuer = ISSUER, claimMap = { "acr=Level4" })
public class ArbeidsforholdController {
    public static final String ARBEIDSFORHOLD = "/arbeidsforhold";
    private final ArbeidsforholdClient arbeidsforholdClient;
    private final TokenUtil tokenUtil;

    public ArbeidsforholdController(ArbeidsforholdClient arbeidsforholdClient, TokenUtil tokenUtil) {
        this.arbeidsforholdClient = arbeidsforholdClient;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping(ARBEIDSFORHOLD)
    public List<Arbeidsforhold> workHistory() {
        return arbeidsforholdClient.aktiveArbeidsforhold(new Fødselsnummer(tokenUtil.autentisertBruker()));
    }

    @GetMapping("/navn")
    public String arbeidsgiverNavn(@RequestParam(name = "orgnr") String orgnr) {
        return arbeidsforholdClient.arbeidsgiverNavn(orgnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforholdClient=" + arbeidsforholdClient + ", tokenUtil="
                + tokenUtil + "]";
    }
}
