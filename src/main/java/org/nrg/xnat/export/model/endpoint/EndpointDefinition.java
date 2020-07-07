package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.List;

import org.nrg.xnat.export.exception.ExportSettingNotFoundException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mohana Ramaratnam
 *
 */
public class EndpointDefinition implements Serializable {
	  
	private static final long serialVersionUID = -1294611492056871173L;


	  @JsonProperty("export-handler")
	  private String export_handler;
	  
	  
	  private String description;
	  
	  @JsonProperty("credentials_required")
	  private boolean credentialsRequired;
	  
	  private boolean export_xnat_meta_data;
	  
	  private boolean maintain_xnat_folder_structure;
	  
	  @JsonProperty("notify")
	  private String notificationEmails;
	  
	  @JsonProperty("export_settings")
	  private List<EndpointSettingItem> exportSettings;
	  
	  private List<EndpointSettingItem> inputs;
	  
	  @JsonProperty("exported_data")
	  private EndpointExportedData exportedData;
	/**
	 * @return the export_handler
	 */
	public String getExportHandler() {
		return export_handler;
	}
	/**
	 * @param export_handler the export_handler to set
	 */
	public void setExportHandler(String label) {
		this.export_handler = label;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the credentialsRequired
	 */
	public boolean isCredentialsRequired() {
		return credentialsRequired;
	}
	/**
	 * @param credentialsRequired the credentialsRequired to set
	 */
	public void setCredentialsRequired(boolean credentialsRequired) {
		this.credentialsRequired = credentialsRequired;
	}
	/**
	 * @return the export_xnat_meta_data
	 */
	public boolean isExport_xnat_meta_data() {
		return export_xnat_meta_data;
	}
	/**
	 * @param export_xnat_meta_data the export_xnat_meta_data to set
	 */
	public void setExport_xnat_meta_data(boolean export_xnat_meta_data) {
		this.export_xnat_meta_data = export_xnat_meta_data;
	}
	/**
	 * @return the maintain_xnat_folder_structure
	 */
	public boolean isMaintain_xnat_folder_structure() {
		return maintain_xnat_folder_structure;
	}
	/**
	 * @param maintain_xnat_folder_structure the maintain_xnat_folder_structure to set
	 */
	public void setMaintain_xnat_folder_structure(boolean maintain_xnat_folder_structure) {
		this.maintain_xnat_folder_structure = maintain_xnat_folder_structure;
	}
	/**
	 * @return the notificationEmails
	 */
	public String getNotificationEmails() {
		return notificationEmails;
	}
	/**
	 * @param notificationEmails the notificationEmails to set
	 */
	public void setNotificationEmails(String notificationEmails) {
		this.notificationEmails = notificationEmails;
	}
	/**
	 * @return the exportSettings
	 */
	public List<EndpointSettingItem> getExportSettings() {
		return exportSettings;
	}
	/**
	 * @param exportSettings the exportSettings to set
	 */
	public void setExportSettings(List<EndpointSettingItem> exportSettings) {
		this.exportSettings = exportSettings;
	}
	/**
	 * @return the inputs
	 */
	public List<EndpointSettingItem> getInputs() {
		return inputs;
	}
	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(List<EndpointSettingItem> inputs) {
		this.inputs = inputs;
	}
	
	
	/**
	 * @return the exportedData
	 */
	public EndpointExportedData getExportedData() {
		return exportedData;
	}
	/**
	 * @param exportedData the exportedData to set
	 */
	public void setExportedData(EndpointExportedData exportedData) {
		this.exportedData = exportedData;
	}

	
	@JsonIgnore
	public EndpointSettingItem getExportSettingForProp(String prop) throws ExportSettingNotFoundException{
		EndpointSettingItem setting = null;
		if (prop == null) {
			throw new ExportSettingNotFoundException("Prop not found in export setting", new IllegalArgumentException());
		}
		for (EndpointSettingItem e : getExportSettings()) {
			if (e.getName().equals(prop)) {
				setting = e;
				break;
			}
		}
		return setting;
	}
	  
	@JsonIgnore
	public String getExportSettingValueForProp(String prop) throws ExportSettingNotFoundException{
		String value = null;
		if (prop == null) {
			throw new ExportSettingNotFoundException("Prop not found in export setting", new IllegalArgumentException());
		}
		for (EndpointSettingItem e : getExportSettings()) {
			if (e.getName().equals(prop)) {
				value = e.getValue();
				break;
			}
		}
		return value;
	}

	@JsonIgnore
	public String getInputSettingValueForProp(String prop) throws ExportSettingNotFoundException{
		String value = null;
		if (prop == null) {
			throw new ExportSettingNotFoundException("Prop not found in input setting", new IllegalArgumentException());
		}
		for (EndpointSettingItem i : getInputs()) {
			if (i.getName().equals(prop)) {
				value = i.getValue();
				break;
			}
		}
		return value;
	}

}
