/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.XFTTable;
import org.nrg.xft.TypeConverter.JavaMapping;
import org.nrg.xft.TypeConverter.TypeConverter;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperField;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.DateUtils;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.restlet.representations.JSONTableRepresentation;
import org.nrg.xnat.services.resources.ScanListService;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author afour
 *
 */
@Service
@Slf4j
public class ScanListServiceImpl implements ScanListService {

	XnatProjectdata proj = null;
	XnatSubjectdata sub = null;
	XnatImagesessiondata session = null;
	XnatImagescandata scan = null;
	public Map<String, String> fieldMapping = new HashMap<>();

	@Override
	public String getScanResource(UserI user, String accessedId) throws IOException {
		session = XnatImagesessiondata.getXnatImagesessiondatasById(accessedId, user, false);

		if (session == null) {
			session = (XnatImagesessiondata) XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), accessedId,
					user, false);
		}

		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XnatImagescandata.SCHEMA_ELEMENT_NAME, true));

		if (scan != null) {
			return "";
			// return new ItemXMLRepresentation(scan.getItem(),MediaType.TEXT_XML);
		} else {
			XFTTable table;
			try {
				final String re = getRootElementName();

				final QueryOrganizer qo = new QueryOrganizer(re, user, ViewManager.ALL);

				populateQuery(qo);

				CriteriaCollection cc = new CriteriaCollection("AND");
				cc.addClause(re + "/image_session_id", session.getId());
				qo.setWhere(cc);

				String query = qo.buildQuery();

				table = XFTTable.Execute(query, user.getDBName(), user.getUsername());

				table = formatHeaders(table, qo, "xnat:imageScanData/ID",
						String.format("/data/experiments/%s/scans/", session.getId()));
			} catch (Exception e) {
				// logger.error("",e);
				// getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
				return null;
			}

			Hashtable<String, Object> params = new Hashtable<String, Object>();
			if (table != null)
				params.put("totalRecords", table.size());
			return new JSONTableRepresentation(table, null, params, MediaType.APPLICATION_JSON).getText();
		}
	}

	public String getDefaultElementName() {
		return "xnat:imageScanData";
	}

	public String getRootElementName() {
		try {
			GenericWrapperElement rootElementName = GenericWrapperElement.GetElement(getDefaultElementName());
			if (this.getQueryVariable("xsiType") != null && !getQueryVariable("xsiType").contains(",")) {
				return this.getQueryVariable("xsiType");
			}

			ArrayList<String> fields = new ArrayList<>();

			for (String key : getQueryVariableKeys()) {
				if (key.contains("/")) {
					fields.add(key);
				} else if (this.fieldMapping.containsKey(key)) {
					fields.add(this.fieldMapping.get(key));
				} else if (key.equals("columns")) {
					for (String col : XftStringUtils.CommaDelimitedStringToArrayList(getQueryVariable("columns"))) {
						if (col.contains("/")) {
							fields.add(col);
						} else if (this.fieldMapping.containsKey(col)) {
							fields.add(this.fieldMapping.get(col));
						}
					}
				}
			}

			for (String field : fields) {
				try {
					GenericWrapperElement ge = XftStringUtils.GetRootElement(field);
					assert ge != null;
					if (!ge.getXSIType().equals(rootElementName.getXSIType()) && ge.isExtensionOf(rootElementName)) {
						rootElementName = ge;
					}
				} catch (ElementNotFoundException e) {
					// log.error("",e);
				}
			}

			return rootElementName.getXSIType();
		} catch (Throwable e) {
			// log.error("",e);
			return this.getDefaultElementName();
		}
	}

	public XFTTable formatHeaders(XFTTable table, QueryOrganizer qo, String idpath, String URIpath) {
		final ArrayList<String> newColumns = new ArrayList<>();
		for (String column : table.getColumns()) {
			String xPath = qo.getXPATHforAlias(column.toLowerCase());
			if (xPath == null) {
				newColumns.add(column);
			} else {
				String key = this.getLabelForFieldMapping(xPath);
				if (key == null) {
					newColumns.add(xPath);
				} else {
					newColumns.add(key);
				}
			}
		}

		int idIndex = table.getColumnIndex(qo.getFieldAlias(idpath));

		if (URIpath != null)
			newColumns.add("URI");

		XFTTable clone = new XFTTable();
		clone.initTable(newColumns);
		for (Object[] row : table.rows()) {
			Object[] newRow;
			if (URIpath != null)
				newRow = new Object[row.length + 1];
			else
				newRow = new Object[row.length];

			System.arraycopy(row, 0, newRow, 0, row.length);

			String id = (String) row[idIndex];

			if (URIpath != null)
				newRow[row.length] = URIpath + id;

			clone.insertRow(newRow);
		}

		return clone;
	}

	private String getLabelForFieldMapping(String xPath) {
		for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(xPath)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void populateQuery(QueryOrganizer qo) {
		final String queryVariable = getQueryVariable("columns");
		final GenericWrapperElement rootElement = qo.getRootElement();
		if (StringUtils.isNotBlank(queryVariable) && !queryVariable.equals("DEFAULT")) {
			try {
				this.columns = XftStringUtils
						.CommaDelimitedStringToArrayList(URLDecoder.decode(queryVariable, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// log.error("",e);
				this.columns = getDefaultFields(rootElement);
			}
		} else {
			this.columns = getDefaultFields(rootElement);
		}

		for (String key : this.columns) {
			try {
				if (key.contains("/")) {
					qo.addField(key);
				} else if (this.fieldMapping.containsKey(key)) {
					qo.addField(this.fieldMapping.get(key));
				} else {
					// log.error("Unknown alias \"{}\" processing query for root element: {}", key,
					// rootElement.getName());
				}
			} catch (ElementNotFoundException e) {
				// log.error("",e);
			}
		}

		CriteriaCollection cc = new CriteriaCollection("AND");

		if (this.fieldMapping.size() > 0) {
			for (String key : fieldMapping.keySet()) {
				if (!key.equals("xsiType") && hasQueryVariable(key)) {
					cc.add(processQueryCriteria(this.fieldMapping.get(key), getQueryVariable(key)));
				}
			}
		}

		for (String key : getQueryVariableKeys()) {
			if (key.contains("/")) {
				cc.add(this.processQueryCriteria(key, getQueryVariable(key)));
			}
		}

		if (isQueryVariable("req_format", "form", false)) {
			if (this.fieldMapping.size() > 0) {
				for (String key : fieldMapping.keySet()) {
					if (hasBodyVariable(key)) {
						cc.add(this.processQueryCriteria(this.fieldMapping.get(key), getBodyVariable(key)));
					}
				}
			}

			for (String key : getBodyVariableKeys()) {
				if (key.contains("/")) {
					cc.add(this.processQueryCriteria(key, getBodyVariable(key)));
				}
			}

			if (hasBodyVariable("columns")) {
				this.columns = XftStringUtils.CommaDelimitedStringToArrayList(getBodyVariable("columns"));
				for (String col : this.columns) {
					if (col.contains("/")) {
						try {
							qo.addField(col);
						} catch (ElementNotFoundException e) {
							// log.error("",e);
						}
					} else if (this.fieldMapping.containsKey(col)) {
						try {
							qo.addField(this.fieldMapping.get(col));
						} catch (ElementNotFoundException e) {
							// log.error("",e);
						}
					}
				}
			}
		}

		if (cc.size() > 0) {
			qo.setWhere(cc);
		}
	}

	public Set<String> getBodyVariableKeys() {
		Form f = getBodyAsForm();
		if (f != null) {
			return f.getValuesMap().keySet();
		}
		return null;
	}

	public boolean isQueryVariable(String key, String value, boolean caseSensitive) {
		if (getQueryVariable(key) != null) {
			return (caseSensitive && getQueryVariable(key).equals(value))
					|| (!caseSensitive && getQueryVariable(key).equalsIgnoreCase(value));
		}
		return false;
	}

	public Set<String> getQueryVariableKeys() {
		Form f = getQueryVariableForm();
		if (f != null) {
			return f.getValuesMap().keySet();
		}
		return null;
	}

	public boolean hasBodyVariable(String key) {
		return getBodyVariable(key) != null;
	}

	public String getBodyVariable(String key) {
		Form f = getBodyAsForm();
		if (f != null) {
			return TurbineUtils.escapeParam(f.getFirstValue(key));
		}
		return null;
	}

	public CriteriaCollection processQueryCriteria(String xPath, String values) {
		CriteriaCollection cc = new CriteriaCollection("OR");
		try {
			GenericWrapperField gwf = GenericWrapperElement.GetFieldForXMLPath(xPath);
			assert gwf != null;
			String type = gwf.getType(new TypeConverter(new JavaMapping("")));

			switch (type) {
			case STRING:
				cc.add(processStringQuery(xPath, values));
				break;
			case DOUBLE:
				cc.add(processNumericQuery(xPath, values));
				break;
			case INTEGER:
				cc.add(processNumericQuery(xPath, values));
				break;
			case DATE:
				cc.add(processDateQuery(xPath, values));
				break;
			case BOOL:
				cc.add(processBooleanQuery(xPath, values));
				break;
			}
		} catch (XFTInitException e) {
			// log.error("An error occurred during XFT initialization",e);
		} catch (ElementNotFoundException e) {
			// log.error("Couldn't find an element in the xPath {}", xPath,e);
		} catch (FieldNotFoundException e) {
			// log.error("Couldn't find the field specified by the xPath {}", xPath,e);
		}
		return cc;
	}

	public CriteriaCollection processStringQuery(String xmlPath, String values) {
		ArrayList<String> al = XftStringUtils.CommaDelimitedStringToArrayList(values);
		CriteriaCollection cc = new CriteriaCollection("OR");
		for (String value : al) {
			if (value.contains("%") || value.contains("*")) {
				value = StringUtils.replace(value, "*", "%");
				cc.addClause(xmlPath, "LIKE", value);
			} else {
				cc.addClause(xmlPath, value);
			}
		}
		return cc;
	}

	public CriteriaCollection processNumericQuery(String column, String values) {
		ArrayList<String> al = XftStringUtils.CommaDelimitedStringToArrayList(values);
		CriteriaCollection cc = new CriteriaCollection("OR");
		for (String date : al) {
			if (date.contains("-")) {
				String date1 = date.substring(0, date.indexOf("-"));

				String date2 = date.substring(date.indexOf("-") + 1);
				CriteriaCollection subCC = new CriteriaCollection("AND");
				subCC.addClause(column, ">=", date1);
				subCC.addClause(column, "<=", date2);
				cc.add(subCC);
			} else {
				cc.addClause(column, date);
			}
		}
		return cc;
	}

	public CriteriaCollection processDateQuery(String column, String dates) {
		ArrayList<String> al = XftStringUtils.CommaDelimitedStringToArrayList(dates);
		CriteriaCollection cc = new CriteriaCollection("OR");
		for (String date : al) {
			if (date.contains("-")) {
				String date1;
				try {
					date1 = DateUtils.parseDate(date.substring(0, date.indexOf("-"))).toString();
				} catch (ParseException e) {
					date1 = date.substring(0, date.indexOf("-"));
				}

				String date2;
				try {
					date2 = DateUtils.parseDate(date.substring(date.indexOf("-") + 1)).toString();
				} catch (ParseException e) {
					date2 = date.substring(date.indexOf("-") + 1);
				}

				CriteriaCollection subCC = new CriteriaCollection("AND");
				subCC.addClause(column, ">=", date1);
				subCC.addClause(column, "<=", date2);
				cc.add(subCC);
			} else {
				String date1;
				try {
					date1 = DateUtils.parseDate(date).toString();
				} catch (ParseException e) {
					date1 = date;
				}
				cc.addClause(column, date1);
			}
		}
		return cc;
	}

	public CriteriaCollection processBooleanQuery(String column, String values) {
		ArrayList<String> al = XftStringUtils.CommaDelimitedStringToArrayList(values);
		CriteriaCollection cc = new CriteriaCollection("OR");
		for (String value : al) {
			cc.addClause(column, value);
		}
		return cc;
	}

	// HC
	private Form getBodyAsForm() {
		// if (_body == null) {
		// final Representation entity = getRequest().getEntity();
		// if (RequestUtil.isMultiPartFormData(entity) && entity.getSize() > 0) {
		// _mediaType = entity.getMediaType();
		// _body = new Form(entity);
		// }
		// }

		return _body;
	}

	public ArrayList<String> getDefaultFields(GenericWrapperElement e) {
		ArrayList<String> al = new ArrayList<String>();
		al.add("xnat_imagescandata_id");
		al.add("ID");
		al.add("type");
		al.add("quality");
		al.add("xsiType");
		al.add("note");
		al.add("series_description");

		return al;
	}

	private Form f = null;

	private Form getQueryVariableForm() {
		if (f == null) {
			f = getQueryVariableForm(new Request());
		}
		return f;
	}

	public boolean hasQueryVariable(String key) {
		return containsQueryVariable(key);
	}

	public boolean containsQueryVariable(String key) {
		return getQueryVariable(key) != null;
	}

	public String getQueryVariable(String key) {
		if (key.equals("columns"))
			return null;
		return key;
	}

	public static String getQueryVariable(String key, Request request) {
		Form f = getQueryVariableForm(request);
		if (f != null && f.getValuesMap().containsKey(key)) {
			return TurbineUtils.escapeParam(f.getFirstValue(key));
		}
		return null;
	}

	// HC
	private static Form getQueryVariableForm(Request request) {
		Form form = new Form();
		form.add("format", "json");
		form.add("accessible", "true");
		form.add("x", "xhr7t78bbwt");
		return form;
		// return request.getResourceRef().getQueryAsForm();
	}

	public ArrayList<String> columns = null;
	private Form _body;
	private MediaType _mediaType;
	private final static String STRING = "java.lang.String";
	private final static String DOUBLE = "java.lang.Double";
	private final static String INTEGER = "java.lang.Integer";
	private final static String DATE = "java.util.Date";
	private final static String BOOL = "java.lang.Boolean";

}
