package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.nrg.xdat.om.XnatProjectdata;

import java.io.IOException;

@Slf4j
public class XnatProjectdataDeserializer<T extends XnatProjectdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3867498404888076051L;

    @SuppressWarnings("unchecked")
    public XnatProjectdataDeserializer() {
        this((Class<T>) XnatProjectdata.class);
    }

    protected XnatProjectdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "secondaryId":
                instance.setSecondaryId(parser.getText());
                break;
            case "keywords":
                instance.setKeywords(parser.getText());
                break;
            case "active":
                instance.setActive(parser.getText());
                break;
            case "investigator":
                final XnatInvestigatordata investigator = parser.readValueAs(XnatInvestigatordata.class);
                investigator.setXnatInvestigatordataId(investigator.getXnatInvestigatordataId());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
