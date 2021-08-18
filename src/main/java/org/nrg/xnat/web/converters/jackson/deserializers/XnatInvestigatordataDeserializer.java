package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatInvestigatordataDeserializer<T extends XnatInvestigatordata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2554868160566424920L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatInvestigatordataDeserializer() {
        this((Class<T>) XnatInvestigatordata.class);
    }

    protected XnatInvestigatordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "department":
                instance.setDepartment(parser.getText());
                break;
            case "email":
                instance.setEmail(parser.getText());
                break;
            case "firstname":
                instance.setFirstname(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "institution":
                instance.setInstitution(parser.getText());
                break;
            case "lastname":
                instance.setLastname(parser.getText());
                break;
            case "phone":
                instance.setPhone(parser.getText());
                break;
            case "title":
                instance.setTitle(parser.getText());
                break;
            case "xnatInvestigatordataId":
                instance.setXnatInvestigatordataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

