package org.example.service;

import org.example.dto.FoundedPage;
import org.example.dto.NegativeResult;
import org.example.dto.Result;
import org.example.dto.SearchResult;
import org.example.model.Lemma;
import org.example.model.Page;
import org.example.repository.IndxRepository;
import org.example.repository.LemmaRepository;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService {

    @Autowired
    IndxRepository indxRepository;
    @Autowired
    LemmaRepository lemmaRepository;
    Lemmatyzer lemmatyzer = new Lemmatyzer();

    public Result search(String query, int offset, int limit) {
        if (query.isEmpty()) {
            return new NegativeResult("Пустая строка поиска!");
        }
        List<Lemma> lemmaEntities = new ArrayList<>();
        Set<String> lemmas = lemmatyzer.getLemmas(query).keySet();
        for (String lemma : lemmas) {
            Optional<Lemma> optionalLemma = lemmaRepository.findByLemma(lemma);
            optionalLemma.ifPresent(lemmaEntities::add);
        }
        if (lemmaEntities.isEmpty()) {
            return new NegativeResult("Ничего не найдено, попробуйте изменить параметры поиска.");
        }
        lemmaEntities.sort((o1, o2) -> Integer.compare(o2.getFrequency(), o1.getFrequency()));
        List<Page> pages = lemmaEntities.get(0).getPages();
        lemmaEntities.stream().skip(1).forEach(lemma -> pages.retainAll(lemma.getPages()));
        if (pages.isEmpty()) {
            return new NegativeResult("Ничего не найдено, попробуйте изменить параметры поиска.");
        }
        List<FoundedPage> foundedPages = new ArrayList<>();
        float maxRelevance = 0.0f;
        for (Page page : pages) {
            String uri = page.getPath();
            float relevance = 0.0f;
            String title = Jsoup.parse(page.getContent()).title();
            for (Lemma lemma : lemmaEntities) {
                relevance += indxRepository.findByPageIdAndLemmaId(page.getId(), lemma.getId()).getRang();
            }
            maxRelevance = Math.max(maxRelevance, relevance);
            String snippet = SnippetBuilder.generateSnippet(query, page.getContent());
            FoundedPage foundedPage = new FoundedPage(page.getSite().getUrl(), page.getSite().getName(), uri, title, snippet, relevance);
            foundedPages.add(foundedPage);
        }
        for (FoundedPage foundedPage : foundedPages) {
            foundedPage.setRelevance(foundedPage.getRelevance() / maxRelevance);
        }
        foundedPages.sort(((o1, o2) -> Float.compare(o1.getRelevance(), o2.getRelevance())));
        Collections.reverse(foundedPages);
        List<FoundedPage> data = foundedPages.stream().skip(offset).limit(limit).toList();
        SearchResult searchResult = new SearchResult();
        searchResult.setCount(foundedPages.size());
        searchResult.setData(data);
        return searchResult;
    }


}
