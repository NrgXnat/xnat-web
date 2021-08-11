package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAscidresearchdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAscidresearchdataSerializer<T extends XnatAscidresearchdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2426342917959202088L;

    @SuppressWarnings("unchecked")
    public XnatAscidresearchdataSerializer() {
        this((Class<T>) XnatAscidresearchdata.class);
    }

    protected XnatAscidresearchdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "adjustmentdisorder" property here: Integer
        // TODO: Write out the "administrator" property here: String
        // TODO: Write out the "anxietydisorders_currentagoraphobiawithoutpanichx" property here: Integer
        // TODO: Write out the "anxietydisorders_currentanxietydisordernos" property here: Integer
        // TODO: Write out the "anxietydisorders_currentanxietyduetomedicalcondition" property here: Integer
        // TODO: Write out the "anxietydisorders_currentgeneralizedanxietydisorder" property here: Integer
        // TODO: Write out the "anxietydisorders_currentocd" property here: Integer
        // TODO: Write out the "anxietydisorders_currentpanicwithagoraphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_currentpanicwithoutagoraphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_currentptsd" property here: Integer
        // TODO: Write out the "anxietydisorders_currentsocialphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_currentspecificphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_currentsubstanceinducedanxietydisorder" property here: Integer
        // TODO: Write out the "anxietydisorders_pastagoraphobiawithoutpanichx" property here: Integer
        // TODO: Write out the "anxietydisorders_pastanxietydisordernos" property here: Integer
        // TODO: Write out the "anxietydisorders_pastanxietyduetomedicalcondition" property here: Integer
        // TODO: Write out the "anxietydisorders_pastocd" property here: Integer
        // TODO: Write out the "anxietydisorders_pastpanicwithagoraphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_pastpanicwithoutagoraphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_pastptsd" property here: Integer
        // TODO: Write out the "anxietydisorders_pastsocialphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_pastspecificphobia" property here: Integer
        // TODO: Write out the "anxietydisorders_pastsubstanceinducedanxietydisorder" property here: Integer
        // TODO: Write out the "eatingdisorders_currentanorexianervosa" property here: Integer
        // TODO: Write out the "eatingdisorders_currentbingeeatingdisorder" property here: Integer
        // TODO: Write out the "eatingdisorders_currentbulimianervosa" property here: Integer
        // TODO: Write out the "eatingdisorders_pastanorexianervosa" property here: Integer
        // TODO: Write out the "eatingdisorders_pastbingeeatingdisorder" property here: Integer
        // TODO: Write out the "eatingdisorders_pastbulimianervosa" property here: Integer
        // TODO: Write out the "mooddisorders_currentbipolar1disorder" property here: Integer
        // TODO: Write out the "mooddisorders_currentbipolar2disorder" property here: Integer
        // TODO: Write out the "mooddisorders_currentdepressivedisordernos" property here: Integer
        // TODO: Write out the "mooddisorders_currentmajordepressivedisorder" property here: Integer
        // TODO: Write out the "mooddisorders_currentotherbipolardisorder" property here: Integer
        // TODO: Write out the "mooddisorders_pastbipolar1disorder" property here: Integer
        // TODO: Write out the "mooddisorders_pastbipolar2disorder" property here: Integer
        // TODO: Write out the "mooddisorders_pastdepressivedisordernos" property here: Integer
        // TODO: Write out the "mooddisorders_pastmajordepressivedisorder" property here: Integer
        // TODO: Write out the "mooddisorders_pastotherbipolardisorder" property here: Integer
        // TODO: Write out the "moodepisodes_currentdysthmicepisode" property here: Integer
        // TODO: Write out the "moodepisodes_currenthypomanicepisode" property here: Integer
        // TODO: Write out the "moodepisodes_currentmajordepressiveepisode" property here: Integer
        // TODO: Write out the "moodepisodes_currentmanicepisode" property here: Integer
        // TODO: Write out the "moodepisodes_currentmooddisorderduetomedicalcondition" property here: Integer
        // TODO: Write out the "moodepisodes_currentsubstanceinducedmooddisorder" property here: Integer
        // TODO: Write out the "moodepisodes_pasthypomanicepisode" property here: Integer
        // TODO: Write out the "moodepisodes_pastmajordepressiveepisode" property here: Integer
        // TODO: Write out the "moodepisodes_pastmanicepisode" property here: Integer
        // TODO: Write out the "moodepisodes_pastmooddisorderduetomedicalcondition" property here: Integer
        // TODO: Write out the "moodepisodes_pastsubstanceinducedmooddisorder" property here: Integer
        // TODO: Write out the "optional_currentacutestressdisorder" property here: Integer
        // TODO: Write out the "optional_currentminordepressivedisorder" property here: Integer
        // TODO: Write out the "optional_currentmixedanxietydepressivedisorder" property here: Integer
        // TODO: Write out the "optional_pastacutestressdisorder" property here: Integer
        // TODO: Write out the "optional_pastminordepressivedisorder" property here: Integer
        // TODO: Write out the "optional_pastmixedanxietydepressivedisorder" property here: Integer
        // TODO: Write out the "optional_pastsympomaticdetails" property here: String
        // TODO: Write out the "psychoticdisorders_currentbriefpsychoticdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentcatatonictype" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentdelusionaldisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentdisorganizedtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentparanoidtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentpsychoticdisorderduetomedicalcondition" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentpsychoticdisordernos" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentresidualtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentschizoaffectivedisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentschizophrenia" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentschizophreniformdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentsubstanceinducedpsychoticdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_currentundifferentiatedtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastbriefpsychoticdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastcatatonictype" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastdelusionaldisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastdisorganizedtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastparanoidtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastpsychoticdisorderduetomedicalcondition" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastpsychoticdisordernos" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastresidualtype" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastschizoaffectivedisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastschizophrenia" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastschizophreniformdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastsubstanceinducedpsychoticdisorder" property here: Integer
        // TODO: Write out the "psychoticdisorders_pastundifferentiatedtype" property here: Integer
        // TODO: Write out the "psychoticsymptoms_currentcatatonicbehavior" property here: Integer
        // TODO: Write out the "psychoticsymptoms_currentdelusions" property here: Integer
        // TODO: Write out the "psychoticsymptoms_currentdisorganizedspeechbehavior" property here: Integer
        // TODO: Write out the "psychoticsymptoms_currenthallucinations" property here: Integer
        // TODO: Write out the "psychoticsymptoms_currentnegativesymptoms" property here: Integer
        // TODO: Write out the "psychoticsymptoms_pastcatatonicbehavior" property here: Integer
        // TODO: Write out the "psychoticsymptoms_pastdelusions" property here: Integer
        // TODO: Write out the "psychoticsymptoms_pastdisorganizedspeechbehavior" property here: Integer
        // TODO: Write out the "psychoticsymptoms_pasthallucinations" property here: Integer
        // TODO: Write out the "psychoticsymptoms_pastnegativesymptoms" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "somatoformdisorders_bodydysmorphicdisorder" property here: Integer
        // TODO: Write out the "somatoformdisorders_hypochondriasis" property here: Integer
        // TODO: Write out the "somatoformdisorders_paindisorder" property here: Integer
        // TODO: Write out the "somatoformdisorders_somatizationdisorder" property here: Integer
        // TODO: Write out the "somatoformdisorders_undifferentiatedsomatformdisorder" property here: Integer
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "substanceusedisorders_currentalcoholabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentalcoholdependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentamphetamineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentamphetaminedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentcannabisabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentcannabisdependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentcocaineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentcocainedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currenthallucinogenabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currenthallucinogendependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentopioidabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentopioiddependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentotherorunknownabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentotherorunknowndependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentphencyclidineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentphencyclidinedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentpolysubstancedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentsedativehypnoticanxiolyticabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_currentsedativehypnoticanxiolyticdependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastalcoholabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastalcoholdependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastamphetamineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastamphetaminedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastcannabisabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastcannabisdependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastcocaineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastcocainedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pasthallucinogenabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pasthallucinogendependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastopioidabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastopioiddependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastotherorunknownabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastotherorunknowndependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastphencyclidineabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastphencyclidinedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastpolysubstancedependence" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastsedativehypnoticanxiolyticabuse" property here: Integer
        // TODO: Write out the "substanceusedisorders_pastsedativehypnoticanxiolyticdependence" property here: Integer
    }
}

