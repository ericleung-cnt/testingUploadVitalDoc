package org.mardep.ssrs.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;

public interface ICrewService extends IBaseService {


	List<Crew> readEng2Excel(Crew entity, List<String> errorMsg)
			throws InvalidFormatException, IOException;



}
