package com.abda.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abda.spring.domain.ActionDomain;
import com.abda.spring.repository.ActionRepository;

@Service
public class ActionService {

	@Autowired
	private ActionRepository actionRepository;

	public List<ActionDomain> fetchAllActions() {
		return actionRepository.fetchAllActions();
	}

	public void executeActions(ActionDomain actionDomain) {
		actionRepository.execute(actionDomain);
	}

}
