package org.nrg.xnat.services.dump.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.restlet.util.Template;

public class Env {
	  Map<String, Object> attrs = new HashMap<>();
	  public DumpUtil.HeaderType h;
	  DumpUtil.ArchiveType a;
	  DumpUtil.ResourceType r;
      final String uri;
      public final Map<Integer, Set<String>> fields;

      public Env(String uri, Map<Integer, Set<String>> fields) {
          this.uri = uri;
          this.a = DumpUtil.ArchiveType.UNKNOWN;
          this.h = DumpUtil.HeaderType.UNKNOWN;
          this.r = DumpUtil.ResourceType.UNKNOWN;
          this.fields = fields;
          this.determineArchiveType();
          this.determineHeaderType();
          this.determineResourceType();
      }

      DumpUtil.ArchiveType getArchiveType() {
          return this.a;
      }

      DumpUtil.HeaderType getHeaderType() {
          return this.h;
      }

      DumpUtil.ResourceType getResourceType() {
          return this.r;
      }

      void determineArchiveType() {
          if (this.uri.startsWith("/prearchive/")) {
              this.a = DumpUtil.ArchiveType.PREARCHIVE;
          } else if (this.uri.startsWith("/archive/")) {
              this.a = DumpUtil.ArchiveType.ARCHIVE;
          } else {
              this.a = DumpUtil.ArchiveType.UNKNOWN;
          }
      }

      /**
       * If a summary is requested then the resource type defaults to SCAN.
       */
      void determineResourceType() {
          if (this.a != DumpUtil.ArchiveType.UNKNOWN && this.h != DumpUtil.HeaderType.UNKNOWN) {
              this.r = DumpUtil.ResourceType.SCAN;
          }
      }

      /**
       * Find a matching template and update the global environment
       *
       * @param _h The header type.
       */
      void visit(DumpUtil.HeaderType _h) {
          for (final Template t : _h.getTemplates()) {
              if (t.match(this.uri) != -1) {
                  t.parse(this.uri, this.attrs);
                  this.h = _h;
                  this.r = _h.getResourceType(t);
                  break;
              }
          }
      }

      void determineHeaderType() {
          for (DumpUtil.HeaderType h : DumpUtil.HeaderType.values()) {
              if (this.h == DumpUtil.HeaderType.UNKNOWN) {
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
