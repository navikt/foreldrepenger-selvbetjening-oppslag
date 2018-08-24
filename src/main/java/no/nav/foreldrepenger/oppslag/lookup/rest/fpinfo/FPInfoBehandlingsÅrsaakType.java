package no.nav.foreldrepenger.oppslag.lookup.rest.fpinfo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FPInfoBehandlingsÅrsaakType {
    RE_MANGLER_FØDSEL("RE-MF"), RE_MANGLER_FØDSEL_I_PERIODE("RE-MFIP"), RE_AVVIK_ANTALL_BARN(
            "RE-AVAB"), RE_FEIL_I_LOVANDVENDELSE("RE-LOV"), RE_FEIL_REGELVERKSFORSTÅELSE(
                    "RE-MF"), RE_FEIL_ELLER_ENDRET_FAKTA("RE-FEFAKTA"), RE_FEIL_PROSESSUELL(
                            "RE-PRSSL"), RE_ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER"), RE_ENDRET_INNTEKTSMELDING(
                                    "RE-END-INNTEKTSMELD"), BERØRT_BEHANDLING("BERØRT-BEHANDLING"), KØET_BEHANDLING(
                                            "KØET-BEHANDLING"), RE_ANNET("RE-ANNET"), RE_KLAGE(
                                                    "RE-KLAG"), RE_OPPLYSNINGER_OM_MEDLEMSKAP(
                                                            "RE-MDL"), RE_OPPLYSNINGER_OM_OPPTJENING(
                                                                    "RE-OPTJ"), RE_OPPLYSNINGER_OM_FORDELING(
                                                                            "RE-FRDLING"), RE_OPPLYSNINGER_OM_INNTEKT(
                                                                                    "RE-INNTK"), RE_OPPLYSNINGER_OM_DØD(
                                                                                            "RE-DØD"), RE_OPPLYSNINGER_OM_SØKERS_REL(
                                                                                                    "RE-SRTB"), RE_OPPLYSNINGER_OM_SØKNAD_FRIST(
                                                                                                            "RE-FRIST"), RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG(
                                                                                                                    "RE-BER-GRUN"), ETTER_KLAGE(
                                                                                                                            "ETTER_KLAGE"), RE_HENDELSE_FØDSEL(
                                                                                                                                    "RE-HENDELSE-FØDSEL"), RE_REGISTEROPPLYSNING(
                                                                                                                                            "RE-REGISTEROPPL"), RE_OPPLYSNINGER_OM_YTELSER(
                                                                                                                                                    "RE-YTELSE"), RE_TILSTØTENDE_YTELSE_INNVILGET(
                                                                                                                                                            "RE-TILST-YT-INNVIL"), RE_ENDRING_BEREGNINGSGRUNNLAG(
                                                                                                                                                                    "RE-ENDR-BER-GRUN"), RE_TILSTØTENDE_YTELSE_OPPHØRT(
                                                                                                                                                                            "RE-TILST-YT-OPPH");

    @JsonValue
    private final String beskrivelse;

    FPInfoBehandlingsÅrsaakType(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
