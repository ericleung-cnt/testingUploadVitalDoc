package org.mardep.ssrs.service;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author
 *
 */
public interface IFsqcService extends IBaseService{

	void send(int operation, String applNo) throws JsonProcessingException;

}
