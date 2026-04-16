package com.bi.etl;

import com.bi.exceptions.ETLProcessException;
import com.bi.interfaces.IETLService;
import com.bi.util.Dataset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ETLServiceImpl implements IETLService to provide the Extract-Transform-Load pipeline.
 * This class processes raw datasets through extraction, transformation, and loading stages.
 */
public class ETLServiceImpl implements IETLService {
    private List<Dataset> processedDatasets;
    private int processedDataId;
    private String status;
    private LocalDateTime lastProcessingDate;

    /**
     * Constructs an ETLServiceImpl with default state.
     */
    public ETLServiceImpl() {
        this.processedDatasets = new ArrayList<>();
        this.processedDataId = 0;
        this.status = "IDLE";
        this.lastProcessingDate = null;
    }

    /**
     * Extracts data from a raw Dataset: validates, tags, and prepares it for transformation.
     * 
     * @param rawData the raw dataset to extract from
     * @return a new Dataset marked as extracted
     * @throws ETLProcessException if rawData or its data content is null
     */
    @Override
    public Dataset extract(Dataset rawData) {
        if (rawData == null) {
            throw new ETLProcessException("Raw data cannot be null for extraction.");
        }
        if (rawData.getData() == null) {
            throw new ETLProcessException("Raw dataset contains no data.");
        }
        System.out.println("[ETL] Extracting data from dataset: " + rawData.getId());
        status = "EXTRACTING";

        Dataset extracted = new Dataset(
            "EXTRACTED-" + rawData.getId(),
            rawData.getData(),
            "Extracted: " + rawData.getDescription()
        );
        System.out.println("[ETL] Extraction complete.");
        return extracted;
    }

    /**
     * Transforms a dataset by adding metadata to each record.
     * Adds "processed": true and "processedAt": timestamp to each record.
     * 
     * @param data the dataset to transform
     * @return a new Dataset with transformed records
     * @throws ETLProcessException if data is null or contains non-List data
     */
    @Override
    public Dataset transform(Dataset data) {
        if (data == null) {
            throw new ETLProcessException("Dataset cannot be null for transformation.");
        }
        System.out.println("[ETL] Transforming dataset: " + data.getId());
        status = "TRANSFORMING";

        List<Map<String, Object>> transformedRecords = new ArrayList<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> originalRecords = (List<Map<String, Object>>) data.getData();
            for (Map<String, Object> record : originalRecords) {
                Map<String, Object> transformedRecord = new HashMap<>(record);
                transformedRecord.put("processed", true);
                transformedRecord.put("processedAt", LocalDateTime.now().toString());
                transformedRecords.add(transformedRecord);
            }
        } catch (ClassCastException e) {
            throw new ETLProcessException("Dataset contains non-list data. Cannot transform.", e);
        }

        Dataset transformed = new Dataset(
            "TRANSFORMED-" + data.getId(),
            transformedRecords,
            "Transformed: " + data.getDescription()
        );
        System.out.println("[ETL] Transformation complete. " + transformedRecords.size() + " records transformed.");
        return transformed;
    }

    /**
     * Loads a transformed dataset into the processed datasets store.
     * 
     * @param data the dataset to load
     * @throws ETLProcessException if data is null
     */
    @Override
    public void load(Dataset data) {
        if (data == null) {
            throw new ETLProcessException("Cannot load null dataset.");
        }
        System.out.println("[ETL] Loading dataset: " + data.getId() + " into processed store.");
        status = "LOADING";
        processedDataId++;
        lastProcessingDate = LocalDateTime.now();
        processedDatasets.add(data);
        status = "COMPLETE";
        System.out.println("[ETL] Load complete. Total processed datasets: " + processedDatasets.size());
    }

    /**
     * Runs the full ETL pipeline: extract → transform → load.
     * If any step fails, wraps the exception in a new ETLProcessException.
     * 
     * @param rawData the raw dataset to process
     * @return the final transformed dataset
     * @throws ETLProcessException if any stage of the pipeline fails
     */
    public Dataset runFullPipeline(Dataset rawData) {
        try {
            Dataset extracted = extract(rawData);
            Dataset transformed = transform(extracted);
            load(transformed);
            System.out.println("[ETL] Full pipeline complete for: " + rawData.getId());
            return transformed;
        } catch (ETLProcessException e) {
            throw new ETLProcessException("ETL pipeline failed: " + e.getMessage(), e);
        }
    }

    // Getters for access to ETL state

    /**
     * Returns the list of all processed datasets.
     */
    public List<Dataset> getProcessedDatasets() {
        return processedDatasets;
    }

    /**
     * Returns the current status of the ETL service.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the timestamp of the last processing event.
     */
    public LocalDateTime getLastProcessingDate() {
        return lastProcessingDate;
    }

    /**
     * Returns the count of datasets processed.
     */
    public int getProcessedDataId() {
        return processedDataId;
    }
}
