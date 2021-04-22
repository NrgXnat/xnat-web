package org.nrg.xapi.rest.dicomweb.mediator;

import org.nrg.xapi.rest.dicomweb.search.SearchException;
import org.nrg.xdat.om.XnatImagesessiondata;

import java.util.*;
import java.util.stream.Collectors;

public class BaseMediator implements Mediator {

    @Override
    public Optional<List<Conflict>> getConflicts( List<XnatImagesessiondata> sessions) {

        Map<String, List<XnatImagesessiondata>> studyUIDPartition = sessions.stream()
                .collect(Collectors.groupingBy(XnatImagesessiondata::getUid));

        boolean atLeastOneStudyInstanceUIDInMultipleProjects = false;
        for (String suid : studyUIDPartition.keySet()) {
            Map<String, List<XnatImagesessiondata>> projectPartition = studyUIDPartition.get(suid).stream()
                    .collect(Collectors.groupingBy(XnatImagesessiondata::getProject));
            if (projectPartition.keySet().size() > 1) {
                atLeastOneStudyInstanceUIDInMultipleProjects = true;
                break;
            }
        }
        if (atLeastOneStudyInstanceUIDInMultipleProjects) {
            List<Conflict> conflicts = new ArrayList<>();
            conflicts.add( new BaseConflict( SearchException.Type.STUDY_INSTANCE_UID_CONFLICT, Arrays.asList("This query matches study-instance UID in multiple projects.")));
            return Optional.of( conflicts);
        }

        return Optional.ofNullable(null);
    }
}
