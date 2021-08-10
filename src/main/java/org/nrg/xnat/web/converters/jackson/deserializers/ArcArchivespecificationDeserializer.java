package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecification;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcArchivespecificationDeserializer<T extends ArcArchivespecification> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8191670307358220949L;

    @SuppressWarnings("unchecked")
    public ArcArchivespecificationDeserializer() {
        this((Class<T>) ArcArchivespecification.class);
    }

    public ArcArchivespecificationDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcArchivespecificationId":
                // TODO: Handle the "arcArchivespecificationId" property here: Integer
                break;
            case "dcm_appletLink":
                // TODO: Handle the "dcm_appletLink" property here: Boolean
                break;
            case "dcm_dcmAe":
                // TODO: Handle the "dcm_dcmAe" property here: String
                break;
            case "dcm_dcmHost":
                // TODO: Handle the "dcm_dcmHost" property here: String
                break;
            case "dcm_dcmPort":
                // TODO: Handle the "dcm_dcmPort" property here: String
                break;
            case "dcm_httpUrl":
                // TODO: Handle the "dcm_httpUrl" property here: String
                break;
            case "emailspecifications_newUserRegistration":
                // TODO: Handle the "emailspecifications_newUserRegistration" property here: Boolean
                break;
            case "emailspecifications_pageEmail":
                // TODO: Handle the "emailspecifications_pageEmail" property here: Boolean
                break;
            case "emailspecifications_pipeline":
                // TODO: Handle the "emailspecifications_pipeline" property here: Boolean
                break;
            case "emailspecifications_projectAccess":
                // TODO: Handle the "emailspecifications_projectAccess" property here: Boolean
                break;
            case "emailspecifications_transfer":
                // TODO: Handle the "emailspecifications_transfer" property here: Boolean
                break;
            case "enableCsrfToken":
                // TODO: Handle the "enableCsrfToken" property here: Boolean
                break;
            case "enableNewRegistrations":
                // TODO: Handle the "enableNewRegistrations" property here: Boolean
                break;
            case "fieldspecifications_fieldspecification":
                // TODO: Handle the "fieldspecifications_fieldspecification" property here: java.util.List
                break;
            case "globalArchivePath":
                // TODO: Handle the "globalArchivePath" property here: String
                break;
            case "globalBuildPath":
                // TODO: Handle the "globalBuildPath" property here: String
                break;
            case "globalCachePath":
                // TODO: Handle the "globalCachePath" property here: String
                break;
            case "globalPrearchivePath":
                // TODO: Handle the "globalPrearchivePath" property here: String
                break;
            case "globalpaths":
                // TODO: Handle the "globalpaths" property here: org.nrg.xdat.om.ArcPathinfo
                break;
            case "notificationTypes_notificationType":
                // TODO: Handle the "notificationTypes_notificationType" property here: java.util.List
                break;
            case "prearchiveCode":
                // TODO: Handle the "prearchiveCode" property here: Integer
                break;
            case "projects_project":
                // TODO: Handle the "projects_project" property here: java.util.List
                break;
            case "quarantineCode":
                // TODO: Handle the "quarantineCode" property here: Integer
                break;
            case "requireLogin":
                // TODO: Handle the "requireLogin" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "siteAdminEmail":
                // TODO: Handle the "siteAdminEmail" property here: String
                break;
            case "siteId":
                // TODO: Handle the "siteId" property here: String
                break;
            case "siteUrl":
                // TODO: Handle the "siteUrl" property here: String
                break;
            case "smtpHost":
                // TODO: Handle the "smtpHost" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

