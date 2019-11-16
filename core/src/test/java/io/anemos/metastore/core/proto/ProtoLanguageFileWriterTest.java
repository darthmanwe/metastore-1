package io.anemos.metastore.core.proto;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import test.v1.Option;

@RunWith(JUnit4.class)
public class ProtoLanguageFileWriterTest {

  private static final Option.TestOption TEST_MINIMAL =
      Option.TestOption.newBuilder()
          .setSingleString("minimal")
          .addRepeatedString("test1")
          .addRepeatedString("test2")
          .setSingleInt32(2)
          .addRepeatedInt32(3)
          .setSingleEnum(Option.TestOption.TestEnum.ENUM2)
          .build();

  private static final Option.TestOption TEST_OPTION =
      Option.TestOption.newBuilder()
          .setSingleString("testString")
          .addRepeatedString("test1")
          .addRepeatedString("test2")
          .setSingleInt32(2)
          .addRepeatedInt32(3)
          .addRepeatedInt32(4)
          .setSingleInt64(10)
          //              .setSingleBytes(ByteString.copyFrom(new byte[] { 0x00, 0x01, 0x02 }))
          .setSingleEnum(Option.TestOption.TestEnum.ENUM2)
          .setSingleMessage(TEST_MINIMAL)
          .build();

  private static final List<String> STING_LIST = new ArrayList<>();

  static {
    STING_LIST.add("Value I");
    STING_LIST.add("Value II");
    STING_LIST.add("Value III");
  }

