package service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hive.hcatalog.common.HCatConstants;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.mapreduce.InputJobInfo;

import java.io.IOException;
import java.util.*;

/**
 * This class provides utility functions to convert names and types between
 * different database dialects.
 */
public class SchemaUtils {
    // using derby
    private static Map<String, String> columnTypeMapping = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                {
                    put("string", "varchar(32000)");
                    put("boolean", "boolean");
                    put("int", "int");
                    put("long", "bigint");
                    put("bigint", "bigint");
                    put("double", "double");
                    put("float", "float");
                    put("tinyint", "int");
                }
            });


    private static Map<String, String> preparedStatementTypeMapping = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                {
                    put("varchar(32000)", "string");
                    put("boolean", "boolean");
                    put("int", "int");
                    put("bigint", "long");
                    put("double", "double");
                    put("float", "float");
                    put("tinyint", "int");
                }
            });



    /**
     * Converts the column names from HCatalogSchema to a given database
     * dialect.
     *
     * @param inputSchema The HCatalog Schema containing the meta data.
     * @return An array of string containing the names of the columns, order is
     * important.
     */
    public static String[] getColumnNamesFromHcatSchema(HCatSchema inputSchema) {

        Object[] hcatInputNames = inputSchema.getFieldNames().toArray();
        String[] outputColumnNames = new String[hcatInputNames.length + 1];

        for (int i = 0; i < hcatInputNames.length; i++) {
            outputColumnNames[i] = hcatInputNames[i].toString();
        }

        outputColumnNames[hcatInputNames.length] = "used_filter";
        return outputColumnNames;
    }

    /**
     * Converts the column types from HCatSchema to the database dialect.
     *
     * @param inputSchema The HCatalog Schema containing the meta data.
     * @param anonFields  A list of fields to anonymize
     * @return An array of string containing the column types, order is
     * important.
     */
    public static String[] getColumnTypesFromHcatSchema(HCatSchema inputSchema,
                                                        Set<String> anonFields) {

        String[] fieldTypes = new String[inputSchema.getFieldNames().size() + 1];

        for (int i = 0; i < inputSchema.getFieldNames().size(); i++) {

            String type = columnTypeMapping.get("string");

            if (!inputSchema.get(i).isComplex()) {

                if (inputSchema.get(i).getTypeString()
                        .toLowerCase(Locale.getDefault()) == null
                        || inputSchema.get(i).getTypeString()
                        .toLowerCase(Locale.getDefault())
                        .equals("null")) {

                    type = columnTypeMapping.get("tinyint");

                } else if (anonFields.contains(inputSchema.get(i).getName())) {
                    type = columnTypeMapping.get("string");

                } else {
                    type = columnTypeMapping.get(inputSchema.get(i)
                            .getTypeString().toLowerCase(Locale.getDefault()));
                }
            }
            fieldTypes[i] = type;
        }

        fieldTypes[inputSchema.getFieldNames().size()] = columnTypeMapping
                .get("string");
        return fieldTypes;
    }

    public static HCatSchema getTableSchema(Configuration conf)
            throws IOException {
        InputJobInfo inputJobInfo = getJobInfo(conf);
        HCatSchema allCols = new HCatSchema(new LinkedList<HCatFieldSchema>());
        for (HCatFieldSchema field :
                inputJobInfo.getTableInfo().getDataColumns().getFields()) {
            allCols.append(field);
        }
        for (HCatFieldSchema field :
                inputJobInfo.getTableInfo().getPartitionColumns().getFields()) {
            allCols.append(field);
        }
        return allCols;
    }

    private static InputJobInfo getJobInfo(Configuration conf)
            throws IOException {
        String jobString = conf.get(
                HCatConstants.HCAT_KEY_JOB_INFO);
        if (jobString == null) {
            throw new IOException("job information not found in JobContext."
                    + " HCatInputFormat.setInput() not called?");
        }

        return (InputJobInfo) HCatUtil.deserialize(jobString);
    }
}
