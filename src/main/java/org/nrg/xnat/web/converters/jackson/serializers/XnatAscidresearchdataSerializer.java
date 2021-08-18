package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAscidresearchdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAscidresearchdataSerializer<T extends XnatAscidresearchdata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = 6526410846540259939L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAscidresearchdataSerializer() {
        this((Class<T>) XnatAscidresearchdata.class);
    }

    protected XnatAscidresearchdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "adjustmentdisorder", instance.getAdjustmentdisorder());
        writeNonBlankField(generator, "administrator", instance.getAdministrator());
        writeNonNullNumber(generator, "anxietydisorders_currentagoraphobiawithoutpanichx", instance.getAnxietydisorders_currentagoraphobiawithoutpanichx());
        writeNonNullNumber(generator, "anxietydisorders_currentanxietydisordernos", instance.getAnxietydisorders_currentanxietydisordernos());
        writeNonNullNumber(generator, "anxietydisorders_currentanxietyduetomedicalcondition", instance.getAnxietydisorders_currentanxietyduetomedicalcondition());
        writeNonNullNumber(generator, "anxietydisorders_currentgeneralizedanxietydisorder", instance.getAnxietydisorders_currentgeneralizedanxietydisorder());
        writeNonNullNumber(generator, "anxietydisorders_currentocd", instance.getAnxietydisorders_currentocd());
        writeNonNullNumber(generator, "anxietydisorders_currentpanicwithagoraphobia", instance.getAnxietydisorders_currentpanicwithagoraphobia());
        writeNonNullNumber(generator, "anxietydisorders_currentpanicwithoutagoraphobia", instance.getAnxietydisorders_currentpanicwithoutagoraphobia());
        writeNonNullNumber(generator, "anxietydisorders_currentptsd", instance.getAnxietydisorders_currentptsd());
        writeNonNullNumber(generator, "anxietydisorders_currentsocialphobia", instance.getAnxietydisorders_currentsocialphobia());
        writeNonNullNumber(generator, "anxietydisorders_currentspecificphobia", instance.getAnxietydisorders_currentspecificphobia());
        writeNonNullNumber(generator, "anxietydisorders_currentsubstanceinducedanxietydisorder", instance.getAnxietydisorders_currentsubstanceinducedanxietydisorder());
        writeNonNullNumber(generator, "anxietydisorders_pastagoraphobiawithoutpanichx", instance.getAnxietydisorders_pastagoraphobiawithoutpanichx());
        writeNonNullNumber(generator, "anxietydisorders_pastanxietydisordernos", instance.getAnxietydisorders_pastanxietydisordernos());
        writeNonNullNumber(generator, "anxietydisorders_pastanxietyduetomedicalcondition", instance.getAnxietydisorders_pastanxietyduetomedicalcondition());
        writeNonNullNumber(generator, "anxietydisorders_pastocd", instance.getAnxietydisorders_pastocd());
        writeNonNullNumber(generator, "anxietydisorders_pastpanicwithagoraphobia", instance.getAnxietydisorders_pastpanicwithagoraphobia());
        writeNonNullNumber(generator, "anxietydisorders_pastpanicwithoutagoraphobia", instance.getAnxietydisorders_pastpanicwithoutagoraphobia());
        writeNonNullNumber(generator, "anxietydisorders_pastptsd", instance.getAnxietydisorders_pastptsd());
        writeNonNullNumber(generator, "anxietydisorders_pastsocialphobia", instance.getAnxietydisorders_pastsocialphobia());
        writeNonNullNumber(generator, "anxietydisorders_pastspecificphobia", instance.getAnxietydisorders_pastspecificphobia());
        writeNonNullNumber(generator, "anxietydisorders_pastsubstanceinducedanxietydisorder", instance.getAnxietydisorders_pastsubstanceinducedanxietydisorder());
        writeNonNullNumber(generator, "eatingdisorders_currentanorexianervosa", instance.getEatingdisorders_currentanorexianervosa());
        writeNonNullNumber(generator, "eatingdisorders_currentbingeeatingdisorder", instance.getEatingdisorders_currentbingeeatingdisorder());
        writeNonNullNumber(generator, "eatingdisorders_currentbulimianervosa", instance.getEatingdisorders_currentbulimianervosa());
        writeNonNullNumber(generator, "eatingdisorders_pastanorexianervosa", instance.getEatingdisorders_pastanorexianervosa());
        writeNonNullNumber(generator, "eatingdisorders_pastbingeeatingdisorder", instance.getEatingdisorders_pastbingeeatingdisorder());
        writeNonNullNumber(generator, "eatingdisorders_pastbulimianervosa", instance.getEatingdisorders_pastbulimianervosa());
        writeNonNullNumber(generator, "mooddisorders_currentbipolar1disorder", instance.getMooddisorders_currentbipolar1disorder());
        writeNonNullNumber(generator, "mooddisorders_currentbipolar2disorder", instance.getMooddisorders_currentbipolar2disorder());
        writeNonNullNumber(generator, "mooddisorders_currentdepressivedisordernos", instance.getMooddisorders_currentdepressivedisordernos());
        writeNonNullNumber(generator, "mooddisorders_currentmajordepressivedisorder", instance.getMooddisorders_currentmajordepressivedisorder());
        writeNonNullNumber(generator, "mooddisorders_currentotherbipolardisorder", instance.getMooddisorders_currentotherbipolardisorder());
        writeNonNullNumber(generator, "mooddisorders_pastbipolar1disorder", instance.getMooddisorders_pastbipolar1disorder());
        writeNonNullNumber(generator, "mooddisorders_pastbipolar2disorder", instance.getMooddisorders_pastbipolar2disorder());
        writeNonNullNumber(generator, "mooddisorders_pastdepressivedisordernos", instance.getMooddisorders_pastdepressivedisordernos());
        writeNonNullNumber(generator, "mooddisorders_pastmajordepressivedisorder", instance.getMooddisorders_pastmajordepressivedisorder());
        writeNonNullNumber(generator, "mooddisorders_pastotherbipolardisorder", instance.getMooddisorders_pastotherbipolardisorder());
        writeNonNullNumber(generator, "moodepisodes_currentdysthmicepisode", instance.getMoodepisodes_currentdysthmicepisode());
        writeNonNullNumber(generator, "moodepisodes_currenthypomanicepisode", instance.getMoodepisodes_currenthypomanicepisode());
        writeNonNullNumber(generator, "moodepisodes_currentmajordepressiveepisode", instance.getMoodepisodes_currentmajordepressiveepisode());
        writeNonNullNumber(generator, "moodepisodes_currentmanicepisode", instance.getMoodepisodes_currentmanicepisode());
        writeNonNullNumber(generator, "moodepisodes_currentmooddisorderduetomedicalcondition", instance.getMoodepisodes_currentmooddisorderduetomedicalcondition());
        writeNonNullNumber(generator, "moodepisodes_currentsubstanceinducedmooddisorder", instance.getMoodepisodes_currentsubstanceinducedmooddisorder());
        writeNonNullNumber(generator, "moodepisodes_pasthypomanicepisode", instance.getMoodepisodes_pasthypomanicepisode());
        writeNonNullNumber(generator, "moodepisodes_pastmajordepressiveepisode", instance.getMoodepisodes_pastmajordepressiveepisode());
        writeNonNullNumber(generator, "moodepisodes_pastmanicepisode", instance.getMoodepisodes_pastmanicepisode());
        writeNonNullNumber(generator, "moodepisodes_pastmooddisorderduetomedicalcondition", instance.getMoodepisodes_pastmooddisorderduetomedicalcondition());
        writeNonNullNumber(generator, "moodepisodes_pastsubstanceinducedmooddisorder", instance.getMoodepisodes_pastsubstanceinducedmooddisorder());
        writeNonNullNumber(generator, "optional_currentacutestressdisorder", instance.getOptional_currentacutestressdisorder());
        writeNonNullNumber(generator, "optional_currentminordepressivedisorder", instance.getOptional_currentminordepressivedisorder());
        writeNonNullNumber(generator, "optional_currentmixedanxietydepressivedisorder", instance.getOptional_currentmixedanxietydepressivedisorder());
        writeNonNullNumber(generator, "optional_pastacutestressdisorder", instance.getOptional_pastacutestressdisorder());
        writeNonNullNumber(generator, "optional_pastminordepressivedisorder", instance.getOptional_pastminordepressivedisorder());
        writeNonNullNumber(generator, "optional_pastmixedanxietydepressivedisorder", instance.getOptional_pastmixedanxietydepressivedisorder());
        writeNonBlankField(generator, "optional_pastsympomaticdetails", instance.getOptional_pastsympomaticdetails());
        writeNonNullNumber(generator, "psychoticdisorders_currentbriefpsychoticdisorder", instance.getPsychoticdisorders_currentbriefpsychoticdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_currentcatatonictype", instance.getPsychoticdisorders_currentcatatonictype());
        writeNonNullNumber(generator, "psychoticdisorders_currentdelusionaldisorder", instance.getPsychoticdisorders_currentdelusionaldisorder());
        writeNonNullNumber(generator, "psychoticdisorders_currentdisorganizedtype", instance.getPsychoticdisorders_currentdisorganizedtype());
        writeNonNullNumber(generator, "psychoticdisorders_currentparanoidtype", instance.getPsychoticdisorders_currentparanoidtype());
        writeNonNullNumber(generator, "psychoticdisorders_currentpsychoticdisorderduetomedicalcondition", instance.getPsychoticdisorders_currentpsychoticdisorderduetomedicalcondition());
        writeNonNullNumber(generator, "psychoticdisorders_currentpsychoticdisordernos", instance.getPsychoticdisorders_currentpsychoticdisordernos());
        writeNonNullNumber(generator, "psychoticdisorders_currentresidualtype", instance.getPsychoticdisorders_currentresidualtype());
        writeNonNullNumber(generator, "psychoticdisorders_currentschizoaffectivedisorder", instance.getPsychoticdisorders_currentschizoaffectivedisorder());
        writeNonNullNumber(generator, "psychoticdisorders_currentschizophrenia", instance.getPsychoticdisorders_currentschizophrenia());
        writeNonNullNumber(generator, "psychoticdisorders_currentschizophreniformdisorder", instance.getPsychoticdisorders_currentschizophreniformdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_currentsubstanceinducedpsychoticdisorder", instance.getPsychoticdisorders_currentsubstanceinducedpsychoticdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_currentundifferentiatedtype", instance.getPsychoticdisorders_currentundifferentiatedtype());
        writeNonNullNumber(generator, "psychoticdisorders_pastbriefpsychoticdisorder", instance.getPsychoticdisorders_pastbriefpsychoticdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_pastcatatonictype", instance.getPsychoticdisorders_pastcatatonictype());
        writeNonNullNumber(generator, "psychoticdisorders_pastdelusionaldisorder", instance.getPsychoticdisorders_pastdelusionaldisorder());
        writeNonNullNumber(generator, "psychoticdisorders_pastdisorganizedtype", instance.getPsychoticdisorders_pastdisorganizedtype());
        writeNonNullNumber(generator, "psychoticdisorders_pastparanoidtype", instance.getPsychoticdisorders_pastparanoidtype());
        writeNonNullNumber(generator, "psychoticdisorders_pastpsychoticdisorderduetomedicalcondition", instance.getPsychoticdisorders_pastpsychoticdisorderduetomedicalcondition());
        writeNonNullNumber(generator, "psychoticdisorders_pastpsychoticdisordernos", instance.getPsychoticdisorders_pastpsychoticdisordernos());
        writeNonNullNumber(generator, "psychoticdisorders_pastresidualtype", instance.getPsychoticdisorders_pastresidualtype());
        writeNonNullNumber(generator, "psychoticdisorders_pastschizoaffectivedisorder", instance.getPsychoticdisorders_pastschizoaffectivedisorder());
        writeNonNullNumber(generator, "psychoticdisorders_pastschizophrenia", instance.getPsychoticdisorders_pastschizophrenia());
        writeNonNullNumber(generator, "psychoticdisorders_pastschizophreniformdisorder", instance.getPsychoticdisorders_pastschizophreniformdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_pastsubstanceinducedpsychoticdisorder", instance.getPsychoticdisorders_pastsubstanceinducedpsychoticdisorder());
        writeNonNullNumber(generator, "psychoticdisorders_pastundifferentiatedtype", instance.getPsychoticdisorders_pastundifferentiatedtype());
        writeNonNullNumber(generator, "psychoticsymptoms_currentcatatonicbehavior", instance.getPsychoticsymptoms_currentcatatonicbehavior());
        writeNonNullNumber(generator, "psychoticsymptoms_currentdelusions", instance.getPsychoticsymptoms_currentdelusions());
        writeNonNullNumber(generator, "psychoticsymptoms_currentdisorganizedspeechbehavior", instance.getPsychoticsymptoms_currentdisorganizedspeechbehavior());
        writeNonNullNumber(generator, "psychoticsymptoms_currenthallucinations", instance.getPsychoticsymptoms_currenthallucinations());
        writeNonNullNumber(generator, "psychoticsymptoms_currentnegativesymptoms", instance.getPsychoticsymptoms_currentnegativesymptoms());
        writeNonNullNumber(generator, "psychoticsymptoms_pastcatatonicbehavior", instance.getPsychoticsymptoms_pastcatatonicbehavior());
        writeNonNullNumber(generator, "psychoticsymptoms_pastdelusions", instance.getPsychoticsymptoms_pastdelusions());
        writeNonNullNumber(generator, "psychoticsymptoms_pastdisorganizedspeechbehavior", instance.getPsychoticsymptoms_pastdisorganizedspeechbehavior());
        writeNonNullNumber(generator, "psychoticsymptoms_pasthallucinations", instance.getPsychoticsymptoms_pasthallucinations());
        writeNonNullNumber(generator, "psychoticsymptoms_pastnegativesymptoms", instance.getPsychoticsymptoms_pastnegativesymptoms());
        writeNonNullNumber(generator, "somatoformdisorders_bodydysmorphicdisorder", instance.getSomatoformdisorders_bodydysmorphicdisorder());
        writeNonNullNumber(generator, "somatoformdisorders_hypochondriasis", instance.getSomatoformdisorders_hypochondriasis());
        writeNonNullNumber(generator, "somatoformdisorders_paindisorder", instance.getSomatoformdisorders_paindisorder());
        writeNonNullNumber(generator, "somatoformdisorders_somatizationdisorder", instance.getSomatoformdisorders_somatizationdisorder());
        writeNonNullNumber(generator, "somatoformdisorders_undifferentiatedsomatformdisorder", instance.getSomatoformdisorders_undifferentiatedsomatformdisorder());
        writeNonNullNumber(generator, "substanceusedisorders_currentalcoholabuse", instance.getSubstanceusedisorders_currentalcoholabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentalcoholdependence", instance.getSubstanceusedisorders_currentalcoholdependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentamphetamineabuse", instance.getSubstanceusedisorders_currentamphetamineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentamphetaminedependence", instance.getSubstanceusedisorders_currentamphetaminedependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentcannabisabuse", instance.getSubstanceusedisorders_currentcannabisabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentcannabisdependence", instance.getSubstanceusedisorders_currentcannabisdependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentcocaineabuse", instance.getSubstanceusedisorders_currentcocaineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentcocainedependence", instance.getSubstanceusedisorders_currentcocainedependence());
        writeNonNullNumber(generator, "substanceusedisorders_currenthallucinogenabuse", instance.getSubstanceusedisorders_currenthallucinogenabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currenthallucinogendependence", instance.getSubstanceusedisorders_currenthallucinogendependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentopioidabuse", instance.getSubstanceusedisorders_currentopioidabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentopioiddependence", instance.getSubstanceusedisorders_currentopioiddependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentotherorunknownabuse", instance.getSubstanceusedisorders_currentotherorunknownabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentotherorunknowndependence", instance.getSubstanceusedisorders_currentotherorunknowndependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentphencyclidineabuse", instance.getSubstanceusedisorders_currentphencyclidineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentphencyclidinedependence", instance.getSubstanceusedisorders_currentphencyclidinedependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentpolysubstancedependence", instance.getSubstanceusedisorders_currentpolysubstancedependence());
        writeNonNullNumber(generator, "substanceusedisorders_currentsedativehypnoticanxiolyticabuse", instance.getSubstanceusedisorders_currentsedativehypnoticanxiolyticabuse());
        writeNonNullNumber(generator, "substanceusedisorders_currentsedativehypnoticanxiolyticdependence", instance.getSubstanceusedisorders_currentsedativehypnoticanxiolyticdependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastalcoholabuse", instance.getSubstanceusedisorders_pastalcoholabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastalcoholdependence", instance.getSubstanceusedisorders_pastalcoholdependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastamphetamineabuse", instance.getSubstanceusedisorders_pastamphetamineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastamphetaminedependence", instance.getSubstanceusedisorders_pastamphetaminedependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastcannabisabuse", instance.getSubstanceusedisorders_pastcannabisabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastcannabisdependence", instance.getSubstanceusedisorders_pastcannabisdependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastcocaineabuse", instance.getSubstanceusedisorders_pastcocaineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastcocainedependence", instance.getSubstanceusedisorders_pastcocainedependence());
        writeNonNullNumber(generator, "substanceusedisorders_pasthallucinogenabuse", instance.getSubstanceusedisorders_pasthallucinogenabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pasthallucinogendependence", instance.getSubstanceusedisorders_pasthallucinogendependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastopioidabuse", instance.getSubstanceusedisorders_pastopioidabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastopioiddependence", instance.getSubstanceusedisorders_pastopioiddependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastotherorunknownabuse", instance.getSubstanceusedisorders_pastotherorunknownabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastotherorunknowndependence", instance.getSubstanceusedisorders_pastotherorunknowndependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastphencyclidineabuse", instance.getSubstanceusedisorders_pastphencyclidineabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastphencyclidinedependence", instance.getSubstanceusedisorders_pastphencyclidinedependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastpolysubstancedependence", instance.getSubstanceusedisorders_pastpolysubstancedependence());
        writeNonNullNumber(generator, "substanceusedisorders_pastsedativehypnoticanxiolyticabuse", instance.getSubstanceusedisorders_pastsedativehypnoticanxiolyticabuse());
        writeNonNullNumber(generator, "substanceusedisorders_pastsedativehypnoticanxiolyticdependence", instance.getSubstanceusedisorders_pastsedativehypnoticanxiolyticdependence());
        super.serializeImpl(instance, generator, provider);
    }
}

