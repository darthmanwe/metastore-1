package io.anemos.metastore.core.proto.validation;

import static io.anemos.metastore.v1alpha1.FieldChangeInfo.FieldType.FIELD_TYPE_STRING;
import static io.anemos.metastore.v1alpha1.FieldChangeInfo.FieldType.FIELD_TYPE_UNSET;

import io.anemos.metastore.core.proto.TestSets;
import io.anemos.metastore.core.proto.validate.ProtoDiff;
import io.anemos.metastore.core.proto.validate.ValidationResults;
import io.anemos.metastore.putils.ProtoDomain;
import io.anemos.metastore.v1alpha1.*;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class DiffTest {

  @Test
  public void onBaseDeprecatedString() throws IOException {
    FieldResult fieldResults = diff(TestSets.base(), TestSets.baseDeprecateString());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.CHANGED, change.getChangeType());
    Assert.assertEquals("", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("", change.getToName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getToType());
    Assert.assertEquals(true, change.getToDeprecated());
  }

  @Test
  public void onBaseRemoveString() throws IOException {
    FieldResult fieldResults = diff(TestSets.base(), TestSets.baseRemoveString());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.REMOVAL, change.getChangeType());
    Assert.assertEquals("primitive_string", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("", change.getToName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void onBaseReserveString() throws IOException {
    FieldResult fieldResults = diff(TestSets.base(), TestSets.baseReserveString());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.RESERVED, change.getChangeType());
    Assert.assertEquals("primitive_string", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("primitive_string", change.getToName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void onBaseReserveStringOnlyNumber() throws IOException {
    FieldResult fieldResults = diff(TestSets.base(), TestSets.baseReserveStringOnlyNumber());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.RESERVED, change.getChangeType());
    Assert.assertEquals("primitive_string", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("", change.getToName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void toBaseUndeprecatedString() throws IOException {
    FieldResult fieldResults = diff(TestSets.baseDeprecateString(), TestSets.base());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.CHANGED, change.getChangeType());
    Assert.assertEquals("", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getFromType());
    Assert.assertEquals(true, change.getFromDeprecated());
    Assert.assertEquals("", change.getToName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void toBaseUnremoveString() throws IOException {
    FieldResult fieldResults = diff(TestSets.baseRemoveString(), TestSets.base());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.ADDITION, change.getChangeType());
    Assert.assertEquals("", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("primitive_string", change.getToName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void tnBaseUnreserveString() throws IOException {
    FieldResult fieldResults = diff(TestSets.baseReserveString(), TestSets.base());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.UNRESERVED, change.getChangeType());
    Assert.assertEquals("primitive_string", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("primitive_string", change.getToName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void toBaseUnreserveStringOnlyNumber() throws IOException {
    FieldResult fieldResults = diff(TestSets.baseReserveStringOnlyNumber(), TestSets.base());

    Assert.assertEquals(16, fieldResults.getNumber());
    Assert.assertEquals("primitive_string", fieldResults.getName());

    FieldChangeInfo change = fieldResults.getChange();

    Assert.assertEquals(ChangeType.UNRESERVED, change.getChangeType());
    Assert.assertEquals("", change.getFromName());
    Assert.assertEquals(FIELD_TYPE_UNSET, change.getFromType());
    Assert.assertEquals(false, change.getFromDeprecated());
    Assert.assertEquals("primitive_string", change.getToName());
    Assert.assertEquals(FIELD_TYPE_STRING, change.getToType());
    Assert.assertEquals(false, change.getToDeprecated());
  }

  @Test
  public void toBaseExtraFileAdded() throws IOException {
    Report report = diffOnPackage(TestSets.base(), TestSets.baseExtraFile());

    Assert.assertEquals(1, report.getFileResultsCount());
    Assert.assertEquals(
        ChangeType.ADDITION,
        report.getFileResultsOrThrow("test/v1/extra.proto").getChange().getChangeType());

    Assert.assertEquals(1, report.getMessageResultsCount());
    Assert.assertEquals(
        ChangeType.ADDITION,
        report.getMessageResultsOrThrow("test.v1.ProtoExtraMessage").getChange().getChangeType());
  }

  @Test
  public void toBaseExtraFileRemoved() throws IOException {
    Report report = diffOnPackage(TestSets.baseExtraFile(), TestSets.base());

    Assert.assertEquals(1, report.getFileResultsCount());
    Assert.assertEquals(
        ChangeType.REMOVAL,
        report.getFileResultsOrThrow("test/v1/extra.proto").getChange().getChangeType());

    Assert.assertEquals(1, report.getMessageResultsCount());
    Assert.assertEquals(
        ChangeType.REMOVAL,
        report.getMessageResultsOrThrow("test.v1.ProtoExtraMessage").getChange().getChangeType());
  }

  private FieldResult diff(ProtoDomain dRef, ProtoDomain dNew) throws IOException {
    ValidationResults results = new ValidationResults();
    ProtoDiff diff = new ProtoDiff(dRef, dNew, results);
    diff.diffOnMessage("test.v1.ProtoBeamBasicMessage");

    Report result = results.createProto();
    System.out.println(result);
    return result.getMessageResultsMap().get("test.v1.ProtoBeamBasicMessage").getFieldResults(0);
  }

  private Report diffOnPackage(ProtoDomain dRef, ProtoDomain dNew) throws IOException {
    ValidationResults results = new ValidationResults();
    ProtoDiff diff = new ProtoDiff(dRef, dNew, results);
    diff.diffOnPackagePrefix("test.v1");

    return results.createProto();
  }
}
