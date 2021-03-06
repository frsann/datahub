package com.linkedin.tag.client;

import com.linkedin.common.urn.TagUrn;
import com.linkedin.data.template.StringArray;
import com.linkedin.metadata.configs.TagSearchConfig;
import com.linkedin.metadata.query.AutoCompleteResult;
import com.linkedin.metadata.query.SortCriterion;
import com.linkedin.metadata.restli.BaseSearchableClient;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.BatchGetEntityRequest;
import com.linkedin.restli.client.Client;
import com.linkedin.restli.client.GetAllRequest;
import com.linkedin.restli.client.GetRequest;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.tag.Tag;
import com.linkedin.tag.TagKey;
import com.linkedin.tag.TagsDoAutocompleteRequestBuilder;
import com.linkedin.tag.TagsFindBySearchRequestBuilder;
import com.linkedin.tag.TagsRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.linkedin.metadata.dao.utils.QueryUtils.newFilter;

public class Tags extends BaseSearchableClient<Tag>  {

    private static final TagsRequestBuilders TAGS_REQUEST_BUILDERS = new TagsRequestBuilders();
    private static final TagSearchConfig TAGS_SEARCH_CONFIG = new TagSearchConfig();

    public Tags(@Nonnull Client restliClient) {
        super(restliClient);
    }

    /**
     * Gets {@link Tag} model of the tag
     *
     * @param urn tag urn
     * @return {@link Tag} model of the tag
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Tag get(@Nonnull TagUrn urn)
            throws RemoteInvocationException {

        GetRequest<Tag> getRequest = TAGS_REQUEST_BUILDERS.get()
                .id(new ComplexResourceKey<>(toTagKey(urn), new EmptyRecord()))
                .build();

        return _client.sendRequest(getRequest).getResponse().getEntity();
    }

    /**
     * Batch gets list of {@link Tag} models of the tag
     *
     * @param urns list of tag urn
     * @return map of {@link Tag} models of the tags
     * @throws RemoteInvocationException
     */
    @Nonnull
    public Map<TagUrn, Tag> batchGet(@Nonnull Set<TagUrn> urns)
            throws RemoteInvocationException {
        BatchGetEntityRequest<ComplexResourceKey<TagKey, EmptyRecord>, Tag> batchGetRequest
                = TAGS_REQUEST_BUILDERS.batchGet()
                .ids(urns.stream().map(this::getKeyFromUrn).collect(Collectors.toSet()))
                .build();

        return _client.sendRequest(batchGetRequest).getResponseEntity().getResults()
                .entrySet().stream().collect(Collectors.toMap(
                        entry -> getUrnFromKey(entry.getKey()),
                        entry -> entry.getValue().getEntity())
                );
    }

    @Nonnull
    @Override
    public CollectionResponse<Tag> search(@Nonnull String input,
                                            @Nullable StringArray aspectNames,
                                            @Nullable Map<String, String> requestFilters,
                                            @Nullable SortCriterion sortCriterion,
                                            int start,
                                            int count)
            throws RemoteInvocationException {
        final TagsFindBySearchRequestBuilder requestBuilder = TAGS_REQUEST_BUILDERS.findBySearch()
                .aspectsParam(aspectNames)
                .inputParam(input)
                .sortParam(sortCriterion)
                .paginate(start, count);
        if (requestFilters != null) {
            requestBuilder.filterParam(newFilter(requestFilters));
        }
        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    @Nonnull
    public CollectionResponse<Tag> search(@Nonnull String input, int start, int count)
            throws RemoteInvocationException {
        return search(input, null, null, start, count);
    }

    @Nonnull
    @Override
    public AutoCompleteResult autocomplete(@Nonnull String query, @Nullable String field, @Nonnull Map<String, String> requestFilters, int limit)
            throws RemoteInvocationException {
        final String autocompleteField = (field != null) ? field : TAGS_SEARCH_CONFIG.getDefaultAutocompleteField();
        TagsDoAutocompleteRequestBuilder requestBuilder = TAGS_REQUEST_BUILDERS
                .actionAutocomplete()
                .queryParam(query)
                .fieldParam(autocompleteField)
                .filterParam(newFilter(requestFilters))
                .limitParam(limit);

        return _client.sendRequest(requestBuilder.build()).getResponse().getEntity();
    }

    /**
     * Get all {@link Tag} models of the tag
     *
     * @param start offset to start
     * @param count number of max {@link Tag}s to return
     * @return {@link Tag} models of the tag
     * @throws RemoteInvocationException
     */
    @Nonnull
    public List<Tag> getAll(int start, int count)
            throws RemoteInvocationException {
        final GetAllRequest<Tag> getAllRequest = TAGS_REQUEST_BUILDERS.getAll()
                .paginate(start, count)
                .build();
        return _client.sendRequest(getAllRequest).getResponseEntity().getElements();
    }

    /**
     * Get all {@link Tag} models of the tag
     *
     * @return {@link Tag} models of the tag
     * @throws RemoteInvocationException
     */
    @Nonnull
    public List<Tag> getAll()
            throws RemoteInvocationException {
        GetAllRequest<Tag> getAllRequest = TAGS_REQUEST_BUILDERS.getAll()
                .paginate(0, 10000)
                .build();
        return _client.sendRequest(getAllRequest).getResponseEntity().getElements();
    }

    @Nonnull
    private TagKey toTagKey(@Nonnull TagUrn urn) {
        return new TagKey().setName(urn.getName());
    }

    @Nonnull
    protected TagUrn toTagUrn(@Nonnull TagKey key) {
        return new TagUrn(key.getName());
    }

    @Nonnull
    private ComplexResourceKey<TagKey, EmptyRecord> getKeyFromUrn(@Nonnull TagUrn urn) {
        return new ComplexResourceKey<>(toTagKey(urn), new EmptyRecord());
    }

    @Nonnull
    private TagUrn getUrnFromKey(@Nonnull ComplexResourceKey<TagKey, EmptyRecord> key) {
        return toTagUrn(key.getKey());
    }
}
