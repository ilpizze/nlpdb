package it.antonio.textprocessing.pos.hmm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class HMMPosTrainData {
	public String[] words;
	public String[] tags;

	public HMMPosTrainData(String[] words, String[] tags) {
		super();
		this.words = words;
		this.tags = tags;
	}

	public static List<HMMPosTrainData> parse(InputStream stream)  {
		List<HMMPosTrainData> trainingData = new LinkedList<>();

		BufferedReader wordFile = new BufferedReader(new InputStreamReader(stream));

		String line = "START";
		while (line != null) {
			try {
				line = wordFile.readLine();
			} catch (IOException e) {
				throw new IllegalStateException("Cannor read line", e);
			}
			if (line == null || !line.contains("_"))
				continue;

			String[] wordsAndTags = line.split(" ");

			String[] words = new String[wordsAndTags.length];
			String[] tags = new String[wordsAndTags.length];

			for (int i = 0; i < wordsAndTags.length; i++) {
				String[] wordAndTag = wordsAndTags[i].split("_");
				words[i] = wordAndTag[0].toLowerCase();
				tags[i] = wordAndTag[1];

			}

			trainingData.add(new HMMPosTrainData(words, tags));

		}

		return trainingData;

	}

}
