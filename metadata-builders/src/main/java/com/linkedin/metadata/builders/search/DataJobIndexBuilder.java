package com.linkedin.metadata.builders.search;

import com.linkedin.common.Ownership;
import com.linkedin.common.urn.DataJobUrn;
import com.linkedin.datajob.DataJobInfo;
import com.linkedin.datajob.DataJobInputOutput;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.StringArray;
import com.linkedin.metadata.search.DataFlowDocument;
import com.linkedin.metadata.snapshot.DataFlowSnapshot;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class DataJobIndexBuilder extends BaseIndexBuilder<DataFlowDocument> {
  public DataJobIndexBuilder() {
    super(Collections.singletonList(DataJobSnapshot.class), DataJobDocument.class);
  }

  @Nonnull
  private static String buildBrowsePath(@Nonnull DataJobUrn urn) {
    return ("/" + urn.getDataFlowEntity().getDataFlowId() + "/"  + urn.getJobId()).toLowerCase();
  }


  // TODO: figure this out!
  @Nonnull
  private static DataJobDocument setUrnDerivedFields(@Nonnull DataJobUrn urn) {
    return new DataJobDocument()
        .setUrn(urn)
        .setDataFlow(urn.getDataFlowEntity().getDataFlowId())
        .setJobId(urn.getJobIdEntity())
        .setBrowsePaths(new StringArray(Collections.singletonList(buildBrowsePath(urn))));
  }

  @Nonnull
  private DataJobDocument getDocumentToUpdateFromAspect(@Nonnull DataJobUrn urn,
      @Nonnull DataJobInfo info) {
    final DataJobDocument document = setUrnDerivedFields(urn);
    document.setName(info.getName());
    if (info.getDescription() != null) {
        document.setDescription(info.getDescription());
    }
    if (info.getType() != null) {
      document.setType(info.getType());
    }
    return document;
  }

  @Nonnull
  private DataJobDocument getDocumentToUpdateFromAspect(@Nonnull DataJobUrn urn,
      @Nonnull Ownership ownership) {
    return setUrnDerivedFields(urn)
        .setOwners(BuilderUtils.getCorpUserOwners(ownership));  // TODO: should be optional?
  }

  @Nonnull
  private List<DataJonDocument> getDocumentsToUpdateFromSnapshotType(@Nonnull DataJobSnapshot snapshot) {
    DataJobUrn urn = snapshot.getUrn();
    return snapshot.getAspects().stream().map(aspect -> {
      if (aspect.isDataJobInfo()) {
        return getDocumentToUpdateFromAspect(urn, aspect.getDataJobInfo());
      } else if (aspect.isOwnership()) {
        return getDocumentToUpdateFromAspect(urn, aspect.getOwnership());
      }
      return null;
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public List<DataJobDocument> getDocumentsToUpdate(@Nonnull RecordTemplate snapshot) {
    if (snapshot instanceof DataJobSnapshot) {
      return getDocumentsToUpdateFromSnapshotType((DataJobSnapshot) snapshot);
    }
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Class<DataJobDocument> getDocumentType() {
    return DataJobDocument.class;
  }
}
