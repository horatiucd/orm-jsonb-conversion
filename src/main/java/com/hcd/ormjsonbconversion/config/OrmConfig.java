package com.hcd.ormjsonbconversion.config;

import com.asentinel.common.jdbc.SqlQuery;
import com.asentinel.common.jdbc.SqlQueryTemplate;
import com.asentinel.common.jdbc.flavors.JdbcFlavor;
import com.asentinel.common.jdbc.flavors.postgres.PgEchoingJdbcTemplate;
import com.asentinel.common.jdbc.flavors.postgres.PostgresJdbcFlavor;
import com.asentinel.common.orm.OrmOperations;
import com.asentinel.common.orm.OrmTemplate;
import com.asentinel.common.orm.ed.tree.DefaultEntityDescriptorTreeRepository;
import com.asentinel.common.orm.ed.tree.EntityDescriptorTreeRepository;
import com.asentinel.common.orm.jql.DefaultSqlBuilderFactory;
import com.asentinel.common.orm.jql.SqlBuilderFactory;
import com.asentinel.common.orm.mappers.Column;
import com.asentinel.common.orm.mappers.SqlParameterTypeDescriptor;
import com.asentinel.common.orm.persist.SimpleUpdater;
import com.asentinel.common.orm.query.DefaultSqlFactory;
import com.asentinel.common.orm.query.SqlFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.jdbc.core.JdbcOperations;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Set;

@Configuration
public class OrmConfig {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    @Bean
    public JdbcFlavor jdbcFlavor() {
        return new PostgresJdbcFlavor();
    }

    @Bean
    public JdbcOperations jdbcOperations(DataSource dataSource,
                                         JdbcFlavor jdbcFlavor) {
        PgEchoingJdbcTemplate template = new PgEchoingJdbcTemplate(dataSource);
        template.setJdbcFlavor(jdbcFlavor);
        return template;
    }

    @Bean
    public SqlQuery sqlQuery(JdbcFlavor jdbcFlavor,
                             JdbcOperations jdbcOps) {
        return new SqlQueryTemplate(jdbcFlavor, jdbcOps);
    }

    @Bean
    public SqlFactory sqlFactory(JdbcFlavor jdbcFlavor) {
        return new DefaultSqlFactory(jdbcFlavor);
    }

    @Bean
    public DefaultEntityDescriptorTreeRepository entityDescriptorTreeRepository(SqlBuilderFactory sqlBuilderFactory,
                                                                                @Qualifier("ormConversionService") ConversionService conversionService) {
        DefaultEntityDescriptorTreeRepository treeRepository = new DefaultEntityDescriptorTreeRepository();
        treeRepository.setSqlBuilderFactory(sqlBuilderFactory);
        treeRepository.setConversionService(conversionService);
        return treeRepository;
    }

    @Bean
    public DefaultSqlBuilderFactory sqlBuilderFactory(@Lazy EntityDescriptorTreeRepository entityDescriptorTreeRepository,
                                                      SqlFactory sqlFactory,
                                                      SqlQuery sqlQuery) {
        DefaultSqlBuilderFactory sqlBuilderFactory = new DefaultSqlBuilderFactory(sqlFactory, sqlQuery);
        sqlBuilderFactory.setEntityDescriptorTreeRepository(entityDescriptorTreeRepository);
        return sqlBuilderFactory;
    }

    @Bean
    public OrmOperations orm(JdbcFlavor jdbcFlavor,
                             SqlQuery sqlQuery,
                             SqlBuilderFactory sqlBuilderFactory,
                             @Qualifier("ormConversionService") ConversionService conversionService) {
        SimpleUpdater updater = new SimpleUpdater(jdbcFlavor, sqlQuery);
        updater.setConversionService(conversionService);
        return new OrmTemplate(sqlBuilderFactory, updater);
    }

    @Bean("ormConversionService")
    public ConversionService ormConversionService() {
        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(new JsonToObjectConverter());
        conversionService.addConverter(new ObjectToJsonConverter());
        return conversionService;
    }

    private static class JsonToObjectConverter implements ConditionalGenericConverter {

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            PGobject pgObj = (PGobject) source;
            try {
                return MAPPER.readValue(pgObj.getValue(), targetType.getType());
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Failed to convert from JSON.", e);
            }
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (!(sourceType.getType() == PGobject.class)) {
                return false;
            }
            Column column = targetType.getAnnotation(Column.class);
            if (column == null) {
                return false;
            }
            return "jsonb".equals(column.sqlParam().value());
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return null;
        }
    }

    private static class ObjectToJsonConverter implements ConditionalGenericConverter {

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String s;
            try {
                s = MAPPER.writeValueAsString(source);
                PGobject pgo = new PGobject();
                pgo.setType("jsonb");
                pgo.setValue(s);
                return pgo;
            } catch (JsonProcessingException | SQLException e) {
                throw new IllegalArgumentException("Failed to convert to JSON.", e);
            }
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (!(targetType instanceof SqlParameterTypeDescriptor)) {
                return false;
            }

            SqlParameterTypeDescriptor typeDescriptor = (SqlParameterTypeDescriptor) targetType;
            return "jsonb".equals(typeDescriptor.getTypeName());
        }

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return null;
        }
    }
}
