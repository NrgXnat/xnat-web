package org.nrg.xapi.model.dicomweb;

import java.io.File;
import java.io.IOException;

public interface DicomObjectFactory {

    DicomImageObject createDicomObject(File f, boolean isTemporary) throws IOException;

    DicomImageObject createDicomObjectQuiet(File f, boolean isTemporary);

    QIDOResponseStudy createQIDOResponseStudy();
    QIDOResponseSeries createQIDOResponseSeries();
    QIDOResponseStudySeries createQIDOResponseStudySeries();
    QIDOResponseInstance createQIDOResponseInstance();

}
