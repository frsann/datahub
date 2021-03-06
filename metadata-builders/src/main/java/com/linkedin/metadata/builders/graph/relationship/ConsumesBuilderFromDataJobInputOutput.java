package com.linkedin.metadata.builders.graph.relationship;

import com.linkedin.common.DataJobInputOutput;
import com.linkedin.common.urn.Urn;
import com.linkedin.metadata.builders.graph.GraphBuilder;
import com.linkedin.metadata.relationship.Consumes;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import static com.linkedin.metadata.dao.internal.BaseGraphWriterDAO.RemovalOption.*;


public class ConsumesBuilderFromDataJobInputOutput extends BaseRelationshipBuilder<DataJobInputOutput> {

  private static final String CORPUSER_URN_TYPE = "corpuser";

  public ConsumesBuilderFromDataJobInputOutput() {
    super(DataJobInputOutput.class);
  }

  @Nonnull
  @Override
  public List<GraphBuilder.RelationshipUpdates> buildRelationships(@Nonnull Urn urn, @Nonnull DataJobInputOutput inputOutput) {
    final List<Consumes> inputsList = inputOutput.getInputDatasets()
        .stream()
        .map(inputDataset -> new Consumes().setSource(inputDatast).setDestination(urn)
        .collect(Collectors.toList());


    return Collections.singletonList(new GraphBuilder.RelationshipUpdates(inputsList, REMOVE_ALL_EDGES_FROM_SOURCE));
  }
}
