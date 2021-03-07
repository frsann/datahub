package com.linkedin.metadata.builders.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.linkedin.common.urn.DataJobUrn;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.metadata.builders.graph.relationship.BaseRelationshipBuilder;
import com.linkedin.metadata.builders.graph.relationship.OwnedByBuilderFromOwnership;
import com.linkedin.metadata.builders.graph.relationship.ConsumesBuilderFromDataJobInputOutput;
import com.linkedin.metadata.builders.graph.relationship.DownstreamOfBuilderFromDataJobInputOutput;
import com.linkedin.metadata.builders.graph.relationship.ProducesBuilderFromDataJobInputOutput;

import com.linkedin.metadata.entity.DataJobEntity;
import com.linkedin.metadata.snapshot.DataJobSnapshot;


// TODO: how to create the IsPartOf relationship to DataFlow?

public class DataJobGraphBuilder extends BaseGraphBuilder<DataJobSnapshot>  {
    private static final Set<BaseRelationshipBuilder> RELATIONSHIP_BUILDERS =
        Collections.unmodifiableSet(new HashSet<BaseRelationshipBuilder>() {
            {
                add(new OwnedByBuilderFromOwnership());
                add(new ConsumesBuilderFromDataJobInputOutput());
                add(new DownstreamOfBuilderFromDataJobInputOutput());
                add(new ProducesBuilderFromDataJobInputOutput());
            }
        });

    public DataJobGraphBuilder() {
        super(DataJobSnapshot.class, RELATIONSHIP_BUILDERS);
    }

    @Nonnull
    @Override
    protected List<? extends RecordTemplate> buildEntities(@Nonnull DataJobSnapshot snapshot) {
        final DataJobUrn urn = snapshot.getUrn();
        final DataJobEntity entity = new DataJobEntity().setUrn(urn)
            .setFlow(urn.getFlowEntity())
            .setJobId(urn.getJobIdEntity());

        setRemovedProperty(snapshot, entity);

        return Collections.singletonList(entity);
    }
}
