package com.hcd.ormjsonbconversion.service;

import com.hcd.ormjsonbconversion.model.Article;
import com.hcd.ormjsonbconversion.model.Attributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    void manageArticles() {
        var code = UUID.randomUUID().toString();
        var attributes = new Attributes("Technically-correct Article", "Horatiu Dan", 1200);
        var article = new Article(code, attributes);
        articleService.write(article);

        Optional<Article> read = articleService.readByCode(code);
        Assertions.assertTrue(read.isPresent());

        final Article readArticle = read.get();
        Assertions.assertEquals(code, readArticle.getCode());

        final Attributes readAttributes = readArticle.getAttributes();
        Assertions.assertEquals(attributes.getTitle(), readAttributes.getTitle());
        Assertions.assertEquals(attributes.getAuthor(), readAttributes.getAuthor());
        Assertions.assertEquals(attributes.getWords(), readAttributes.getWords());
    }
}