  private static final DescriptorProtos.FileOptions FILE_OPTIONS =
      DescriptorProtos.FileOptions.newBuilder()
          .setExtension(Option.fileOption, TEST_OPTION)
          .setExtension(Option.fileOption1, 12)
          .setExtension(Option.fileOption2, "String")
          .setExtension(Option.fileOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.MessageOptions MESSAGE_OPTIONS =
      DescriptorProtos.MessageOptions.newBuilder()
          .setExtension(Option.messageOption, TEST_OPTION)
          .setExtension(Option.messageOption1, 12)
          .setExtension(Option.messageOption2, "String")
          .setExtension(Option.messageOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.FieldOptions FIELD_OPTIONS =
      DescriptorProtos.FieldOptions.newBuilder()
          .setExtension(Option.fieldOption, TEST_OPTION)
          .setExtension(Option.fieldOption1, 12)
          .setExtension(Option.fieldOption2, "String")
          .setExtension(Option.fieldOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.ServiceOptions SERVICE_OPTIONS =
      DescriptorProtos.ServiceOptions.newBuilder()
          .setExtension(Option.serviceOption, TEST_OPTION)
          .setExtension(Option.serviceOption1, 12)
          .setExtension(Option.serviceOption2, "String")
          .setExtension(Option.serviceOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.MethodOptions METHOD_OPTIONS =
      DescriptorProtos.MethodOptions.newBuilder()
          .setExtension(Option.methodOption, TEST_OPTION)
          .setExtension(Option.methodOption1, 12)
          .setExtension(Option.methodOption2, "String")
          .setExtension(Option.methodOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.EnumOptions ENUM_OPTIONS =
      DescriptorProtos.EnumOptions.newBuilder()
          .setExtension(Option.enumOption, TEST_OPTION)
          .setExtension(Option.enumOption1, 12)
          .setExtension(Option.enumOption2, "String")
          .setExtension(Option.enumOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private static final DescriptorProtos.EnumValueOptions ENUM_VALUE_OPTIONS =
      DescriptorProtos.EnumValueOptions.newBuilder()
          .setExtension(Option.enumValueOption, TEST_OPTION)
          .setExtension(Option.enumValueOption1, 12)
          .setExtension(Option.enumValueOption2, "String")
          .setExtension(Option.enumValueOptionN, STING_LIST)
          .setDeprecated(true)
          .build();

  private void testOutput(
      DescriptorProtos.FileDescriptorProto protoBuilder, PContainer container, String expected)
      throws Descriptors.DescriptorValidationException {
    Descriptors.FileDescriptor[] dependencies = new Descriptors.FileDescriptor[1];
    dependencies[0] = Option.getDescriptor();
    Descriptors.FileDescriptor fileDescriptor =
        Descriptors.FileDescriptor.buildFrom(protoBuilder, dependencies);

    testOutput(fileDescriptor, container, expected);
  }

  private void testOutput(
      Descriptors.FileDescriptor fileDescriptor, PContainer container, String expected)
      throws Descriptors.DescriptorValidationException {
    // expected = expected + "\n// test";
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ProtoLanguageFileWriter.write(fileDescriptor, container, outputStream);
    Assert.assertEquals(expected, outputStream.toString());
  }

  @Test
  public void noPackageSetTest() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder().setName("test").setSyntax("proto3");

    DescriptorProtos.DescriptorProto.Builder descriptor =
        DescriptorProtos.DescriptorProto.newBuilder();
    descriptor.setName("TestMessage");
    fileDescriptorProtoBuilder.addMessageType(descriptor);

    testOutput(
        fileDescriptorProtoBuilder.build(),
        null,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "message TestMessage {\n"
            + "\n"
            + "}\n");
  }

  @Test
  public void extensionTest() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName("test")
            .setSyntax("proto3")
            .addDependency("google/protobuf/descriptor.proto");

    DescriptorProtos.FieldDescriptorProto extensionField =
        DescriptorProtos.FieldDescriptorProto.newBuilder()
            .setName("test_extension")
            .setNumber(66666700)
            .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
            .setExtendee(".google.protobuf.FileOptions")
            .build();

    fileDescriptorProtoBuilder.addExtension(extensionField);

    Descriptors.FileDescriptor[] dependencies = new Descriptors.FileDescriptor[1];
    dependencies[0] = DescriptorProtos.getDescriptor();
    Descriptors.FileDescriptor fileDescriptor =
        Descriptors.FileDescriptor.buildFrom(fileDescriptorProtoBuilder.build(), dependencies);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ProtoLanguageFileWriter.write(fileDescriptor, outputStream);

    String expected =
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"google/protobuf/descriptor.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "extend google.protobuf.FileOptions {\n"
            + "\tstring test_extension = 66666700;\n"
            + "}\n"
            + "\n";
    Assert.assertEquals(expected, outputStream.toString());
  }

  private void assertMessage(DescriptorProtos.FileDescriptorProto proto, PContainer domain)
      throws Descriptors.DescriptorValidationException {
    testOutput(
        proto,
        domain,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "message Proto3Message {\n"
            + "\toption deprecated = true;\n"
            + "\toption (test.v1.message_option) = {\n"
            + "\t\tsingle_string: \"testString\"\n"
            + "\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\tsingle_int32: 2\n"
            + "\t\trepeated_int32: [3,4]\n"
            + "\t\tsingle_int64: 10\n"
            + "\t\tsingle_enum: ENUM2\n"
            + "\t\tsingle_message: {\n"
            + "\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3]\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t}\n"
            + "\t};\n"
            + "\toption (test.v1.message_option_1) = 12;\n"
            + "\toption (test.v1.message_option_2) = \"String\";\n"
            + "\toption (test.v1.message_option_n) = \"Value I\";\n"
            + "\toption (test.v1.message_option_n) = \"Value II\";\n"
            + "\toption (test.v1.message_option_n) = \"Value III\";\n"
            + "\n"
            + "\tbool field_1 = 1 [\n"
            + "\t\tdeprecated = true,\n"
            + "\t\t(test.v1.field_option) = {\n"
            + "\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\tsingle_int64: 10\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\tsingle_message: {\n"
            + "\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t}\n"
            + "\t\t},\n"
            + "\t\t(test.v1.field_option_1) = 12,\n"
            + "\t\t(test.v1.field_option_2) = \"String\",\n"
            + "\t\t(test.v1.field_option_n) = \"Value I\",\n"
            + "\t\t(test.v1.field_option_n) = \"Value II\",\n"
            + "\t\t(test.v1.field_option_n) = \"Value III\"\n"
            + "\t];\n"
            + "}\n");
  }

  @Test
  public void writeMessageFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions();
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_message.proto");

    assertMessage(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writeMessage() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder().setName("test").setSyntax("proto3");

    DescriptorProtos.DescriptorProto.Builder descriptor =
        DescriptorProtos.DescriptorProto.newBuilder()
            .setName("Proto3Message")
            .setOptions(MESSAGE_OPTIONS)
            .addField(
                DescriptorProtos.FieldDescriptorProto.newBuilder()
                    .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL)
                    .setNumber(1)
                    .setName("field_1")
                    .setOptions(FIELD_OPTIONS)
                    .build());

    fileDescriptorProtoBuilder.addMessageType(descriptor);
    assertMessage(fileDescriptorProtoBuilder.build(), null);
  }

  private void assertNested(DescriptorProtos.FileDescriptorProto proto, PContainer domain)
      throws Descriptors.DescriptorValidationException {
    testOutput(
        proto,
        domain,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "message Proto3Nested {\n"
            + "\n"
            + "\tmessage Level1 {\n"
            + "\n"
            + "\t\tmessage Level2A {\n"
            + "\n"
            + "\t\t\tbool field_2A_1 = 1 [\n"
            + "\t\t\t\tdeprecated = true,\n"
            + "\t\t\t\t(test.v1.field_option) = {\n"
            + "\t\t\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\t\t\tsingle_int64: 10\n"
            + "\t\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\t\tsingle_message: {\n"
            + "\t\t\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\t\t}\n"
            + "\t\t\t\t},\n"
            + "\t\t\t\t(test.v1.field_option_1) = 12,\n"
            + "\t\t\t\t(test.v1.field_option_2) = \"String\",\n"
            + "\t\t\t\t(test.v1.field_option_n) = \"Value I\",\n"
            + "\t\t\t\t(test.v1.field_option_n) = \"Value II\",\n"
            + "\t\t\t\t(test.v1.field_option_n) = \"Value III\"\n"
            + "\t\t\t];\n"
            + "\t\t}\n"
            + "\n"
            + "\t\tmessage Level2B {\n"
            + "\t\t\toption deprecated = true;\n"
            + "\t\t\toption (test.v1.message_option) = {\n"
            + "\t\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\t\tsingle_int64: 10\n"
            + "\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\tsingle_message: {\n"
            + "\t\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\t}\n"
            + "\t\t\t};\n"
            + "\t\t\toption (test.v1.message_option_1) = 12;\n"
            + "\t\t\toption (test.v1.message_option_2) = \"String\";\n"
            + "\t\t\toption (test.v1.message_option_n) = \"Value I\";\n"
            + "\t\t\toption (test.v1.message_option_n) = \"Value II\";\n"
            + "\t\t\toption (test.v1.message_option_n) = \"Value III\";\n"
            + "\n"
            + "\t\t\tbool field_2B_1 = 1;\n"
            + "\t\t}\n"
            + "\n"
            + "\t\tmessage Level2C {\n"
            + "\n"
            + "\t\t\tenum ELevel2C {\n"
            + "\n"
            + "\t\t\t\tELEVEL2C_ENUM_UNSET = 0 [\n"
            + "\t\t\t\t\tdeprecated = true,\n"
            + "\t\t\t\t\t(test.v1.enum_value_option) = {\n"
            + "\t\t\t\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\t\t\t\tsingle_int64: 10\n"
            + "\t\t\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\t\t\tsingle_message: {\n"
            + "\t\t\t\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t\t\t\t}\n"
            + "\t\t\t\t\t},\n"
            + "\t\t\t\t\t(test.v1.enum_value_option_1) = 12,\n"
            + "\t\t\t\t\t(test.v1.enum_value_option_2) = \"String\",\n"
            + "\t\t\t\t\t(test.v1.enum_value_option_n) = \"Value I\",\n"
            + "\t\t\t\t\t(test.v1.enum_value_option_n) = \"Value II\",\n"
            + "\t\t\t\t\t(test.v1.enum_value_option_n) = \"Value III\"\n"
            + "\t\t\t\t];\n"
            + "\t\t\t}\n"
            + "\n"
            + "\t\t\tstring field_2C_1 = 1;\n"
            + "\t\t}\n"
            + "\n"
            + "\t}\n"
            + "\n"
            + "}\n");
  }

  @Test
  public void writeNestedFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions();
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_nested.proto");

    assertNested(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writNested() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder().setName("test").setSyntax("proto3");

    DescriptorProtos.DescriptorProto.Builder descriptor =
        DescriptorProtos.DescriptorProto.newBuilder()
            .setName("Proto3Nested")
            .addNestedType(
                DescriptorProtos.DescriptorProto.newBuilder()
                    .setName("Level1")
                    .addNestedType(
                        DescriptorProtos.DescriptorProto.newBuilder()
                            .setName("Level2A")
                            .addField(
                                DescriptorProtos.FieldDescriptorProto.newBuilder()
                                    .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL)
                                    .setNumber(1)
                                    .setName("field_2A_1")
                                    .setOptions(FIELD_OPTIONS)
                                    .build())
                            .build())
                    .addNestedType(
                        DescriptorProtos.DescriptorProto.newBuilder()
                            .setName("Level2B")
                            .setOptions(MESSAGE_OPTIONS)
                            .addField(
                                DescriptorProtos.FieldDescriptorProto.newBuilder()
                                    .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL)
                                    .setNumber(1)
                                    .setName("field_2B_1")
                                    .build())
                            .build())
                    .addNestedType(
                        DescriptorProtos.DescriptorProto.newBuilder()
                            .setName("Level2C")
                            .addEnumType(
                                DescriptorProtos.EnumDescriptorProto.newBuilder()
                                    .setName("ELevel2C")
                                    .addValue(
                                        DescriptorProtos.EnumValueDescriptorProto.newBuilder()
                                            .setNumber(0)
                                            .setName("ELEVEL2C_ENUM_UNSET")
                                            .setOptions(ENUM_VALUE_OPTIONS)
                                            .build())
                                    .build())
                            .addField(
                                DescriptorProtos.FieldDescriptorProto.newBuilder()
                                    .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                                    .setNumber(1)
                                    .setName("field_2C_1")
                                    .build())
                            .build()));

    fileDescriptorProtoBuilder.addMessageType(descriptor);
    assertNested(fileDescriptorProtoBuilder.build(), null);
  }

  private void assertService(DescriptorProtos.FileDescriptorProto proto, PContainer domain)
      throws Descriptors.DescriptorValidationException {
    testOutput(
        proto,
        domain,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "service Service {\n"
            + "\toption deprecated = true;\n"
            + "\toption (test.v1.service_option) = {\n"
            + "\t\tsingle_string: \"testString\"\n"
            + "\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\tsingle_int32: 2\n"
            + "\t\trepeated_int32: [3,4]\n"
            + "\t\tsingle_int64: 10\n"
            + "\t\tsingle_enum: ENUM2\n"
            + "\t\tsingle_message: {\n"
            + "\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3]\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t}\n"
            + "\t};\n"
            + "\toption (test.v1.service_option_1) = 12;\n"
            + "\toption (test.v1.service_option_2) = \"String\";\n"
            + "\toption (test.v1.service_option_n) = \"Value I\";\n"
            + "\toption (test.v1.service_option_n) = \"Value II\";\n"
            + "\toption (test.v1.service_option_n) = \"Value III\";\n"
            + "\n"
            + "\trpc FirstMethod(MethodRequest) returns (MethodResponse) {}\n"
            + "\trpc ClientStreamingMethod(stream MethodRequest) returns (MethodResponse) {}\n"
            + "\trpc ServerStreamingMethod(MethodRequest) returns (stream MethodResponse) {\n"
            + "\t\toption (test.v1.method_option) = {\n"
            + "\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3]\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t};\n"
            + "\n"
            + "\t}\n"
            + "\trpc BiStreamingMethod(stream MethodRequest) returns (stream MethodResponse) {\n"
            + "\t\toption deprecated = true;\n"
            + "\t\toption (test.v1.method_option) = {\n"
            + "\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\tsingle_int64: 10\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\tsingle_message: {\n"
            + "\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t}\n"
            + "\t\t};\n"
            + "\t\toption (test.v1.method_option_1) = 12;\n"
            + "\t\toption (test.v1.method_option_2) = \"String\";\n"
            + "\t\toption (test.v1.method_option_n) = \"Value I\";\n"
            + "\t\toption (test.v1.method_option_n) = \"Value II\";\n"
            + "\t\toption (test.v1.method_option_n) = \"Value III\";\n"
            + "\n"
            + "\t}\n"
            + "}\n"
            + "\n"
            + "message MethodRequest {\n"
            + "\n"
            + "}\n"
            + "\n"
            + "message MethodResponse {\n"
            + "\n"
            + "}\n");
  }

  @Test
  public void writeServiceFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions();
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_service.proto");

    assertService(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writeService() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName("test")
            .setSyntax("proto3")
            .addDependency("google/protobuf/descriptor.proto");

    DescriptorProtos.DescriptorProto methodRequest =
        DescriptorProtos.DescriptorProto.newBuilder().setName("MethodRequest").build();

    DescriptorProtos.DescriptorProto methodResponse =
        DescriptorProtos.DescriptorProto.newBuilder().setName("MethodResponse").build();

    DescriptorProtos.ServiceDescriptorProto service =
        DescriptorProtos.ServiceDescriptorProto.newBuilder()
            .setName("Service")
            .addMethod(
                DescriptorProtos.MethodDescriptorProto.newBuilder()
                    .setName("FirstMethod")
                    .setInputType("MethodRequest")
                    .setOutputType("MethodResponse"))
            .addMethod(
                DescriptorProtos.MethodDescriptorProto.newBuilder()
                    .setName("ClientStreamingMethod")
                    .setInputType("MethodRequest")
                    .setOutputType("MethodResponse")
                    .setClientStreaming(true))
            .addMethod(
                DescriptorProtos.MethodDescriptorProto.newBuilder()
                    .setName("ServerStreamingMethod")
                    .setInputType("MethodRequest")
                    .setOutputType("MethodResponse")
                    .setServerStreaming(true)
                    .setOptions(
                        DescriptorProtos.MethodOptions.newBuilder()
                            .setExtension(Option.methodOption, TEST_MINIMAL)))
            .addMethod(
                DescriptorProtos.MethodDescriptorProto.newBuilder()
                    .setName("BiStreamingMethod")
                    .setInputType("MethodRequest")
                    .setOutputType("MethodResponse")
                    .setClientStreaming(true)
                    .setServerStreaming(true)
                    .setOptions(METHOD_OPTIONS))
            .setOptions(SERVICE_OPTIONS)
            .build();

    fileDescriptorProtoBuilder.addService(service);
    fileDescriptorProtoBuilder.addMessageType(methodRequest);
    fileDescriptorProtoBuilder.addMessageType(methodResponse);

    assertService(fileDescriptorProtoBuilder.build(), null);
  }

  private void assertEnum(DescriptorProtos.FileDescriptorProto proto, PContainer domain)
      throws Descriptors.DescriptorValidationException {
    testOutput(
        proto,
        domain,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "\n"
            + "\n"
            + "enum WriteEnum {\n"
            + "\toption deprecated = true;\n"
            + "\toption (test.v1.enum_option) = {\n"
            + "\t\tsingle_string: \"testString\"\n"
            + "\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\tsingle_int32: 2\n"
            + "\t\trepeated_int32: [3,4]\n"
            + "\t\tsingle_int64: 10\n"
            + "\t\tsingle_enum: ENUM2\n"
            + "\t\tsingle_message: {\n"
            + "\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3]\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t}\n"
            + "\t};\n"
            + "\toption (test.v1.enum_option_1) = 12;\n"
            + "\toption (test.v1.enum_option_2) = \"String\";\n"
            + "\toption (test.v1.enum_option_n) = \"Value I\";\n"
            + "\toption (test.v1.enum_option_n) = \"Value II\";\n"
            + "\toption (test.v1.enum_option_n) = \"Value III\";\n"
            + "\n"
            + "\tWRITE_ENUM_UNSET = 0 [\n"
            + "\t\tdeprecated = true,\n"
            + "\t\t(test.v1.enum_value_option) = {\n"
            + "\t\t\tsingle_string: \"testString\"\n"
            + "\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\tsingle_int32: 2\n"
            + "\t\t\trepeated_int32: [3,4]\n"
            + "\t\t\tsingle_int64: 10\n"
            + "\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\tsingle_message: {\n"
            + "\t\t\t\tsingle_string: \"minimal\"\n"
            + "\t\t\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\t\t\tsingle_int32: 2\n"
            + "\t\t\t\trepeated_int32: [3]\n"
            + "\t\t\t\tsingle_enum: ENUM2\n"
            + "\t\t\t}\n"
            + "\t\t},\n"
            + "\t\t(test.v1.enum_value_option_1) = 12,\n"
            + "\t\t(test.v1.enum_value_option_2) = \"String\",\n"
            + "\t\t(test.v1.enum_value_option_n) = \"Value I\",\n"
            + "\t\t(test.v1.enum_value_option_n) = \"Value II\",\n"
            + "\t\t(test.v1.enum_value_option_n) = \"Value III\"\n"
            + "\t];\n"
            + "}\n");
  }

  @Test
  public void writeEnumFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions();
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_enum.proto");

    assertEnum(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writeEnum() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName("test")
            .setSyntax("proto3")
            .addDependency("google/protobuf/descriptor.proto");

    DescriptorProtos.EnumDescriptorProto enumDescriptor =
        DescriptorProtos.EnumDescriptorProto.newBuilder()
            .setName("WriteEnum")
            .setOptions(ENUM_OPTIONS)
            .addValue(
                DescriptorProtos.EnumValueDescriptorProto.newBuilder()
                    .setNumber(0)
                    .setName("WRITE_ENUM_UNSET")
                    .setOptions(ENUM_VALUE_OPTIONS)
                    .build())
            .build();

    fileDescriptorProtoBuilder.addEnumType(enumDescriptor);

    assertEnum(fileDescriptorProtoBuilder.build(), null);
  }

  private void assertFile(DescriptorProtos.FileDescriptorProto proto, PContainer domain)
      throws Descriptors.DescriptorValidationException {
    testOutput(
        proto,
        domain,
        "syntax = \"proto3\";\n"
            + "\n"
            + "import \"test/v1/option.proto\";\n"
            + "\n"
            + "option deprecated = true;\n"
            + "option (test.v1.file_option) = {\n"
            + "\tsingle_string: \"testString\"\n"
            + "\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\tsingle_int32: 2\n"
            + "\trepeated_int32: [3,4]\n"
            + "\tsingle_int64: 10\n"
            + "\tsingle_enum: ENUM2\n"
            + "\tsingle_message: {\n"
            + "\t\tsingle_string: \"minimal\"\n"
            + "\t\trepeated_string: [\"test1\",\"test2\"]\n"
            + "\t\tsingle_int32: 2\n"
            + "\t\trepeated_int32: [3]\n"
            + "\t\tsingle_enum: ENUM2\n"
            + "\t}\n"
            + "};\n"
            + "option (test.v1.file_option_1) = 12;\n"
            + "option (test.v1.file_option_2) = \"String\";\n"
            + "option (test.v1.file_option_n) = \"Value I\";\n"
            + "option (test.v1.file_option_n) = \"Value II\";\n"
            + "option (test.v1.file_option_n) = \"Value III\";\n"
            + "\n"
            + "\n"
            + "enum Proto3FileEnum {\n"
            + "\n"
            + "\tPROTO3_FILE_ENUM_UNSET = 0;\n"
            + "}\n"
            + "\n"
            + "message Proto3FileMessage {\n"
            + "\n"
            + "}\n");
  }

  @Test
  public void writeFileFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions();
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_file.proto");

    assertFile(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writeFileFromFile() throws Exception {
    PContainer PContainer = TestSets.baseComplexMessageOptions().update(null);
    Descriptors.FileDescriptor fileDescriptor =
        PContainer.getFileDescriptorByFileName("test/v1/proto3_file.proto");

    assertFile(fileDescriptor.toProto(), PContainer);
  }

  @Test
  public void writeFile() throws Exception {
    DescriptorProtos.FileDescriptorProto.Builder fileDescriptorProtoBuilder =
        DescriptorProtos.FileDescriptorProto.newBuilder()
            .setName("test")
            .setSyntax("proto3")
            .addDependency("google/protobuf/descriptor.proto")
            .setOptions(FILE_OPTIONS);

    DescriptorProtos.EnumDescriptorProto enumDescriptor =
        DescriptorProtos.EnumDescriptorProto.newBuilder()
            .setName("Proto3FileEnum")
            .addValue(
                DescriptorProtos.EnumValueDescriptorProto.newBuilder()
                    .setNumber(0)
                    .setName("PROTO3_FILE_ENUM_UNSET")
                    .build())
            .build();

    DescriptorProtos.DescriptorProto.Builder descriptor =
        DescriptorProtos.DescriptorProto.newBuilder();
    descriptor.setName("Proto3FileMessage");

    fileDescriptorProtoBuilder.addEnumType(enumDescriptor);
    fileDescriptorProtoBuilder.addMessageType(descriptor);

    assertFile(fileDescriptorProtoBuilder.build(), null);
  }
}
