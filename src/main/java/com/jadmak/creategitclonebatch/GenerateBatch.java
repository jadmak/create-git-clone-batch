package com.jadmak.creategitclonebatch;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GenerateBatch implements CommandLineRunner {

	@Autowired
	RestTemplate client;

	@Autowired
	ObjectMapper mp;

	public class ATypeReference extends TypeReference<List<Object>> {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args) throws Exception {

		StringBuilder sb = new StringBuilder();
		int page = 1;

		while (true) {
			String json = client.getForObject(
					String.format("https://api.github.com/users/jadmak/repos?page=%d&per_page=100", page),
					String.class);

			List<Object> list = mp.readValue(json, new ATypeReference());

			if (list.size() == 0) {
				break;
			}

			for (Object projecto : list) {
				if (projecto instanceof Map<?, ?>) {
					Map<String, String> project = (Map<String, String>) projecto;
					sb.append("git clone ");
					sb.append(project.get("clone_url"));
					sb.append("\n");
				}
			}

			if (list.size() < 100) {
				break;
			}
			page++;

		}

		Files.write(Paths.get("clone.bat"), sb.toString().getBytes(), StandardOpenOption.CREATE);
		System.out.print("File Created at " + Paths.get("clone.bat").toAbsolutePath().toString());
	
	}

}
