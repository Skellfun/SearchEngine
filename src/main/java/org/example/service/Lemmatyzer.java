package org.example.service;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemmatyzer {

    private static final List<String> unusableForms = Arrays.asList("СОЮЗ", "МЕЖД", "ПРЕДЛ", "ЧАСТ", "МС-П");
    private static LuceneMorphology luceneMorph;

    private static boolean checkPartOfSpeech(String word) {
        word = luceneMorph.getMorphInfo(word).get(0);
        for (String form : unusableForms) {
            if (word.contains(form)) {
                return true;
            }
        }
        return false;
    }

    public static String getLemma(String word) {
        getMorphology();
        String lemma = "";
        if (word.length() < 3) {
            return null;
        }
        if (!luceneMorph.checkString(word)) {
            return null;
        }
        if (checkPartOfSpeech(word)) {
            return null;
        }
        lemma = luceneMorph.getNormalForms(word).get(0);
        return lemma;
    }

    private static LuceneMorphology getMorphology() {
        if (luceneMorph == null) {
            try {
                luceneMorph = new RussianLuceneMorphology();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return luceneMorph;
    }

    public Map<String, Integer> getLemmas(String content) {
        Map<String, Integer> lemmas = new HashMap<>();
        getMorphology();
        content = content.toLowerCase(Locale.ROOT).replaceAll("[^А-я\\s]", " ").replaceAll("\\s{2,}", " ").trim();
        String[] words = content.split(" ");
        for (String word : words) {
            if (word.length() < 3) {
                continue;
            }
            if (!luceneMorph.checkString(word)) {
                continue;
            }
            if (checkPartOfSpeech(word)) {
                continue;
            }
            String normalForm = luceneMorph.getNormalForms(word).get(0);
            if (!lemmas.containsKey(normalForm)) {
                lemmas.put(normalForm, 1);
            } else {
                int value = lemmas.get(normalForm);
                lemmas.put(normalForm, value + 1);
            }
        }
        return lemmas;
    }
}
