package no.acat.controller;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import static no.acat.controller.ESQueryUtil.isNationalComponentQuery;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ApiSearchESQueryBuilder {
    private static Logger logger = LoggerFactory.getLogger(ApiSearchESQueryBuilder.class);

    private BoolQueryBuilder composedQuery;

    public ApiSearchESQueryBuilder() {
        // Default query is to match all. Additional .must clauses will narrow it down.
        composedQuery = QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery());
    }

    public BoolQueryBuilder build() {
        return composedQuery;
    }

    public ApiSearchESQueryBuilder boostNationalComponents() {
        // Increase score of national components in all queries
        // in api-cat, we use modern notation nationalComponent=true, while in dataset is not as explicit
        composedQuery.should(isNationalComponentQuery().boost(2));
        return this;
    }

    public ApiSearchESQueryBuilder addParams(Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String filterName = entry.getKey();
            String filterValue = entry.getValue();

            if (isEmpty(filterValue)) continue; //skip filters with empty values

            Method[] methods = ApiSearchParamHandlers.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(filterName)) {
                    try {
                        QueryBuilder filter = (QueryBuilder) (method.invoke(null, new Object[]{filterValue, this}));
                        // Difference between .must() and .filter() is that must keeps scores, while filter does not.
                        // We use .must() because of "q"-filter assigns scores.
                        // For other parameters, scores are irrelevant and therefore can be included safely.
                        if (filter != null) {
                            composedQuery.must(filter);
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Filter invocation error:" + filterName);
                    }
                }
            }
        }
        return this;
    }
}
