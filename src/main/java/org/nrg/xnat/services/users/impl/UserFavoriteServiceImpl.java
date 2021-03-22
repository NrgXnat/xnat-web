package org.nrg.xnat.services.users.impl;

import java.sql.SQLException;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.FavEntries;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

	@Override
	public void findUserFavoritesByDataType(UserI user, String dataType) {
		XFTTable table = null;
		if (dataType != null) {
			try {
				table = FavEntries.GetFavoriteEntries(dataType, user);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (DBPoolException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void findUserFavoritesByDataTypeAndProjectId(UserI user, String dataType, String projectId) {
		findUserFavoritesByDataType(user, dataType);
	}

	@Override
	public void delete(UserI user, String dataType, String projectId) throws NotFoundException {
		if (projectId == null || dataType == null || user == null) {
			throw new NotFoundException("");
		} else {
			try {
				FavEntries favEntry = FavEntries.GetFavoriteEntries(dataType, projectId, user);
				favEntry.delete();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(UserI user, String dataType, String projectId) throws NotFoundException {
		if (projectId == null || dataType == null) {
			throw new NotFoundException("");
		} else {
			try {
				FavEntries favEntry = new FavEntries();
				favEntry.setId(projectId);
				favEntry.setDataType(dataType);
				favEntry.setUser(user);
				favEntry.save();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
