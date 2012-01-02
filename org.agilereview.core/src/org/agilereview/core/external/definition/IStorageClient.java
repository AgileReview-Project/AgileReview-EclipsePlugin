package org.agilereview.core.external.definition;

public interface IStorageClient {
	
	public String[] getAffectedFiles(String[] paths);
	public String getXmlText(String authorFilePath);
	
}
