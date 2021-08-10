package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatInvestigatordata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatInvestigatordataDeserializer<T extends XnatInvestigatordata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1983327625611925901L;

    @SuppressWarnings("unchecked")
    public XnatInvestigatordataDeserializer() {
        this((Class<T>) XnatInvestigatordata.class);
    }

    protected XnatInvestigatordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "xnatInvestigatordataId":
                instance.setXnatInvestigatordataId(parser.getIntValue());
                break;
            case "firstname":
                instance.setFirstname(parser.getText());
                break;
            case "lastname":
                instance.setLastname(parser.getText());
                break;
            case "title":
                instance.setTitle(parser.getText());
                break;
            case "institution":
                instance.setInstitution(parser.getText());
                break;
            case "department":
                instance.setDepartment(parser.getText());
                break;
            case "email":
                instance.setEmail(parser.getText());
                break;
            case "phone":
                instance.setPhone(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
