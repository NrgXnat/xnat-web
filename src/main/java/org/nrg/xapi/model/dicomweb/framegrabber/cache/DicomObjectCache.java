package org.nrg.xapi.model.dicomweb.framegrabber.cache;

import org.nrg.xapi.model.dicomweb.DicomObjectI;
import org.nrg.xapi.model.dicomweb.TransCoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DicomObjectCache {

    private Map<DicomObjectKey, DicomObjectI> dicomObjectCache;
    private TransCoder transCoder;

    public DicomObjectCache( TransCoder transCoder) {
        this.transCoder = transCoder;
        dicomObjectCache = new HashMap<>();
    }

    public DicomObjectI getDicomObject( DicomObjectI dobj) {
        DicomObjectKey dicomObjectKey = new DicomObjectKey( dobj);
        if( dicomObjectCache.containsKey( dicomObjectKey)) {
            return dicomObjectCache.get( dicomObjectKey);
        }
        else {
            DicomObjectI newDicomObject = transCoder.transcode( dobj, EVLE);
            dicomObjectCache.clear();
            dicomObjectCache.put( dicomObjectKey, newDicomObject);
            return newDicomObject;
        }
    }

    private class DicomObjectKey {
        String studyInstanceUID;
        String seriesInstanceUID;
        String sopInstanceUID;
        public DicomObjectKey( DicomObjectI dobj) {
            this.studyInstanceUID = dobj.getStudyInstanceUID();
            this.seriesInstanceUID = dobj.getSeriesInstanceUID();
            this.sopInstanceUID = dobj.getSOPInstanceUID();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DicomObjectKey that = (DicomObjectKey) o;
            return Objects.equals(studyInstanceUID, that.studyInstanceUID) && Objects.equals(seriesInstanceUID, that.seriesInstanceUID) && Objects.equals(sopInstanceUID, that.sopInstanceUID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(studyInstanceUID, seriesInstanceUID, sopInstanceUID);
        }
    }

    private final String EVLE = "1.2.840.10008.1.2.1";

}
