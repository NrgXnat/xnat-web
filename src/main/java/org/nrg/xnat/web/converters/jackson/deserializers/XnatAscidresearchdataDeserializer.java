package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAscidresearchdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAscidresearchdataDeserializer<T extends XnatAscidresearchdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -5310262280354920787L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAscidresearchdataDeserializer() {
        this((Class<T>) XnatAscidresearchdata.class);
    }

    protected XnatAscidresearchdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "adjustmentdisorder":
                instance.setAdjustmentdisorder(parser.getIntValue());
                break;
            case "administrator":
                instance.setAdministrator(parser.getText());
                break;
            case "anxietydisorders_currentagoraphobiawithoutpanichx":
                instance.setAnxietydisorders_currentagoraphobiawithoutpanichx(parser.getIntValue());
                break;
            case "anxietydisorders_currentanxietydisordernos":
                instance.setAnxietydisorders_currentanxietydisordernos(parser.getIntValue());
                break;
            case "anxietydisorders_currentanxietyduetomedicalcondition":
                instance.setAnxietydisorders_currentanxietyduetomedicalcondition(parser.getIntValue());
                break;
            case "anxietydisorders_currentgeneralizedanxietydisorder":
                instance.setAnxietydisorders_currentgeneralizedanxietydisorder(parser.getIntValue());
                break;
            case "anxietydisorders_currentocd":
                instance.setAnxietydisorders_currentocd(parser.getIntValue());
                break;
            case "anxietydisorders_currentpanicwithagoraphobia":
                instance.setAnxietydisorders_currentpanicwithagoraphobia(parser.getIntValue());
                break;
            case "anxietydisorders_currentpanicwithoutagoraphobia":
                instance.setAnxietydisorders_currentpanicwithoutagoraphobia(parser.getIntValue());
                break;
            case "anxietydisorders_currentptsd":
                instance.setAnxietydisorders_currentptsd(parser.getIntValue());
                break;
            case "anxietydisorders_currentsocialphobia":
                instance.setAnxietydisorders_currentsocialphobia(parser.getIntValue());
                break;
            case "anxietydisorders_currentspecificphobia":
                instance.setAnxietydisorders_currentspecificphobia(parser.getIntValue());
                break;
            case "anxietydisorders_currentsubstanceinducedanxietydisorder":
                instance.setAnxietydisorders_currentsubstanceinducedanxietydisorder(parser.getIntValue());
                break;
            case "anxietydisorders_pastagoraphobiawithoutpanichx":
                instance.setAnxietydisorders_pastagoraphobiawithoutpanichx(parser.getIntValue());
                break;
            case "anxietydisorders_pastanxietydisordernos":
                instance.setAnxietydisorders_pastanxietydisordernos(parser.getIntValue());
                break;
            case "anxietydisorders_pastanxietyduetomedicalcondition":
                instance.setAnxietydisorders_pastanxietyduetomedicalcondition(parser.getIntValue());
                break;
            case "anxietydisorders_pastocd":
                instance.setAnxietydisorders_pastocd(parser.getIntValue());
                break;
            case "anxietydisorders_pastpanicwithagoraphobia":
                instance.setAnxietydisorders_pastpanicwithagoraphobia(parser.getIntValue());
                break;
            case "anxietydisorders_pastpanicwithoutagoraphobia":
                instance.setAnxietydisorders_pastpanicwithoutagoraphobia(parser.getIntValue());
                break;
            case "anxietydisorders_pastptsd":
                instance.setAnxietydisorders_pastptsd(parser.getIntValue());
                break;
            case "anxietydisorders_pastsocialphobia":
                instance.setAnxietydisorders_pastsocialphobia(parser.getIntValue());
                break;
            case "anxietydisorders_pastspecificphobia":
                instance.setAnxietydisorders_pastspecificphobia(parser.getIntValue());
                break;
            case "anxietydisorders_pastsubstanceinducedanxietydisorder":
                instance.setAnxietydisorders_pastsubstanceinducedanxietydisorder(parser.getIntValue());
                break;
            case "eatingdisorders_currentanorexianervosa":
                instance.setEatingdisorders_currentanorexianervosa(parser.getIntValue());
                break;
            case "eatingdisorders_currentbingeeatingdisorder":
                instance.setEatingdisorders_currentbingeeatingdisorder(parser.getIntValue());
                break;
            case "eatingdisorders_currentbulimianervosa":
                instance.setEatingdisorders_currentbulimianervosa(parser.getIntValue());
                break;
            case "eatingdisorders_pastanorexianervosa":
                instance.setEatingdisorders_pastanorexianervosa(parser.getIntValue());
                break;
            case "eatingdisorders_pastbingeeatingdisorder":
                instance.setEatingdisorders_pastbingeeatingdisorder(parser.getIntValue());
                break;
            case "eatingdisorders_pastbulimianervosa":
                instance.setEatingdisorders_pastbulimianervosa(parser.getIntValue());
                break;
            case "mooddisorders_currentbipolar1disorder":
                instance.setMooddisorders_currentbipolar1disorder(parser.getIntValue());
                break;
            case "mooddisorders_currentbipolar2disorder":
                instance.setMooddisorders_currentbipolar2disorder(parser.getIntValue());
                break;
            case "mooddisorders_currentdepressivedisordernos":
                instance.setMooddisorders_currentdepressivedisordernos(parser.getIntValue());
                break;
            case "mooddisorders_currentmajordepressivedisorder":
                instance.setMooddisorders_currentmajordepressivedisorder(parser.getIntValue());
                break;
            case "mooddisorders_currentotherbipolardisorder":
                instance.setMooddisorders_currentotherbipolardisorder(parser.getIntValue());
                break;
            case "mooddisorders_pastbipolar1disorder":
                instance.setMooddisorders_pastbipolar1disorder(parser.getIntValue());
                break;
            case "mooddisorders_pastbipolar2disorder":
                instance.setMooddisorders_pastbipolar2disorder(parser.getIntValue());
                break;
            case "mooddisorders_pastdepressivedisordernos":
                instance.setMooddisorders_pastdepressivedisordernos(parser.getIntValue());
                break;
            case "mooddisorders_pastmajordepressivedisorder":
                instance.setMooddisorders_pastmajordepressivedisorder(parser.getIntValue());
                break;
            case "mooddisorders_pastotherbipolardisorder":
                instance.setMooddisorders_pastotherbipolardisorder(parser.getIntValue());
                break;
            case "moodepisodes_currentdysthmicepisode":
                instance.setMoodepisodes_currentdysthmicepisode(parser.getIntValue());
                break;
            case "moodepisodes_currenthypomanicepisode":
                instance.setMoodepisodes_currenthypomanicepisode(parser.getIntValue());
                break;
            case "moodepisodes_currentmajordepressiveepisode":
                instance.setMoodepisodes_currentmajordepressiveepisode(parser.getIntValue());
                break;
            case "moodepisodes_currentmanicepisode":
                instance.setMoodepisodes_currentmanicepisode(parser.getIntValue());
                break;
            case "moodepisodes_currentmooddisorderduetomedicalcondition":
                instance.setMoodepisodes_currentmooddisorderduetomedicalcondition(parser.getIntValue());
                break;
            case "moodepisodes_currentsubstanceinducedmooddisorder":
                instance.setMoodepisodes_currentsubstanceinducedmooddisorder(parser.getIntValue());
                break;
            case "moodepisodes_pasthypomanicepisode":
                instance.setMoodepisodes_pasthypomanicepisode(parser.getIntValue());
                break;
            case "moodepisodes_pastmajordepressiveepisode":
                instance.setMoodepisodes_pastmajordepressiveepisode(parser.getIntValue());
                break;
            case "moodepisodes_pastmanicepisode":
                instance.setMoodepisodes_pastmanicepisode(parser.getIntValue());
                break;
            case "moodepisodes_pastmooddisorderduetomedicalcondition":
                instance.setMoodepisodes_pastmooddisorderduetomedicalcondition(parser.getIntValue());
                break;
            case "moodepisodes_pastsubstanceinducedmooddisorder":
                instance.setMoodepisodes_pastsubstanceinducedmooddisorder(parser.getIntValue());
                break;
            case "optional_currentacutestressdisorder":
                instance.setOptional_currentacutestressdisorder(parser.getIntValue());
                break;
            case "optional_currentminordepressivedisorder":
                instance.setOptional_currentminordepressivedisorder(parser.getIntValue());
                break;
            case "optional_currentmixedanxietydepressivedisorder":
                instance.setOptional_currentmixedanxietydepressivedisorder(parser.getIntValue());
                break;
            case "optional_pastacutestressdisorder":
                instance.setOptional_pastacutestressdisorder(parser.getIntValue());
                break;
            case "optional_pastminordepressivedisorder":
                instance.setOptional_pastminordepressivedisorder(parser.getIntValue());
                break;
            case "optional_pastmixedanxietydepressivedisorder":
                instance.setOptional_pastmixedanxietydepressivedisorder(parser.getIntValue());
                break;
            case "optional_pastsympomaticdetails":
                instance.setOptional_pastsympomaticdetails(parser.getText());
                break;
            case "psychoticdisorders_currentbriefpsychoticdisorder":
                instance.setPsychoticdisorders_currentbriefpsychoticdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_currentcatatonictype":
                instance.setPsychoticdisorders_currentcatatonictype(parser.getIntValue());
                break;
            case "psychoticdisorders_currentdelusionaldisorder":
                instance.setPsychoticdisorders_currentdelusionaldisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_currentdisorganizedtype":
                instance.setPsychoticdisorders_currentdisorganizedtype(parser.getIntValue());
                break;
            case "psychoticdisorders_currentparanoidtype":
                instance.setPsychoticdisorders_currentparanoidtype(parser.getIntValue());
                break;
            case "psychoticdisorders_currentpsychoticdisorderduetomedicalcondition":
                instance.setPsychoticdisorders_currentpsychoticdisorderduetomedicalcondition(parser.getIntValue());
                break;
            case "psychoticdisorders_currentpsychoticdisordernos":
                instance.setPsychoticdisorders_currentpsychoticdisordernos(parser.getIntValue());
                break;
            case "psychoticdisorders_currentresidualtype":
                instance.setPsychoticdisorders_currentresidualtype(parser.getIntValue());
                break;
            case "psychoticdisorders_currentschizoaffectivedisorder":
                instance.setPsychoticdisorders_currentschizoaffectivedisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_currentschizophrenia":
                instance.setPsychoticdisorders_currentschizophrenia(parser.getIntValue());
                break;
            case "psychoticdisorders_currentschizophreniformdisorder":
                instance.setPsychoticdisorders_currentschizophreniformdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_currentsubstanceinducedpsychoticdisorder":
                instance.setPsychoticdisorders_currentsubstanceinducedpsychoticdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_currentundifferentiatedtype":
                instance.setPsychoticdisorders_currentundifferentiatedtype(parser.getIntValue());
                break;
            case "psychoticdisorders_pastbriefpsychoticdisorder":
                instance.setPsychoticdisorders_pastbriefpsychoticdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_pastcatatonictype":
                instance.setPsychoticdisorders_pastcatatonictype(parser.getIntValue());
                break;
            case "psychoticdisorders_pastdelusionaldisorder":
                instance.setPsychoticdisorders_pastdelusionaldisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_pastdisorganizedtype":
                instance.setPsychoticdisorders_pastdisorganizedtype(parser.getIntValue());
                break;
            case "psychoticdisorders_pastparanoidtype":
                instance.setPsychoticdisorders_pastparanoidtype(parser.getIntValue());
                break;
            case "psychoticdisorders_pastpsychoticdisorderduetomedicalcondition":
                instance.setPsychoticdisorders_pastpsychoticdisorderduetomedicalcondition(parser.getIntValue());
                break;
            case "psychoticdisorders_pastpsychoticdisordernos":
                instance.setPsychoticdisorders_pastpsychoticdisordernos(parser.getIntValue());
                break;
            case "psychoticdisorders_pastresidualtype":
                instance.setPsychoticdisorders_pastresidualtype(parser.getIntValue());
                break;
            case "psychoticdisorders_pastschizoaffectivedisorder":
                instance.setPsychoticdisorders_pastschizoaffectivedisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_pastschizophrenia":
                instance.setPsychoticdisorders_pastschizophrenia(parser.getIntValue());
                break;
            case "psychoticdisorders_pastschizophreniformdisorder":
                instance.setPsychoticdisorders_pastschizophreniformdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_pastsubstanceinducedpsychoticdisorder":
                instance.setPsychoticdisorders_pastsubstanceinducedpsychoticdisorder(parser.getIntValue());
                break;
            case "psychoticdisorders_pastundifferentiatedtype":
                instance.setPsychoticdisorders_pastundifferentiatedtype(parser.getIntValue());
                break;
            case "psychoticsymptoms_currentcatatonicbehavior":
                instance.setPsychoticsymptoms_currentcatatonicbehavior(parser.getIntValue());
                break;
            case "psychoticsymptoms_currentdelusions":
                instance.setPsychoticsymptoms_currentdelusions(parser.getIntValue());
                break;
            case "psychoticsymptoms_currentdisorganizedspeechbehavior":
                instance.setPsychoticsymptoms_currentdisorganizedspeechbehavior(parser.getIntValue());
                break;
            case "psychoticsymptoms_currenthallucinations":
                instance.setPsychoticsymptoms_currenthallucinations(parser.getIntValue());
                break;
            case "psychoticsymptoms_currentnegativesymptoms":
                instance.setPsychoticsymptoms_currentnegativesymptoms(parser.getIntValue());
                break;
            case "psychoticsymptoms_pastcatatonicbehavior":
                instance.setPsychoticsymptoms_pastcatatonicbehavior(parser.getIntValue());
                break;
            case "psychoticsymptoms_pastdelusions":
                instance.setPsychoticsymptoms_pastdelusions(parser.getIntValue());
                break;
            case "psychoticsymptoms_pastdisorganizedspeechbehavior":
                instance.setPsychoticsymptoms_pastdisorganizedspeechbehavior(parser.getIntValue());
                break;
            case "psychoticsymptoms_pasthallucinations":
                instance.setPsychoticsymptoms_pasthallucinations(parser.getIntValue());
                break;
            case "psychoticsymptoms_pastnegativesymptoms":
                instance.setPsychoticsymptoms_pastnegativesymptoms(parser.getIntValue());
                break;
            case "somatoformdisorders_bodydysmorphicdisorder":
                instance.setSomatoformdisorders_bodydysmorphicdisorder(parser.getIntValue());
                break;
            case "somatoformdisorders_hypochondriasis":
                instance.setSomatoformdisorders_hypochondriasis(parser.getIntValue());
                break;
            case "somatoformdisorders_paindisorder":
                instance.setSomatoformdisorders_paindisorder(parser.getIntValue());
                break;
            case "somatoformdisorders_somatizationdisorder":
                instance.setSomatoformdisorders_somatizationdisorder(parser.getIntValue());
                break;
            case "somatoformdisorders_undifferentiatedsomatformdisorder":
                instance.setSomatoformdisorders_undifferentiatedsomatformdisorder(parser.getIntValue());
                break;
            case "substanceusedisorders_currentalcoholabuse":
                instance.setSubstanceusedisorders_currentalcoholabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentalcoholdependence":
                instance.setSubstanceusedisorders_currentalcoholdependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentamphetamineabuse":
                instance.setSubstanceusedisorders_currentamphetamineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentamphetaminedependence":
                instance.setSubstanceusedisorders_currentamphetaminedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentcannabisabuse":
                instance.setSubstanceusedisorders_currentcannabisabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentcannabisdependence":
                instance.setSubstanceusedisorders_currentcannabisdependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentcocaineabuse":
                instance.setSubstanceusedisorders_currentcocaineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentcocainedependence":
                instance.setSubstanceusedisorders_currentcocainedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currenthallucinogenabuse":
                instance.setSubstanceusedisorders_currenthallucinogenabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currenthallucinogendependence":
                instance.setSubstanceusedisorders_currenthallucinogendependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentopioidabuse":
                instance.setSubstanceusedisorders_currentopioidabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentopioiddependence":
                instance.setSubstanceusedisorders_currentopioiddependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentotherorunknownabuse":
                instance.setSubstanceusedisorders_currentotherorunknownabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentotherorunknowndependence":
                instance.setSubstanceusedisorders_currentotherorunknowndependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentphencyclidineabuse":
                instance.setSubstanceusedisorders_currentphencyclidineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentphencyclidinedependence":
                instance.setSubstanceusedisorders_currentphencyclidinedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentpolysubstancedependence":
                instance.setSubstanceusedisorders_currentpolysubstancedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_currentsedativehypnoticanxiolyticabuse":
                instance.setSubstanceusedisorders_currentsedativehypnoticanxiolyticabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_currentsedativehypnoticanxiolyticdependence":
                instance.setSubstanceusedisorders_currentsedativehypnoticanxiolyticdependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastalcoholabuse":
                instance.setSubstanceusedisorders_pastalcoholabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastalcoholdependence":
                instance.setSubstanceusedisorders_pastalcoholdependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastamphetamineabuse":
                instance.setSubstanceusedisorders_pastamphetamineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastamphetaminedependence":
                instance.setSubstanceusedisorders_pastamphetaminedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastcannabisabuse":
                instance.setSubstanceusedisorders_pastcannabisabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastcannabisdependence":
                instance.setSubstanceusedisorders_pastcannabisdependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastcocaineabuse":
                instance.setSubstanceusedisorders_pastcocaineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastcocainedependence":
                instance.setSubstanceusedisorders_pastcocainedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pasthallucinogenabuse":
                instance.setSubstanceusedisorders_pasthallucinogenabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pasthallucinogendependence":
                instance.setSubstanceusedisorders_pasthallucinogendependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastopioidabuse":
                instance.setSubstanceusedisorders_pastopioidabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastopioiddependence":
                instance.setSubstanceusedisorders_pastopioiddependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastotherorunknownabuse":
                instance.setSubstanceusedisorders_pastotherorunknownabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastotherorunknowndependence":
                instance.setSubstanceusedisorders_pastotherorunknowndependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastphencyclidineabuse":
                instance.setSubstanceusedisorders_pastphencyclidineabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastphencyclidinedependence":
                instance.setSubstanceusedisorders_pastphencyclidinedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastpolysubstancedependence":
                instance.setSubstanceusedisorders_pastpolysubstancedependence(parser.getIntValue());
                break;
            case "substanceusedisorders_pastsedativehypnoticanxiolyticabuse":
                instance.setSubstanceusedisorders_pastsedativehypnoticanxiolyticabuse(parser.getIntValue());
                break;
            case "substanceusedisorders_pastsedativehypnoticanxiolyticdependence":
                instance.setSubstanceusedisorders_pastsedativehypnoticanxiolyticdependence(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

