package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecification;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcArchivespecificationDeserializer<T extends ArcArchivespecification> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -450299104821653831L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcArchivespecificationDeserializer() {
        this((Class<T>) ArcArchivespecification.class);
    }

    protected ArcArchivespecificationDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcArchivespecificationId":
                instance.setArcArchivespecificationId(parser.getIntValue());
                break;
            case "dcm_appletLink":
                instance.setDcm_appletLink(parser.getBooleanValue());
                break;
            case "dcm_dcmAe":
                instance.setDcm_dcmAe(parser.getText());
                break;
            case "dcm_dcmHost":
                instance.setDcm_dcmHost(parser.getText());
                break;
            case "dcm_dcmPort":
                instance.setDcm_dcmPort(parser.getText());
                break;
            case "dcm_httpUrl":
                instance.setDcm_httpUrl(parser.getText());
                break;
            case "emailspecifications_newUserRegistration":
                instance.setEmailspecifications_newUserRegistration(parser.getBooleanValue());
                break;
            case "emailspecifications_pageEmail":
                instance.setEmailspecifications_pageEmail(parser.getBooleanValue());
                break;
            case "emailspecifications_pipeline":
                instance.setEmailspecifications_pipeline(parser.getBooleanValue());
                break;
            case "emailspecifications_projectAccess":
                instance.setEmailspecifications_projectAccess(parser.getBooleanValue());
                break;
            case "emailspecifications_transfer":
                instance.setEmailspecifications_transfer(parser.getBooleanValue());
                break;
            case "enableCsrfToken":
                instance.setEnableCsrfToken(parser.getBooleanValue());
                break;
            case "enableNewRegistrations":
                instance.setEnableNewRegistrations(parser.getBooleanValue());
                break;
            case "fieldspecifications_fieldspecification":
                // TODO: Handle the "fieldspecifications_fieldspecification" property here: java.util.List
                break;
            case "globalPaths":
                // TODO: Handle the "globalPaths" property here: org.nrg.xdat.model.ArcPathinfoI
                break;
            case "notificationTypes_notificationType":
                // TODO: Handle the "notificationTypes_notificationType" property here: java.util.List
                break;
            case "prearchiveCode":
                instance.setPrearchiveCode(parser.getIntValue());
                break;
            case "projects_project":
                // TODO: Handle the "projects_project" property here: java.util.List
                break;
            case "quarantineCode":
                instance.setQuarantineCode(parser.getIntValue());
                break;
            case "requireLogin":
                instance.setRequireLogin(parser.getBooleanValue());
                break;
            case "siteAdminEmail":
                instance.setSiteAdminEmail(parser.getText());
                break;
            case "siteId":
                instance.setSiteId(parser.getText());
                break;
            case "siteUrl":
                instance.setSiteUrl(parser.getText());
                break;
            case "smtpHost":
                instance.setSmtpHost(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
