package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArchivableItem;

public interface XftDataObjectService<T> {

	public T create(UserI user, T item);

	public List<T> getAll(UserI user);

	public T get(UserI user, int itemId);

	public T findById(UserI user, String itemId);
}
