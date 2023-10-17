package com.marklogic.client;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileReader;

/**
 * Expects "diff.txt" to capture modified files.
 */
public class CopyrightModifier {

	public static void main(String[] args) throws Exception {
		final String copyrightYear = "2022 MarkLogic Corporation";
		File file = new File("diff.txt");
		String contents = FileCopyUtils.copyToString(new FileReader(file));
		for (String filename : contents.split("\n")) {
			String content = FileCopyUtils.copyToString(new FileReader(filename));
			if (content.contains(copyrightYear)) {
				content = content.replaceAll(copyrightYear, "2023 MarkLogic Corporation");
				System.out.println("Modifying: " + filename);
				FileCopyUtils.copy(content.getBytes(), new File(filename));
			}
		}
	}
}
