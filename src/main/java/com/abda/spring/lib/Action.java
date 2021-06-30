package com.abda.spring.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.core.io.ClassPathResource;

public class Action {
	public String transformText(String str) {
		if (str == "") {
			return str;
		} else {
			return str.toUpperCase();
		}

	}

	public int toNumber(String name) {
		int number = 0;
		for (int i = 0; i < name.length(); i++) {
			number = number * 26 + (name.charAt(i) - ('A' - 1));
		}
		return number;
	}

	public String toName(int number) {
		StringBuilder sb = new StringBuilder();
		while (number-- > 0) {
			sb.append((char) ('A' + (number % 26)));
			number /= 26;
		}
		return sb.reverse().toString();
	}

	public String readFileToString(String path) throws IOException {
		StringBuilder resultBuilder = new StringBuilder("");
		ClassPathResource resource = new ClassPathResource(path);

		try (InputStream inputStream = resource.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				resultBuilder.append(line);
			}

		}

		return resultBuilder.toString();
	}

}
