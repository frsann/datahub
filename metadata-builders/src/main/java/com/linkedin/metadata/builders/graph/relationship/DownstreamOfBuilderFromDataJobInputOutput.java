package com.linkedin.metadata.builders.graph.relationship;

import com.linkedin.datajob.DataJobInputOutput;
import com.linkedin.common.urn.Urn;
import com.linkedin.metadata.builders.graph.GraphBuilder;
import com.linkedin.metadata.relationship.DownstreamOf;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import static com.linkedin.metadata.dao.internal.BaseGraphWriterDAO.RemovalOption.*;


public class DownstreamOfBuilderFromDataJobInputOutput extends BaseRelationshipBuilder<DataJobInputOutput> {

  private static final String CORPUSER_URN_TYPE = "corpuser";

  public DownstreamOfBuilderFromDataJobInputOutput() {
    super(DataJobInputOutput.class);
  }

  @Nonnull
  @Override
  public List<GraphBuilder.RelationshipUpdates> buildRelationships(@Nonnull Urn urn, @Nonnull DataJobInputOutput inputOutput) {
    final List<DownstreamOf> downstreamEdges = inputOutput.getInputDatasets()
        .stream()
        .flatMap(upstreamDataset -> inputOutput.getOutputDatasets().stream()
        .map(downstreamDataset -> new DownstreamOf().setSource(downstreamDataset).setDestination(upstreamDataset)))
        .collect(Collectors.toList());

    return Collections.singletonList(new GraphBuilder.RelationshipUpdates(downstreamEdges, REMOVE_ALL_EDGES_FROM_SOURCE));
  }
}
