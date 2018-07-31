package org.nrg.xnat.helpers.uri.archive;

/**
 * Created by mike on 7/31/18.
 */
public class DoiCreationHelper {

    /** The project ID. */
    private String projectId;

    /** The ID of the XNAT data object. */
    private String objectId;

    /** The DOI identifier. */
    private String doi;

    /** The data type of the data object this DOI maps to. */
    private String xsiType;

    /** The ID of the DOI Credentials object used to create the DOI. */
    private Long issuerId;

    /** The username of the XNAT user who created the DOI. */
    private String xnatUsername;

    /** The password of the DOI creation account. */
    private String issuerPassword;

    /** The xml with the metadata for the DOI. */
    private String metadataXml;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getXsiType() {
        return xsiType;
    }

    public void setXsiType(String xsiType) {
        this.xsiType = xsiType;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public String getXnatUsername() {
        return xnatUsername;
    }

    public void setXnatUsername(String xnatUsername) {
        this.xnatUsername = xnatUsername;
    }

    public String getIssuerPassword() {
        return issuerPassword;
    }

    public void setIssuerPassword(String issuerPassword) {
        this.issuerPassword = issuerPassword;
    }

    public String getMetadataXml() {
        return metadataXml;
    }

    public void setMetadataXml(String metadataXml) {
        this.metadataXml = metadataXml;
    }

    public DoiCreationHelper() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoiCreationHelper that = (DoiCreationHelper) o;

        if (projectId != null ? !projectId.equals(that.projectId) : that.projectId != null) return false;
        if (objectId != null ? !objectId.equals(that.objectId) : that.objectId != null) return false;
        if (doi != null ? !doi.equals(that.doi) : that.doi != null) return false;
        if (xsiType != null ? !xsiType.equals(that.xsiType) : that.xsiType != null) return false;
        if (issuerId != null ? !issuerId.equals(that.issuerId) : that.issuerId != null) return false;
        if (xnatUsername != null ? !xnatUsername.equals(that.xnatUsername) : that.xnatUsername != null) return false;
        if (issuerPassword != null ? !issuerPassword.equals(that.issuerPassword) : that.issuerPassword != null)
            return false;
        return metadataXml != null ? metadataXml.equals(that.metadataXml) : that.metadataXml == null;
    }

    @Override
    public int hashCode() {
        int result = projectId != null ? projectId.hashCode() : 0;
        result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
        result = 31 * result + (doi != null ? doi.hashCode() : 0);
        result = 31 * result + (xsiType != null ? xsiType.hashCode() : 0);
        result = 31 * result + (issuerId != null ? issuerId.hashCode() : 0);
        result = 31 * result + (xnatUsername != null ? xnatUsername.hashCode() : 0);
        result = 31 * result + (issuerPassword != null ? issuerPassword.hashCode() : 0);
        result = 31 * result + (metadataXml != null ? metadataXml.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DoiCreationHelper{" +
                "projectId='" + projectId + '\'' +
                ", objectId='" + objectId + '\'' +
                ", doi='" + doi + '\'' +
                ", xsiType='" + xsiType + '\'' +
                ", issuerId=" + issuerId +
                ", xnatUsername='" + xnatUsername + '\'' +
                ", issuerPassword='" + issuerPassword + '\'' +
                ", metadataXml='" + metadataXml + '\'' +
                '}';
    }
}
