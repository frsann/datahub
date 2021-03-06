package com.linkedin.metadata.kafka.elasticsearch;

import com.linkedin.data.template.RecordTemplate;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import com.linkedin.metadata.dao.utils.RecordUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;


public class MCEElasticEvent extends ElasticEvent {

  private final RecordTemplate _doc;

  public MCEElasticEvent(RecordTemplate doc) {
    this._doc = doc;
  }

  @Override
  @Nullable
  public XContentBuilder buildJson() {
    XContentBuilder builder = null;
    try {
      String jsonString = RecordUtils.toJsonString(this._doc);
      builder = XContentFactory.jsonBuilder().prettyPrint();
      XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, jsonString);
      builder.copyCurrentStructure(parser);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder;
  }
}