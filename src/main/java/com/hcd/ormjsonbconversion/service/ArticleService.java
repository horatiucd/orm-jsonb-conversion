package com.hcd.ormjsonbconversion.service;

import com.asentinel.common.orm.OrmOperations;
import com.hcd.ormjsonbconversion.model.Article;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArticleService {

    private final OrmOperations orm;

    public ArticleService(OrmOperations orm) {
        this.orm = orm;
    }

    @Transactional
    public void write(Article article) {
        orm.update(article);
    }

    @Transactional
    public Optional<Article> readByCode(String code) {
        return orm.newSqlBuilder(Article.class)
                .select()
                .where().column("code").eq(code)
                .execForOptional();
    }
}
