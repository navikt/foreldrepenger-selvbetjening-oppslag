package no.nav.foreldrepenger.oppslag.lookup.ws.medl;

import no.nav.foreldrepenger.oppslag.time.DateUtil;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Medlemsperiode;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.GrunnlagstypeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.LandkodeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.PeriodetypeMedTerm;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.kodeverk.StatuskodeMedTerm;

import java.time.LocalDate;

public class TestdataProvider {

   public static Medlemsperiode medlemsperiode(LocalDate from, LocalDate to) {
      Medlemsperiode periode = new Medlemsperiode();
      periode.setFraOgMed(DateUtil.toXMLGregorianCalendar(from));
      periode.setTilOgMed(DateUtil.toXMLGregorianCalendar(to));
      StatuskodeMedTerm status = new StatuskodeMedTerm();
      status.setTerm("statusen");
      periode.setStatus(status);
      PeriodetypeMedTerm type = new PeriodetypeMedTerm();
      type.setTerm("typen");
      periode.setType(type);
      GrunnlagstypeMedTerm grunnlag = new GrunnlagstypeMedTerm();
      grunnlag.setTerm("grunnlaget");
      periode.setGrunnlagstype(grunnlag);
      LandkodeMedTerm land = new LandkodeMedTerm();
      land.setTerm("landet");
      periode.setLand(land);
      return periode;
   }

}
