package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcscandataDeserializer<T extends XnatQcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7654435521427410595L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcscandataDeserializer() {
        this((Class<T>) XnatQcscandata.class);
    }

    protected XnatQcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                instance.setComments(parser.getText());
                break;
            case "coverage":
                instance.setCoverage(parser.getText());
                break;
            case "fields_field":
                // TODO: Handle the "fields_field" property here: java.util.List
                break;
            case "imagescanId":
                instance.setImagescanId(parser.getText());
                break;
            case "motion":
                instance.setMotion(parser.getText());
                break;
            case "otherimageartifacts":
                instance.setOtherimageartifacts(parser.getText());
                break;
            case "pass":
                instance.setPass(parser.getText());
                break;
            case "rater":
                instance.setRater(parser.getText());
                break;
            case "rating":
                instance.setRating(parser.getText());
                break;
            case "rating_scale":
                instance.setRating_scale(parser.getText());
                break;
            case "xnatQcscandataId":
                instance.setXnatQcscandataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

