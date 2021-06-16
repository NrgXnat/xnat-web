package org.nrg.xnat.dto.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileSet {
	final File parent;
	final List<File> matches= new ArrayList<>();
	public FileSet(File p){
		parent=p;
	}
	public List<File> getMatches() {
		return matches;
	}
	
	public void add(File f){
		matches.add(f);
	}
	
	public void addAll(Collection<File> files){
		matches.addAll(files);
	}
	
	public File getParent(){
		return parent;
	}
}
