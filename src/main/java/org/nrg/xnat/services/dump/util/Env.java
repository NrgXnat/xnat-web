package org.nrg.xnat.services.dump.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.springframework.web.util.UriTemplate;

public class Env {
	  Map<String, String> attrs = new HashMap<>();
	  public HeaderTypeUtil.HeaderType h;
	  ArchiveTypeUtil.ArchiveType a;
	  ResourceTypeUtil.ResourceType r;
      final String uri;
      public final Map<Integer, Set<String>> fields;

      public Env(String uri, Map<Integer, Set<String>> fields) {
          this.uri = uri;
          this.a = ArchiveTypeUtil.ArchiveType.UNKNOWN;
          this.h = HeaderTypeUtil.HeaderType.UNKNOWN;
          this.r = ResourceTypeUtil.ResourceType.UNKNOWN;
          this.fields = fields;
          this.determineArchiveType();
          this.determineHeaderType();
          this.determineResourceType();
      }

      ArchiveTypeUtil.ArchiveType getArchiveType() {
          return this.a;
      }

      HeaderTypeUtil.HeaderType getHeaderType() {
          return this.h;
      }

      ResourceTypeUtil.ResourceType getResourceType() {
          return this.r;
      }

      void determineArchiveType() {
          if (this.uri.startsWith("/prearchive/")) {
              this.a = ArchiveTypeUtil.ArchiveType.PREARCHIVE;
          } else if (this.uri.startsWith("/archive/")) {
              this.a = ArchiveTypeUtil.ArchiveType.ARCHIVE;
          } else {
              this.a = ArchiveTypeUtil.ArchiveType.UNKNOWN;
          }
      }

      /**
       * If a summary is requested then the resource type defaults to SCAN.
       */
      void determineResourceType() {
          if (this.a != ArchiveTypeUtil.ArchiveType.UNKNOWN && this.h != HeaderTypeUtil.HeaderType.UNKNOWN) {
              this.r = ResourceTypeUtil.ResourceType.SCAN;
          }
      }

      /**
       * Find a matching template and update the global environment
       *
       * @param _h The header type.
       */
//      void visit(HeaderTypeUtil.HeaderType _h) {
//          for (final Template t : _h.getTemplates()) {
//              if (t.match(this.uri) != -1) {
//                  t.parse(this.uri, this.attrs);
//                  this.h = _h;
//                  this.r = _h.getResourceType(t);
//                  break;
//              }
//          }
//      }
      
      void visit(HeaderTypeUtil.HeaderType _h) {
          for (final UriTemplate t : _h.getTemplates()) {
        	  boolean match = t.matches(uri);
              if (match) {
            	  this.attrs = t.match(uri);
                  this.h = _h;
                  this.r = _h.getResourceType(t);
                  break;
              }
          }
      }

      void determineHeaderType() {
          for (HeaderTypeUtil.HeaderType h : HeaderTypeUtil.HeaderType.values()) {
              if (this.h == HeaderTypeUtil.HeaderType.UNKNOWN) {
                  this.visit(h);
              }
          }
      }

      @Nullable
      String getProject(Env env) {
          final Object proj = env.attrs.get("PROJECT_ID");
          return proj != null ? (String) proj : null;
      }
}
