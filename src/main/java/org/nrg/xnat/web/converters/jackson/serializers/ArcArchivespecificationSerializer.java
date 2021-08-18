package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecification;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ArcArchivespecificationSerializer<T extends ArcArchivespecification> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 800525773763916393L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcArchivespecificationSerializer() {
        this((Class<T>) ArcArchivespecification.class);
    }

    protected ArcArchivespecificationSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "arcArchivespecificationId", instance.getArcArchivespecificationId());
        writeNonNullBoolean(generator, "dcm_appletLink", instance.getDcm_appletLink());
        writeNonBlankField(generator, "dcm_dcmAe", instance.getDcm_dcmAe());
        writeNonBlankField(generator, "dcm_dcmHost", instance.getDcm_dcmHost());
        writeNonBlankField(generator, "dcm_dcmPort", instance.getDcm_dcmPort());
        writeNonBlankField(generator, "dcm_httpUrl", instance.getDcm_httpUrl());
        writeNonNullBoolean(generator, "emailspecifications_newUserRegistration", instance.getEmailspecifications_newUserRegistration());
        writeNonNullBoolean(generator, "emailspecifications_pageEmail", instance.getEmailspecifications_pageEmail());
        writeNonNullBoolean(generator, "emailspecifications_pipeline", instance.getEmailspecifications_pipeline());
        writeNonNullBoolean(generator, "emailspecifications_projectAccess", instance.getEmailspecifications_projectAccess());
        writeNonNullBoolean(generator, "emailspecifications_transfer", instance.getEmailspecifications_transfer());
        writeNonNullBoolean(generator, "enableCsrfToken", instance.getEnableCsrfToken());
        writeNonNullBoolean(generator, "enableNewRegistrations", instance.getEnableNewRegistrations());
        // TODO: Write out the "fieldspecifications_fieldspecification" property here: java.util.List
        // TODO: Write out the "globalpaths" property here: org.nrg.xdat.model.ArcPathinfoI
        // TODO: Write out the "notificationTypes_notificationType" property here: java.util.List
        writeNonNullNumber(generator, "prearchiveCode", instance.getPrearchiveCode());
        // TODO: Write out the "projects_project" property here: java.util.List
        writeNonNullNumber(generator, "quarantineCode", instance.getQuarantineCode());
        writeNonNullBoolean(generator, "requireLogin", instance.getRequireLogin());
        writeNonBlankField(generator, "siteAdminEmail", instance.getSiteAdminEmail());
        writeNonBlankField(generator, "siteId", instance.getSiteId());
        writeNonBlankField(generator, "siteUrl", instance.getSiteUrl());
        writeNonBlankField(generator, "smtpHost", instance.getSmtpHost());
    }
}

