package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecification;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcArchivespecificationSerializer<T extends ArcArchivespecification> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 800525773763916393L;

    @SuppressWarnings("unchecked")
    public ArcArchivespecificationSerializer() {
        this((Class<T>) ArcArchivespecification.class);
    }

    protected ArcArchivespecificationSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcArchivespecificationId" property here: Integer
        // TODO: Write out the "dcm_appletLink" property here: Boolean
        // TODO: Write out the "dcm_dcmAe" property here: String
        // TODO: Write out the "dcm_dcmHost" property here: String
        // TODO: Write out the "dcm_dcmPort" property here: String
        // TODO: Write out the "dcm_httpUrl" property here: String
        // TODO: Write out the "emailspecifications_newUserRegistration" property here: Boolean
        // TODO: Write out the "emailspecifications_pageEmail" property here: Boolean
        // TODO: Write out the "emailspecifications_pipeline" property here: Boolean
        // TODO: Write out the "emailspecifications_projectAccess" property here: Boolean
        // TODO: Write out the "emailspecifications_transfer" property here: Boolean
        // TODO: Write out the "enableCsrfToken" property here: Boolean
        // TODO: Write out the "enableNewRegistrations" property here: Boolean
        // TODO: Write out the "fieldspecifications_fieldspecification" property here: java.util.List
        // TODO: Write out the "globalArchivePath" property here: String
        // TODO: Write out the "globalBuildPath" property here: String
        // TODO: Write out the "globalCachePath" property here: String
        // TODO: Write out the "globalPrearchivePath" property here: String
        // TODO: Write out the "globalpaths" property here: org.nrg.xdat.om.ArcPathinfo
        // TODO: Write out the "notificationTypes_notificationType" property here: java.util.List
        // TODO: Write out the "prearchiveCode" property here: Integer
        // TODO: Write out the "projects_project" property here: java.util.List
        // TODO: Write out the "quarantineCode" property here: Integer
        // TODO: Write out the "requireLogin" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "siteAdminEmail" property here: String
        // TODO: Write out the "siteId" property here: String
        // TODO: Write out the "siteUrl" property here: String
        // TODO: Write out the "smtpHost" property here: String
    }
}

