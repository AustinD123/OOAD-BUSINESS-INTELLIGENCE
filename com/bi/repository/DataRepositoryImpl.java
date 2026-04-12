package com.bi.repository;

import com.bi.exceptions.DataNotFoundException;
import com.bi.interfaces.IDataRepository;
import com.bi.util.Dataset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataRepositoryImpl implements IDataRepository to provide an in-memory data store.
 * This class manages the storage, retrieval, and indexing of processed datasets.
 */
public class DataRepositoryImpl implements IDataRepository {
    private Map<String, Dataset> dataStore;
    private Map<String, String> indexMap;
    private int recordId;

    /**
     * Constructs a DataRepositoryImpl with default empty storage structures.
     */
    public DataRepositoryImpl() {
        this.dataStore = new HashMap<>();
        this.indexMap = new HashMap<>();
        this.recordId = 0;
    }

    /**
     * Stores a dataset in the repository.
     * 
     * @param data the dataset to store
     * @throws DataNotFoundException if data is null, has no ID, or already exists
     */
    @Override
    public void store(Dataset data) {
        if (data == null) {
            throw new DataNotFoundException("Cannot store null dataset.");
        }
        if (data.getId() == null || data.getId().isEmpty()) {
            throw new DataNotFoundException("Dataset must have a valid ID to be stored.");
        }
        if (dataStore.containsKey(data.getId())) {
            throw new DataNotFoundException("Dataset with ID " + data.getId() + " already exists.");
        }
        recordId++;
        dataStore.put(data.getId(), data);
        indexMap.put(data.getId(), data.getDescription());
        System.out.println("[Repository] Stored dataset: " + data.getId() + " | Total records: " + dataStore.size());
    }

    /**
     * Retrieves a dataset from the repository by ID.
     * 
     * @param id the ID of the dataset to retrieve
     * @return the retrieved dataset
     * @throws DataNotFoundException if ID is null/empty or dataset not found
     */
    @Override
    public Dataset retrieve(String id) {
        if (id == null || id.isEmpty()) {
            throw new DataNotFoundException("Retrieve ID cannot be null or empty.");
        }
        if (!dataStore.containsKey(id)) {
            throw new DataNotFoundException("No dataset found with ID: " + id);
        }
        System.out.println("[Repository] Retrieved dataset: " + id);
        return dataStore.get(id);
    }

    /**
     * Returns an index of all stored datasets.
     * 
     * @return a list containing all stored datasets
     */
    @Override
    public List<Dataset> index() {
        if (dataStore.isEmpty()) {
            return new ArrayList<>();
        }
        System.out.println("[Repository] Indexing. Total datasets: " + dataStore.size());
        return new ArrayList<>(dataStore.values());
    }

    /**
     * Checks if a dataset with a given ID exists in the repository.
     * 
     * @param id the dataset ID to check
     * @return true if the dataset exists, false otherwise
     */
    public boolean exists(String id) {
        return dataStore.containsKey(id);
    }

    /**
     * Returns the total number of records (datasets) in the repository.
     * 
     * @return the count of stored datasets
     */
    public int getTotalRecords() {
        return dataStore.size();
    }

    /**
     * Clears all datasets and indices from the repository.
     */
    public void clearAll() {
        dataStore.clear();
        indexMap.clear();
        recordId = 0;
        System.out.println("[Repository] All records cleared.");
    }

    /**
     * Returns the index map of all stored datasets.
     * Maps dataset ID to description.
     * 
     * @return the index map
     */
    public Map<String, String> getIndexMap() {
        return indexMap;
    }
}
