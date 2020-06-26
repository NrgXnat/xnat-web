// Copyright 2019 Radiologics, Inc
// Developer: Mohana Ramaratnam <mohana@radiologics.com>

package org.nrg.xnat.export.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.*;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SearchXMLBuilder {
	public String execute(final List<String> projects,
						  @Nonnull final String dataType,
						  final UserI user,
						  final String whereClause,
						  @Nullable List<String> scan_types){

		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		sb.append("<xdat:bundle ID=\"\" allow-diff-columns=\"0\" secure=\"0\" brief-description=\"Sessions\" xmlns:arc=\"http://nrg.wustl.edu/arc\" xmlns:val=\"http://nrg.wustl.edu/val\" xmlns:pipe=\"http://nrg.wustl.edu/pipe\" xmlns:wrk=\"http://nrg.wustl.edu/workflow\" xmlns:scr=\"http://nrg.wustl.edu/scr\" xmlns:xdat=\"http://nrg.wustl.edu/security\" xmlns:cat=\"http://nrg.wustl.edu/catalog\" xmlns:prov=\"http://www.nbirn.net/prov\" xmlns:xnat=\"http://nrg.wustl.edu/xnat\" xmlns:xnat_a=\"http://nrg.wustl.edu/xnat_assessments\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://nrg.wustl.edu/workflow " + XDAT.getSiteUrl() + "/schemas/workflow.xsd http://nrg.wustl.edu/catalog " + XDAT.getSiteUrl() + "/schemas/catalog.xsd http://nrg.wustl.edu/pipe " + XDAT.getSiteUrl() + "/schemas/repository.xsd http://nrg.wustl.edu/scr " + XDAT.getSiteUrl() + "/schemas/screeningAssessment.xsd http://nrg.wustl.edu/arc " + XDAT.getSiteUrl() + "/schemas/project.xsd http://nrg.wustl.edu/val " + XDAT.getSiteUrl() + "/schemas/protocolValidation.xsd http://nrg.wustl.edu/xnat " + XDAT.getSiteUrl() + "/schemas/xnat.xsd http://nrg.wustl.edu/xnat_assessments " + XDAT.getSiteUrl() + "/schemas/assessments.xsd http://www.nbirn.net/prov " + XDAT.getSiteUrl() + "/schemas/birnprov.xsd http://nrg.wustl.edu/security " + XDAT.getSiteUrl() + "/schemas/security.xsd\">");
		sb.append("<xdat:root_element_name>").append(dataType).append("</xdat:root_element_name>");

		switch (dataType) {
			case XnatProjectdata.SCHEMA_ELEMENT_NAME:
				sb.append("<xdat:search_field>");
				sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
				sb.append("<xdat:field_ID>ID</xdat:field_ID>");
				sb.append("<xdat:sequence>0</xdat:sequence>");
				sb.append("<xdat:type>string</xdat:type>");
				sb.append("<xdat:header>Project</xdat:header>");
				sb.append("</xdat:search_field>");
				break;
			case XnatSubjectdata.SCHEMA_ELEMENT_NAME:
				addProjectColumn(sb, dataType);
				addSubjectColumn(sb, dataType, projects);
				break;
			default:
				addProjectColumn(sb, dataType);
				addSubjectColumn(sb, dataType, projects);
				addSessionColumn(sb, dataType, projects);
		}

		int sequence=100;

//		if(scan_types!=null){
//			for(String sType: scan_types){
//				String pipelineDisplay="<xdat:search_field><xdat:element_name>"+dataType+"</xdat:element_name>" +
//						"<xdat:field_ID>SCAN_TYPE_COUNT="+ sType +"</xdat:field_ID>" +
//						"<xdat:sequence>"+sequence+"</xdat:sequence>" +
//						"<xdat:type>integer</xdat:type>" +
//						"<xdat:header>"+sType+"</xdat:header>" +
//						"<xdat:value>"+sType+"</xdat:value>" +
//						"</xdat:search_field>";
//				sb.append(pipelineDisplay);
//				sequence++;
//			}
//		}

		sb.append(whereClause);

		sb.append("</xdat:bundle>");

		return sb.toString();
	}
	private void addProjectColumn(StringBuilder sb, String dataType) {
		sb.append("<xdat:search_field>");
		sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
		sb.append("<xdat:field_ID>PROJECT</xdat:field_ID>");
		sb.append("<xdat:sequence>0</xdat:sequence>");
		sb.append("<xdat:type>string</xdat:type>");
		sb.append("<xdat:header>Project</xdat:header>");
		sb.append("</xdat:search_field>");
	}

	private void addSubjectColumn(StringBuilder sb, String dataType, List<String> projects) {
		addLabelField(sb, XnatSubjectdata.SCHEMA_ELEMENT_NAME, projects, XnatSubjectdata.SCHEMA_ELEMENT_NAME.equals(dataType));
	}

	private void addSessionColumn(StringBuilder sb, String dataType, List<String> projects) {
		addLabelField(sb, dataType, projects, true);
		sb.append("<xdat:search_field>");
		sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
		sb.append("<xdat:field_ID>VISIT</xdat:field_ID>");
		sb.append("<xdat:sequence>4</xdat:sequence>");
		sb.append("<xdat:type>string</xdat:type>");
		sb.append("<xdat:header>Visit</xdat:header>");
		sb.append("</xdat:search_field>");
	}

	private void addLabelField(StringBuilder sb, String dataType, List<String> projects, boolean baseDataTypeMatches) {
		String projIdField = "PROJECT_IDENTIFIER";
		String header = "Session";
		String seq = "3";
		if (XnatSubjectdata.SCHEMA_ELEMENT_NAME.equals(dataType)) {
			header = "Subject";
			seq = "2";
			projIdField = "SUB_PROJECT_IDENTIFIER";
		} else if (XnatMrsessiondata.SCHEMA_ELEMENT_NAME.equals(dataType)) {
			projIdField = "MR_PROJECT_IDENTIFIER";
		} else if (dataType.contains("Session")) {
			projIdField = dataType.replaceFirst(":", "_").toUpperCase() + "_PROJECT_IDENTIFIER";
		}

		if (projects.size()>1) {
			if (baseDataTypeMatches) {
				sb.append("<xdat:search_field>");
				sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
				sb.append("<xdat:field_ID>ID</xdat:field_ID>");
				sb.append("<xdat:sequence>1</xdat:sequence>");
				sb.append("<xdat:type>string</xdat:type>");
				sb.append("<xdat:header>id</xdat:header>");
				sb.append("</xdat:search_field>");
			}
			//if more then 1 project is in scope, then show default label
			sb.append("<xdat:search_field>");
			sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
			if (baseDataTypeMatches) {
				sb.append("<xdat:field_ID>LABEL</xdat:field_ID>");
			} else {
				sb.append("<xdat:field_ID>SUBJECT_LABEL</xdat:field_ID>");
			}
			sb.append("<xdat:sequence>").append(seq).append("</xdat:sequence>");
			sb.append("<xdat:type>string</xdat:type>");
			sb.append("<xdat:header>").append(header).append("</xdat:header>");
			sb.append("</xdat:search_field>");
		} else {
			//if only 1 project is in scope, show that project's label
			sb.append("<xdat:search_field>");
			sb.append("<xdat:element_name>").append(dataType).append("</xdat:element_name>");
			sb.append("<xdat:field_ID>").append(projIdField).append("=").append(projects.get(0)).append("</xdat:field_ID>");
			sb.append("<xdat:sequence>").append(seq).append("</xdat:sequence>");
			sb.append("<xdat:type>string</xdat:type>");
			sb.append("<xdat:header>").append(header).append("</xdat:header>");
			sb.append("<xdat:value>").append(projects.get(0)).append("</xdat:value>");
			sb.append("</xdat:search_field>");
		}
	}

}