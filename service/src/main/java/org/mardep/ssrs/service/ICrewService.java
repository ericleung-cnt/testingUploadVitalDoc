package org.mardep.ssrs.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mardep.ssrs.domain.codetable.Crew006;
import org.mardep.ssrs.domain.codetable.CrewListCover;

public interface ICrewService extends IBaseService {

//	List<String> readEng2Excel(byte[] excel) throws InvalidFormatException, IOException;


	List<Crew006> readEng2Excel(CrewListCover entity, Map<String, List<String>> errorMsg)
			throws InvalidFormatException, IOException;


}
