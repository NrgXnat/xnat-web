package org.nrg.xnat.services.dump.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.nrg.action.ClientException;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.dump.util.ResourceTypeUtil.CatFilterWithPath;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.util.Template;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class HeaderTypeUtil {
	private static String dumpType;
	 private static final int MAXFILENUMBER=10000;
	
	public HeaderTypeUtil(String dumpType) {
		HeaderTypeUtil.dumpType = dumpType;
	}
	public  enum HeaderType {
        FILE("/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}/resources/"+dumpType+"/files/{FILENAME}",
                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}/resources/"+dumpType+"/files/{FILENAME}",
                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}/resources/"+dumpType+"/files/{FILENAME}",
                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}/resources/"+dumpType+"/files/{FILENAME}",
                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}/resources/"+dumpType+"/files/{FILENAME}"
        ) {
            private static final String FILENAME_PARAM = "FILENAME";

            @Override
            CatFilterWithPath getFilter(final Env env, final UserI user) {
                final Object filename = env.attrs.get(FILENAME_PARAM);
                final String project = env.getProject(env);
                return new CatFilterWithPath() {
                    public boolean accept(CatEntryI entry) {
                        final File f = CatalogUtils.getFile(entry, path, project);
                        return f != null && f.getName().equals(filename);
                    }
                };
            }
        },

        SCAN("/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}",
                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}",
                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}",
                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}",
                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}",
                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}",
                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}"
        ),

        SESSION("/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}",
                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}",
                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}"
        ),

        UNKNOWN() {
            @Override
            public String retrieve(Env env, UserI user) {
                return null;
            }
        };

        private final ImmutableMap<Template, ResourceTypeUtil.ResourceType> templates;

        HeaderType(final String... templates) {
            // Convert the provided string templates to Template objects
            final ImmutableMap.Builder<Template, ResourceTypeUtil.ResourceType> builder = ImmutableMap.builder();
            for (final String st : templates) {
                final Template t = new Template(st, Template.MODE_STARTS_WITH);
                final ResourceTypeUtil.ResourceType r;
                if (st.contains("scans")) {
                    r = ResourceTypeUtil.ResourceType.SCAN;
                } else if (st.contains("assessors")) {
                    r = ResourceTypeUtil.ResourceType.ASSESSOR;
                } else if (st.contains("recons")) {
                    r = ResourceTypeUtil.ResourceType.RECON;
                } else {
                    r = ResourceTypeUtil.ResourceType.UNKNOWN;
                }
                builder.put(t, r);
            }
            this.templates = builder.build();
        }


        /**
         * The URI templates associated with type
         *
         * @return The available templates.
         */
        final List<Template> getTemplates() {
            return Lists.newArrayList(templates.keySet());
        }

        /**
         * Based on the matching template output the correct resource type
         *
         * @param matchingTemplate The template to match.
         * @return The resource type matching the submitted template.
         */
        final ResourceTypeUtil.ResourceType getResourceType(final Template matchingTemplate) {
            final ResourceTypeUtil.ResourceType r = templates.get(matchingTemplate);
            return null == r ? ResourceTypeUtil.ResourceType.UNKNOWN : r;
        }

        /**
         * Returns the filter used to determine whether to use a provided catalog entry.
         * Default implementation always passes.
         *
         * @param env  The environment object.
         * @param user The user.
         * @return The catalog filter.
         */
        CatFilterWithPath getFilter(Env env, UserI user) {
            return ResourceTypeUtil.alwaysCatWithPath;
        }

        /**
         * Retrieve the file path to the first matching file.
         * <p/>
         * Returns null if no DICOM file is found.
         *
         * @param env  The environment object.
         * @param user The user.
         * @return The file path to the first matching file.
         * @throws ClientException
         * @throws IOException
         * @throws InvalidPermissionException
         * @throws Exception
         */
        public String retrieve(final Env env, final UserI user) throws Exception {
            for (final File f : env.r.getFiles(env, user, getFilter(env, user), 1)) {
                if (null != f) {
                    return f.getAbsolutePath();
                }
            }
            return null;
        }

		public Iterable<File> retrieveAll(Env env, UserI user) throws Exception {
			final Iterable<File> matches = env.r.getFiles(env, user,getFilter(env, user), MAXFILENUMBER);
            return matches;    
        }
    }
	
}
