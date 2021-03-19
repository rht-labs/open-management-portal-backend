package com.redhat.labs.lodestar.model.filter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import com.redhat.labs.lodestar.util.ClassFieldUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListFilterOptions extends FilterOptions {

    @Parameter(name = "search", required = false, description = "search string used to query engagements.  allows =, like, not exists, exists")
    @QueryParam("search")
    private String search;

    @Parameter(name = "sortOrder", required = false, description = "response list sort order.  valid values are 'ASC' or 'DESC'")
    @QueryParam("sortOrder")
    private SortOrder sortOrder;

    @Parameter(name = "sortFields", required = false, description = "comma separated list of fields to sort on")
    @QueryParam("sortFields")
    private String sortFields;

    @Parameter(name = "limit", required = false, description = "result list will be limited to the given number.  ignored if page supplied")
    @QueryParam("limit")
    private Integer limit;

    @Parameter(name = "page", required = false, description = "page number of results to return")
    @QueryParam("page")
    private Integer page;

    @Parameter(name = "perPage", required = false, description = "number of results per page to return")
    @QueryParam("perPage")
    private Integer perPage;

    private Optional<String> suggestFieldName = Optional.empty();
    private Optional<String> unwindFieldName = Optional.empty();    
    private Optional<String> groupByFieldName = Optional.empty();

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }
   
    public Optional<SortOrder> getSortOrder() {
        return Optional.ofNullable(sortOrder);
    }

    public Optional<String> getSortFields() {
        return Optional.ofNullable(sortFields);
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    public Optional<Integer> getPage() {
        return Optional.ofNullable(page);
    }

    public Optional<Integer> getPerPage() {
        return Optional.ofNullable(perPage);
    }

    public List<String> getSortFieldsAsList() {
        String fields = getSortFields().orElse("customer_name,project_name");
        return Stream.of(fields.split(",")).map(ClassFieldUtils::snakeToCamelCase).collect(Collectors.toList());
    }

    public void addLikeSearchCriteria(String fieldName, String value) {

        StringBuilder builder = getSearch().isPresent() ? new StringBuilder(search) : new StringBuilder();

        String[] split = value.split(",");
        Stream.of(split).forEach(c -> builder.append("&").append(fieldName).append(" like ").append(value));
        String newSearch = builder.toString();
        search = (newSearch.startsWith("&")) ? newSearch.substring(1) : newSearch;

    }

}