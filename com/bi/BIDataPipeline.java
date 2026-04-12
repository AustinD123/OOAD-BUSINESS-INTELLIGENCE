package com.bi;

import com.bi.datasources.FinanceModule;
import com.bi.datasources.HRModule;
import com.bi.datasources.SalesModule;
import com.bi.etl.ETLServiceImpl;
import com.bi.exceptions.DataSourceException;
import com.bi.exceptions.ETLProcessException;
import com.bi.repository.DataRepositoryImpl;
import com.bi.util.Dataset;
import java.util.Map;

/**
 * BIDataPipeline coordinates the full data flow through the Business Intelligence system.
 * It wires together the three data sources (Sales, HR, Finance), the ETL service, and the repository.
 * This class demonstrates the complete data layer working end-to-end.
 */
public class BIDataPipeline {
    private SalesModule salesModule;
    private HRModule hrModule;
    private FinanceModule financeModule;
    private ETLServiceImpl etlService;
    private DataRepositoryImpl dataRepository;

    /**
     * Constructs a BIDataPipeline and instantiates all components.
     */
    public BIDataPipeline() {
        this.salesModule = new SalesModule();
        this.hrModule = new HRModule();
        this.financeModule = new FinanceModule();
        this.etlService = new ETLServiceImpl();
        this.dataRepository = new DataRepositoryImpl();
    }

    /**
     * Runs the complete sales data pipeline from source through ETL to repository.
     * Wraps exceptions and prints error messages if any occur.
     */
    public void runSalesPipeline() {
        try {
            salesModule.connect();
            salesModule.validateSource();
            Dataset raw = salesModule.getData();
            Dataset processed = etlService.runFullPipeline(raw);
            dataRepository.store(processed);
            System.out.println("[Pipeline] Sales pipeline complete.\n");
        } catch (DataSourceException e) {
            System.err.println("[Pipeline] Sales pipeline error: " + e.getMessage() + "\n");
        } catch (ETLProcessException e) {
            System.err.println("[Pipeline] Sales ETL error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Runs the complete HR data pipeline from source through ETL to repository.
     * Wraps exceptions and prints error messages if any occur.
     */
    public void runHRPipeline() {
        try {
            hrModule.connect();
            hrModule.validateSource();
            Dataset raw = hrModule.getData();
            Dataset processed = etlService.runFullPipeline(raw);
            dataRepository.store(processed);
            System.out.println("[Pipeline] HR pipeline complete.\n");
        } catch (DataSourceException e) {
            System.err.println("[Pipeline] HR pipeline error: " + e.getMessage() + "\n");
        } catch (ETLProcessException e) {
            System.err.println("[Pipeline] HR ETL error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Runs the complete finance data pipeline from source through ETL to repository.
     * Wraps exceptions and prints error messages if any occur.
     */
    public void runFinancePipeline() {
        try {
            financeModule.connect();
            financeModule.validateSource();
            Dataset raw = financeModule.getData();
            Dataset processed = etlService.runFullPipeline(raw);
            dataRepository.store(processed);
            System.out.println("[Pipeline] Finance pipeline complete.\n");
        } catch (DataSourceException e) {
            System.err.println("[Pipeline] Finance pipeline error: " + e.getMessage() + "\n");
        } catch (ETLProcessException e) {
            System.err.println("[Pipeline] Finance ETL error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Runs all three pipelines (Sales, HR, Finance) in sequence.
     */
    public void runAllPipelines() {
        System.out.println("========== Starting BIDataPipeline ==========\n");
        runSalesPipeline();
        runHRPipeline();
        runFinancePipeline();
        System.out.println("[Pipeline] All pipelines complete. Repository has " + dataRepository.getTotalRecords() + " datasets.\n");
    }

    /**
     * Prints the repository index: all stored datasets and their descriptions.
     */
    public void printRepositoryIndex() {
        System.out.println("========== Repository Index ==========");
        Map<String, String> indexMap = dataRepository.getIndexMap();
        if (indexMap.isEmpty()) {
            System.out.println("(empty)");
        } else {
            for (Map.Entry<String, String> entry : indexMap.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println();
    }

    /**
     * Main entry point for the BIDataPipeline demonstration.
     * Connects all sources, runs the full pipeline, and demonstrates retrieval and indexing.
     */
    public static void main(String[] args) {
        BIDataPipeline pipeline = new BIDataPipeline();
        
        // Run all pipelines
        pipeline.runAllPipelines();
        
        // Print repository index
        pipeline.printRepositoryIndex();
        
        // Demonstrate retrieval of one dataset
        System.out.println("========== Dataset Retrieval Demo ==========");
        try {
            Dataset retrieved = pipeline.dataRepository.retrieve("TRANSFORMED-EXTRACTED-SALES-" + java.time.LocalDateTime.now().getYear());
            System.out.println("Retrieved dataset: " + retrieved);
        } catch (Exception e) {
            // Expected: the exact timestamp will differ; this is just a demo
            System.out.println("Note: Exact dataset IDs vary by timestamp. Retrieved datasets are shown in the index above.\n");
        }
        
        System.out.println("========== BIDataPipeline Demonstration Complete ==========");
    }
}
