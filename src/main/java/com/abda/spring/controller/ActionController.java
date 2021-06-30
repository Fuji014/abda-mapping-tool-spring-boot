package com.abda.spring.controller;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.abda.spring.domain.ActionDomain;
import com.abda.spring.service.ActionService;

// http://localhost:8080
@RestController
@CrossOrigin(origins = "http://localhost:3000/")
public class ActionController {

	@Autowired
	private ActionService actionService;

	@GetMapping("/api/v1/execute")
	public ResponseEntity<?> fetchAllTodoItems() {
		List<ActionDomain> actionDomains = actionService.fetchAllActions();
		return ResponseEntity.ok(actionDomains);
	}

	@PostMapping("/api/v1/execute")
	public void createItem(@RequestBody ActionDomain actionDomain) {
		actionService.executeActions(actionDomain);

	}

	@GetMapping("/api/v1/getFilePath")
	public ResponseEntity<String> getFilePath() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(new JFileChooser().getFileSystemView().getDefaultDirectory().toString()));
		chooser.setDialogTitle("Select Excel File");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			StringBuilder builder = new StringBuilder("{ \"path\": \"");
			builder.append(chooser.getSelectedFile().getPath().replace("\\", "\\\\"));
			builder.append("\" }");
			return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
		} else {
			throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "No Selected Directory");
		}
	}

}
