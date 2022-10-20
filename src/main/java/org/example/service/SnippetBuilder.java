package org.example.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Locale;

public class SnippetBuilder {

    private static final int SNIPPET_MAX_LENGTH = 300;
    private static final int FRAGMENT_SIZE = 5;

    public static String generateSnippet(String requestText, String content) {
        StringBuilder stringBuilder = new StringBuilder();
        String cleanContent = Jsoup.clean(content, new Safelist());
        cleanContent = cleanContent.replaceAll("[^А-я\\s]", "").toLowerCase(Locale.ROOT);
        String[] contentWords = cleanContent.split("\\s+");
        requestText = requestText.replaceAll("[^А-я\\s]", "").toLowerCase(Locale.ROOT);
        String[] requestWords = requestText.split("\\s+");

        for (String contentWord : contentWords) {
            String contentLemma = Lemmatyzer.getLemma(contentWord);
            if (contentLemma == null) {
                continue;
            }
            if (contentLemma.isEmpty()) {
                continue;
            }
            for (String requestWord : requestWords) {
                String requestLemma = Lemmatyzer.getLemma(requestWord);
                if (requestLemma == null) {
                    continue;
                }
                if (requestLemma.isEmpty()) {
                    continue;
                }
                if (contentLemma.equals(requestLemma)) {
                    StringBuilder fragment = new StringBuilder();
                    for (int i = 0; i < contentWords.length; i++) {
                        if (contentWords[i].equals(contentWord)) {
                            contentWords[i] = "<b>" + contentWord + "</b>";
                            int startFragment = Math.max(i - FRAGMENT_SIZE, 0);
                            int endFragment = Math.min(contentWords.length, i + FRAGMENT_SIZE);
                            for (int j = startFragment; j < endFragment; j++) {
                                fragment.append(contentWords[j]).append(" ");
                            }
                            if ((stringBuilder.length() + fragment.length()) < SNIPPET_MAX_LENGTH) {
                                stringBuilder.append(fragment).append("... ");
                                fragment = new StringBuilder();
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
}
