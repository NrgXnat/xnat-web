package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAscidresearchdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAscidresearchdataDeserializer<T extends XnatAscidresearchdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6392307651688645972L;

    @SuppressWarnings("unchecked")
    public XnatAscidresearchdataDeserializer() {
        this((Class<T>) XnatAscidresearchdata.class);
    }

    public XnatAscidresearchdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "adjustmentdisorder":
                // TODO: Handle the "adjustmentdisorder" property here: Integer
                break;
            case "administrator":
                // TODO: Handle the "administrator" property here: String
                break;
            case "anxietydisorders_currentagoraphobiawithoutpanichx":
                // TODO: Handle the "anxietydisorders_currentagoraphobiawithoutpanichx" property here: Integer
                break;
            case "anxietydisorders_currentanxietydisordernos":
                // TODO: Handle the "anxietydisorders_currentanxietydisordernos" property here: Integer
                break;
            case "anxietydisorders_currentanxietyduetomedicalcondition":
                // TODO: Handle the "anxietydisorders_currentanxietyduetomedicalcondition" property here: Integer
                break;
            case "anxietydisorders_currentgeneralizedanxietydisorder":
                // TODO: Handle the "anxietydisorders_currentgeneralizedanxietydisorder" property here: Integer
                break;
            case "anxietydisorders_currentocd":
                // TODO: Handle the "anxietydisorders_currentocd" property here: Integer
                break;
            case "anxietydisorders_currentpanicwithagoraphobia":
                // TODO: Handle the "anxietydisorders_currentpanicwithagoraphobia" property here: Integer
                break;
            case "anxietydisorders_currentpanicwithoutagoraphobia":
                // TODO: Handle the "anxietydisorders_currentpanicwithoutagoraphobia" property here: Integer
                break;
            case "anxietydisorders_currentptsd":
                // TODO: Handle the "anxietydisorders_currentptsd" property here: Integer
                break;
            case "anxietydisorders_currentsocialphobia":
                // TODO: Handle the "anxietydisorders_currentsocialphobia" property here: Integer
                break;
            case "anxietydisorders_currentspecificphobia":
                // TODO: Handle the "anxietydisorders_currentspecificphobia" property here: Integer
                break;
            case "anxietydisorders_currentsubstanceinducedanxietydisorder":
                // TODO: Handle the "anxietydisorders_currentsubstanceinducedanxietydisorder" property here: Integer
                break;
            case "anxietydisorders_pastagoraphobiawithoutpanichx":
                // TODO: Handle the "anxietydisorders_pastagoraphobiawithoutpanichx" property here: Integer
                break;
            case "anxietydisorders_pastanxietydisordernos":
                // TODO: Handle the "anxietydisorders_pastanxietydisordernos" property here: Integer
                break;
            case "anxietydisorders_pastanxietyduetomedicalcondition":
                // TODO: Handle the "anxietydisorders_pastanxietyduetomedicalcondition" property here: Integer
                break;
            case "anxietydisorders_pastocd":
                // TODO: Handle the "anxietydisorders_pastocd" property here: Integer
                break;
            case "anxietydisorders_pastpanicwithagoraphobia":
                // TODO: Handle the "anxietydisorders_pastpanicwithagoraphobia" property here: Integer
                break;
            case "anxietydisorders_pastpanicwithoutagoraphobia":
                // TODO: Handle the "anxietydisorders_pastpanicwithoutagoraphobia" property here: Integer
                break;
            case "anxietydisorders_pastptsd":
                // TODO: Handle the "anxietydisorders_pastptsd" property here: Integer
                break;
            case "anxietydisorders_pastsocialphobia":
                // TODO: Handle the "anxietydisorders_pastsocialphobia" property here: Integer
                break;
            case "anxietydisorders_pastspecificphobia":
                // TODO: Handle the "anxietydisorders_pastspecificphobia" property here: Integer
                break;
            case "anxietydisorders_pastsubstanceinducedanxietydisorder":
                // TODO: Handle the "anxietydisorders_pastsubstanceinducedanxietydisorder" property here: Integer
                break;
            case "eatingdisorders_currentanorexianervosa":
                // TODO: Handle the "eatingdisorders_currentanorexianervosa" property here: Integer
                break;
            case "eatingdisorders_currentbingeeatingdisorder":
                // TODO: Handle the "eatingdisorders_currentbingeeatingdisorder" property here: Integer
                break;
            case "eatingdisorders_currentbulimianervosa":
                // TODO: Handle the "eatingdisorders_currentbulimianervosa" property here: Integer
                break;
            case "eatingdisorders_pastanorexianervosa":
                // TODO: Handle the "eatingdisorders_pastanorexianervosa" property here: Integer
                break;
            case "eatingdisorders_pastbingeeatingdisorder":
                // TODO: Handle the "eatingdisorders_pastbingeeatingdisorder" property here: Integer
                break;
            case "eatingdisorders_pastbulimianervosa":
                // TODO: Handle the "eatingdisorders_pastbulimianervosa" property here: Integer
                break;
            case "mooddisorders_currentbipolar1disorder":
                // TODO: Handle the "mooddisorders_currentbipolar1disorder" property here: Integer
                break;
            case "mooddisorders_currentbipolar2disorder":
                // TODO: Handle the "mooddisorders_currentbipolar2disorder" property here: Integer
                break;
            case "mooddisorders_currentdepressivedisordernos":
                // TODO: Handle the "mooddisorders_currentdepressivedisordernos" property here: Integer
                break;
            case "mooddisorders_currentmajordepressivedisorder":
                // TODO: Handle the "mooddisorders_currentmajordepressivedisorder" property here: Integer
                break;
            case "mooddisorders_currentotherbipolardisorder":
                // TODO: Handle the "mooddisorders_currentotherbipolardisorder" property here: Integer
                break;
            case "mooddisorders_pastbipolar1disorder":
                // TODO: Handle the "mooddisorders_pastbipolar1disorder" property here: Integer
                break;
            case "mooddisorders_pastbipolar2disorder":
                // TODO: Handle the "mooddisorders_pastbipolar2disorder" property here: Integer
                break;
            case "mooddisorders_pastdepressivedisordernos":
                // TODO: Handle the "mooddisorders_pastdepressivedisordernos" property here: Integer
                break;
            case "mooddisorders_pastmajordepressivedisorder":
                // TODO: Handle the "mooddisorders_pastmajordepressivedisorder" property here: Integer
                break;
            case "mooddisorders_pastotherbipolardisorder":
                // TODO: Handle the "mooddisorders_pastotherbipolardisorder" property here: Integer
                break;
            case "moodepisodes_currentdysthmicepisode":
                // TODO: Handle the "moodepisodes_currentdysthmicepisode" property here: Integer
                break;
            case "moodepisodes_currenthypomanicepisode":
                // TODO: Handle the "moodepisodes_currenthypomanicepisode" property here: Integer
                break;
            case "moodepisodes_currentmajordepressiveepisode":
                // TODO: Handle the "moodepisodes_currentmajordepressiveepisode" property here: Integer
                break;
            case "moodepisodes_currentmanicepisode":
                // TODO: Handle the "moodepisodes_currentmanicepisode" property here: Integer
                break;
            case "moodepisodes_currentmooddisorderduetomedicalcondition":
                // TODO: Handle the "moodepisodes_currentmooddisorderduetomedicalcondition" property here: Integer
                break;
            case "moodepisodes_currentsubstanceinducedmooddisorder":
                // TODO: Handle the "moodepisodes_currentsubstanceinducedmooddisorder" property here: Integer
                break;
            case "moodepisodes_pasthypomanicepisode":
                // TODO: Handle the "moodepisodes_pasthypomanicepisode" property here: Integer
                break;
            case "moodepisodes_pastmajordepressiveepisode":
                // TODO: Handle the "moodepisodes_pastmajordepressiveepisode" property here: Integer
                break;
            case "moodepisodes_pastmanicepisode":
                // TODO: Handle the "moodepisodes_pastmanicepisode" property here: Integer
                break;
            case "moodepisodes_pastmooddisorderduetomedicalcondition":
                // TODO: Handle the "moodepisodes_pastmooddisorderduetomedicalcondition" property here: Integer
                break;
            case "moodepisodes_pastsubstanceinducedmooddisorder":
                // TODO: Handle the "moodepisodes_pastsubstanceinducedmooddisorder" property here: Integer
                break;
            case "optional_currentacutestressdisorder":
                // TODO: Handle the "optional_currentacutestressdisorder" property here: Integer
                break;
            case "optional_currentminordepressivedisorder":
                // TODO: Handle the "optional_currentminordepressivedisorder" property here: Integer
                break;
            case "optional_currentmixedanxietydepressivedisorder":
                // TODO: Handle the "optional_currentmixedanxietydepressivedisorder" property here: Integer
                break;
            case "optional_pastacutestressdisorder":
                // TODO: Handle the "optional_pastacutestressdisorder" property here: Integer
                break;
            case "optional_pastminordepressivedisorder":
                // TODO: Handle the "optional_pastminordepressivedisorder" property here: Integer
                break;
            case "optional_pastmixedanxietydepressivedisorder":
                // TODO: Handle the "optional_pastmixedanxietydepressivedisorder" property here: Integer
                break;
            case "optional_pastsympomaticdetails":
                // TODO: Handle the "optional_pastsympomaticdetails" property here: String
                break;
            case "psychoticdisorders_currentbriefpsychoticdisorder":
                // TODO: Handle the "psychoticdisorders_currentbriefpsychoticdisorder" property here: Integer
                break;
            case "psychoticdisorders_currentcatatonictype":
                // TODO: Handle the "psychoticdisorders_currentcatatonictype" property here: Integer
                break;
            case "psychoticdisorders_currentdelusionaldisorder":
                // TODO: Handle the "psychoticdisorders_currentdelusionaldisorder" property here: Integer
                break;
            case "psychoticdisorders_currentdisorganizedtype":
                // TODO: Handle the "psychoticdisorders_currentdisorganizedtype" property here: Integer
                break;
            case "psychoticdisorders_currentparanoidtype":
                // TODO: Handle the "psychoticdisorders_currentparanoidtype" property here: Integer
                break;
            case "psychoticdisorders_currentpsychoticdisorderduetomedicalcondition":
                // TODO: Handle the "psychoticdisorders_currentpsychoticdisorderduetomedicalcondition" property here: Integer
                break;
            case "psychoticdisorders_currentpsychoticdisordernos":
                // TODO: Handle the "psychoticdisorders_currentpsychoticdisordernos" property here: Integer
                break;
            case "psychoticdisorders_currentresidualtype":
                // TODO: Handle the "psychoticdisorders_currentresidualtype" property here: Integer
                break;
            case "psychoticdisorders_currentschizoaffectivedisorder":
                // TODO: Handle the "psychoticdisorders_currentschizoaffectivedisorder" property here: Integer
                break;
            case "psychoticdisorders_currentschizophrenia":
                // TODO: Handle the "psychoticdisorders_currentschizophrenia" property here: Integer
                break;
            case "psychoticdisorders_currentschizophreniformdisorder":
                // TODO: Handle the "psychoticdisorders_currentschizophreniformdisorder" property here: Integer
                break;
            case "psychoticdisorders_currentsubstanceinducedpsychoticdisorder":
                // TODO: Handle the "psychoticdisorders_currentsubstanceinducedpsychoticdisorder" property here: Integer
                break;
            case "psychoticdisorders_currentundifferentiatedtype":
                // TODO: Handle the "psychoticdisorders_currentundifferentiatedtype" property here: Integer
                break;
            case "psychoticdisorders_pastbriefpsychoticdisorder":
                // TODO: Handle the "psychoticdisorders_pastbriefpsychoticdisorder" property here: Integer
                break;
            case "psychoticdisorders_pastcatatonictype":
                // TODO: Handle the "psychoticdisorders_pastcatatonictype" property here: Integer
                break;
            case "psychoticdisorders_pastdelusionaldisorder":
                // TODO: Handle the "psychoticdisorders_pastdelusionaldisorder" property here: Integer
                break;
            case "psychoticdisorders_pastdisorganizedtype":
                // TODO: Handle the "psychoticdisorders_pastdisorganizedtype" property here: Integer
                break;
            case "psychoticdisorders_pastparanoidtype":
                // TODO: Handle the "psychoticdisorders_pastparanoidtype" property here: Integer
                break;
            case "psychoticdisorders_pastpsychoticdisorderduetomedicalcondition":
                // TODO: Handle the "psychoticdisorders_pastpsychoticdisorderduetomedicalcondition" property here: Integer
                break;
            case "psychoticdisorders_pastpsychoticdisordernos":
                // TODO: Handle the "psychoticdisorders_pastpsychoticdisordernos" property here: Integer
                break;
            case "psychoticdisorders_pastresidualtype":
                // TODO: Handle the "psychoticdisorders_pastresidualtype" property here: Integer
                break;
            case "psychoticdisorders_pastschizoaffectivedisorder":
                // TODO: Handle the "psychoticdisorders_pastschizoaffectivedisorder" property here: Integer
                break;
            case "psychoticdisorders_pastschizophrenia":
                // TODO: Handle the "psychoticdisorders_pastschizophrenia" property here: Integer
                break;
            case "psychoticdisorders_pastschizophreniformdisorder":
                // TODO: Handle the "psychoticdisorders_pastschizophreniformdisorder" property here: Integer
                break;
            case "psychoticdisorders_pastsubstanceinducedpsychoticdisorder":
                // TODO: Handle the "psychoticdisorders_pastsubstanceinducedpsychoticdisorder" property here: Integer
                break;
            case "psychoticdisorders_pastundifferentiatedtype":
                // TODO: Handle the "psychoticdisorders_pastundifferentiatedtype" property here: Integer
                break;
            case "psychoticsymptoms_currentcatatonicbehavior":
                // TODO: Handle the "psychoticsymptoms_currentcatatonicbehavior" property here: Integer
                break;
            case "psychoticsymptoms_currentdelusions":
                // TODO: Handle the "psychoticsymptoms_currentdelusions" property here: Integer
                break;
            case "psychoticsymptoms_currentdisorganizedspeechbehavior":
                // TODO: Handle the "psychoticsymptoms_currentdisorganizedspeechbehavior" property here: Integer
                break;
            case "psychoticsymptoms_currenthallucinations":
                // TODO: Handle the "psychoticsymptoms_currenthallucinations" property here: Integer
                break;
            case "psychoticsymptoms_currentnegativesymptoms":
                // TODO: Handle the "psychoticsymptoms_currentnegativesymptoms" property here: Integer
                break;
            case "psychoticsymptoms_pastcatatonicbehavior":
                // TODO: Handle the "psychoticsymptoms_pastcatatonicbehavior" property here: Integer
                break;
            case "psychoticsymptoms_pastdelusions":
                // TODO: Handle the "psychoticsymptoms_pastdelusions" property here: Integer
                break;
            case "psychoticsymptoms_pastdisorganizedspeechbehavior":
                // TODO: Handle the "psychoticsymptoms_pastdisorganizedspeechbehavior" property here: Integer
                break;
            case "psychoticsymptoms_pasthallucinations":
                // TODO: Handle the "psychoticsymptoms_pasthallucinations" property here: Integer
                break;
            case "psychoticsymptoms_pastnegativesymptoms":
                // TODO: Handle the "psychoticsymptoms_pastnegativesymptoms" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "somatoformdisorders_bodydysmorphicdisorder":
                // TODO: Handle the "somatoformdisorders_bodydysmorphicdisorder" property here: Integer
                break;
            case "somatoformdisorders_hypochondriasis":
                // TODO: Handle the "somatoformdisorders_hypochondriasis" property here: Integer
                break;
            case "somatoformdisorders_paindisorder":
                // TODO: Handle the "somatoformdisorders_paindisorder" property here: Integer
                break;
            case "somatoformdisorders_somatizationdisorder":
                // TODO: Handle the "somatoformdisorders_somatizationdisorder" property here: Integer
                break;
            case "somatoformdisorders_undifferentiatedsomatformdisorder":
                // TODO: Handle the "somatoformdisorders_undifferentiatedsomatformdisorder" property here: Integer
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "substanceusedisorders_currentalcoholabuse":
                // TODO: Handle the "substanceusedisorders_currentalcoholabuse" property here: Integer
                break;
            case "substanceusedisorders_currentalcoholdependence":
                // TODO: Handle the "substanceusedisorders_currentalcoholdependence" property here: Integer
                break;
            case "substanceusedisorders_currentamphetamineabuse":
                // TODO: Handle the "substanceusedisorders_currentamphetamineabuse" property here: Integer
                break;
            case "substanceusedisorders_currentamphetaminedependence":
                // TODO: Handle the "substanceusedisorders_currentamphetaminedependence" property here: Integer
                break;
            case "substanceusedisorders_currentcannabisabuse":
                // TODO: Handle the "substanceusedisorders_currentcannabisabuse" property here: Integer
                break;
            case "substanceusedisorders_currentcannabisdependence":
                // TODO: Handle the "substanceusedisorders_currentcannabisdependence" property here: Integer
                break;
            case "substanceusedisorders_currentcocaineabuse":
                // TODO: Handle the "substanceusedisorders_currentcocaineabuse" property here: Integer
                break;
            case "substanceusedisorders_currentcocainedependence":
                // TODO: Handle the "substanceusedisorders_currentcocainedependence" property here: Integer
                break;
            case "substanceusedisorders_currenthallucinogenabuse":
                // TODO: Handle the "substanceusedisorders_currenthallucinogenabuse" property here: Integer
                break;
            case "substanceusedisorders_currenthallucinogendependence":
                // TODO: Handle the "substanceusedisorders_currenthallucinogendependence" property here: Integer
                break;
            case "substanceusedisorders_currentopioidabuse":
                // TODO: Handle the "substanceusedisorders_currentopioidabuse" property here: Integer
                break;
            case "substanceusedisorders_currentopioiddependence":
                // TODO: Handle the "substanceusedisorders_currentopioiddependence" property here: Integer
                break;
            case "substanceusedisorders_currentotherorunknownabuse":
                // TODO: Handle the "substanceusedisorders_currentotherorunknownabuse" property here: Integer
                break;
            case "substanceusedisorders_currentotherorunknowndependence":
                // TODO: Handle the "substanceusedisorders_currentotherorunknowndependence" property here: Integer
                break;
            case "substanceusedisorders_currentphencyclidineabuse":
                // TODO: Handle the "substanceusedisorders_currentphencyclidineabuse" property here: Integer
                break;
            case "substanceusedisorders_currentphencyclidinedependence":
                // TODO: Handle the "substanceusedisorders_currentphencyclidinedependence" property here: Integer
                break;
            case "substanceusedisorders_currentpolysubstancedependence":
                // TODO: Handle the "substanceusedisorders_currentpolysubstancedependence" property here: Integer
                break;
            case "substanceusedisorders_currentsedativehypnoticanxiolyticabuse":
                // TODO: Handle the "substanceusedisorders_currentsedativehypnoticanxiolyticabuse" property here: Integer
                break;
            case "substanceusedisorders_currentsedativehypnoticanxiolyticdependence":
                // TODO: Handle the "substanceusedisorders_currentsedativehypnoticanxiolyticdependence" property here: Integer
                break;
            case "substanceusedisorders_pastalcoholabuse":
                // TODO: Handle the "substanceusedisorders_pastalcoholabuse" property here: Integer
                break;
            case "substanceusedisorders_pastalcoholdependence":
                // TODO: Handle the "substanceusedisorders_pastalcoholdependence" property here: Integer
                break;
            case "substanceusedisorders_pastamphetamineabuse":
                // TODO: Handle the "substanceusedisorders_pastamphetamineabuse" property here: Integer
                break;
            case "substanceusedisorders_pastamphetaminedependence":
                // TODO: Handle the "substanceusedisorders_pastamphetaminedependence" property here: Integer
                break;
            case "substanceusedisorders_pastcannabisabuse":
                // TODO: Handle the "substanceusedisorders_pastcannabisabuse" property here: Integer
                break;
            case "substanceusedisorders_pastcannabisdependence":
                // TODO: Handle the "substanceusedisorders_pastcannabisdependence" property here: Integer
                break;
            case "substanceusedisorders_pastcocaineabuse":
                // TODO: Handle the "substanceusedisorders_pastcocaineabuse" property here: Integer
                break;
            case "substanceusedisorders_pastcocainedependence":
                // TODO: Handle the "substanceusedisorders_pastcocainedependence" property here: Integer
                break;
            case "substanceusedisorders_pasthallucinogenabuse":
                // TODO: Handle the "substanceusedisorders_pasthallucinogenabuse" property here: Integer
                break;
            case "substanceusedisorders_pasthallucinogendependence":
                // TODO: Handle the "substanceusedisorders_pasthallucinogendependence" property here: Integer
                break;
            case "substanceusedisorders_pastopioidabuse":
                // TODO: Handle the "substanceusedisorders_pastopioidabuse" property here: Integer
                break;
            case "substanceusedisorders_pastopioiddependence":
                // TODO: Handle the "substanceusedisorders_pastopioiddependence" property here: Integer
                break;
            case "substanceusedisorders_pastotherorunknownabuse":
                // TODO: Handle the "substanceusedisorders_pastotherorunknownabuse" property here: Integer
                break;
            case "substanceusedisorders_pastotherorunknowndependence":
                // TODO: Handle the "substanceusedisorders_pastotherorunknowndependence" property here: Integer
                break;
            case "substanceusedisorders_pastphencyclidineabuse":
                // TODO: Handle the "substanceusedisorders_pastphencyclidineabuse" property here: Integer
                break;
            case "substanceusedisorders_pastphencyclidinedependence":
                // TODO: Handle the "substanceusedisorders_pastphencyclidinedependence" property here: Integer
                break;
            case "substanceusedisorders_pastpolysubstancedependence":
                // TODO: Handle the "substanceusedisorders_pastpolysubstancedependence" property here: Integer
                break;
            case "substanceusedisorders_pastsedativehypnoticanxiolyticabuse":
                // TODO: Handle the "substanceusedisorders_pastsedativehypnoticanxiolyticabuse" property here: Integer
                break;
            case "substanceusedisorders_pastsedativehypnoticanxiolyticdependence":
                // TODO: Handle the "substanceusedisorders_pastsedativehypnoticanxiolyticdependence" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

