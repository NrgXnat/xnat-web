package org.nrg.xnat.services.resources.prearchive;

public interface PrearchiveBatchMoveService {

	public void handlePost();

	public void handleParam(final String key, final Object value);

}
